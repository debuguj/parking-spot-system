package pl.debuguj.system.spot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.dao.InvalidDataAccessApiUsageException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

@DataJpaTest
class SpotRepoSpec extends Specification{

    @Subject @Autowired SpotRepo spotRepo

    @Shared Spot spot

    def setup() {
        spot = new Spot('WZE12345', DriverType.REGULAR, LocalDateTime.now())
    }

    def 'should return exception because of null value to save'() {
        when: "save null to repository"
        spotRepo.save(null)

        then: 'should return exception'
        thrown(InvalidDataAccessApiUsageException)
    }

    def 'should return value in optional because vehicle is saved'() {
        when: 'save spot to repository'
        Optional<Spot> opt = spotRepo.save(spot)

        then: 'should return value in optional'
        opt != Optional.empty()
    }

    def 'should delete active spot'() {
        when: 'save one spot'
        spotRepo.save(spot)

        and: 'try to find saved spot by vehicle plate'
        Optional<Spot> found = spotRepo.findByVehiclePlate('WZE12345')

        then: 'saved spot should be found'
        found?.isPresent()

        when: 'delete previous spot'
        spotRepo.deleteByVehiclePlate('WZE12345')

        and: 'try to find previously saved spot'
        Optional<Spot> notFound = spotRepo.findByVehiclePlate('WZE12345')

        then: 'should not be found'
        notFound == Optional.empty()
    }

    def 'should not find active parking space'() {
        when: 'try to find not persisted vehicle by plate'
        Optional<Spot> notFound = spotRepo.findByVehiclePlate('WZE12345')

        then: 'should be empty'
        notFound == Optional.empty()
    }
}
