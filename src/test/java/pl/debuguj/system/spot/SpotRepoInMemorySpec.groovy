package pl.debuguj.system.spot

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class SpotRepoInMemorySpec extends Specification {

    @Subject
    SpotRepo sut = new SpotRepoInMemory()

    @Shared
    String defaultVehiclePlate
    @Shared
    Spot spot

    def setup() {
        defaultVehiclePlate = 'WZE12345'
        spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, LocalDateTime.now())
    }

    def 'should return empty optional because of null spot value'() {
        when: "save #spot to repository"
        Optional<Spot> opt = sut.save(null)

        then: 'should return empty optional'
        opt == Optional.empty()
    }

    def 'should return empty optional because vehicle is active'() {
        when: 'saved one spot'
        sut.save(spot)

        and: 'save the same spot again'
        Optional<Spot> opt = sut.save(spot)

        then: 'should return empty optional'
        opt == Optional.empty()
    }

    def 'should return not empty optional because vehicle is not saved'() {
        when: 'save spot to repository'
        Optional<Spot> opt = sut.save(spot)

        then: 'should return value in optional'
        opt != Optional.empty()
    }

    def 'should delete active spot'() {
        when: 'save one spot'
        sut.save(spot)

        and: 'try to find saved spot by vehicle plate'
        Optional<Spot> found = sut.findVehicleByPlate(defaultVehiclePlate)

        then: 'saved spot should be found'
        found?.isPresent()

        when: 'delete previous spot'
        sut.delete(spot.getVehiclePlate())

        and: 'try find previously saved spot'
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultVehiclePlate)

        then: 'should not be found'
        notFound == Optional.empty()
    }

    def 'should not find active parking space'() {
        when: 'try to find unsaved vehicle by plate'
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultVehiclePlate)

        then: 'should not be find'
        notFound == Optional.empty()
    }
}
