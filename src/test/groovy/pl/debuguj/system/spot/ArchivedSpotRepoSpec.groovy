package pl.debuguj.system.spot


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.dao.InvalidDataAccessApiUsageException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class ArchivedSpotRepoSpec extends Specification{

    @Subject @Autowired ArchivedSpotRepo archivedSpotRepo

    @Shared LocalDateTime defBeginDateTime = LocalDateTime.now()
    @Shared LocalDateTime defEndDateTime = LocalDateTime.now().plusHours(2L)
    @Shared ArchivedSpot archivedSpot

    @Shared LocalDateTime startDate = LocalDate.parse('2020-06-25', DateTimeFormatter.ofPattern('yyyy-MM-dd')).atStartOfDay()

    def setupSpec() {
        archivedSpot = new ArchivedSpot('WZE12345', DriverType.REGULAR, defBeginDateTime, defEndDateTime)
    }

    def 'should return exception because of null value to save'() {
        when: 'save archived spot as null to repository'
        archivedSpotRepo.save(null)

        then: 'should return exception'
        thrown(InvalidDataAccessApiUsageException)
    }

    def 'should save new archived spot to repository'() {
        when: 'save archived spot to repository'
        Optional<ArchivedSpot> opt = archivedSpotRepo.save(archivedSpot)

        then: 'should return not empty optional'
        opt != Optional.empty()

        and: 'values should be correct'
        with(archivedSpot) {
            vehiclePlate == opt.get().vehiclePlate
            beginTimestamp== opt.get().beginTimestamp
            endTimestamp == opt.get().endTimestamp
        }
    }

    def 'should find all items by date'() {
        given: 'values loaded to sut'
        values.forEach(archivedSpotRepo.&save)

        when: "get values by date #startDate"
        def spotStream = archivedSpotRepo.findAllByBeginTimestamp(startDate)

        then: 'Size of elements should be equal 2'
        spotStream.size() == 2

        when: "Check size one day after start date #startDate"
        spotStream = archivedSpotRepo.findAllByBeginTimestamp(startDate.plusDays(1L))

        then: 'number of found items should be 3'
        spotStream.size() == 3

        when: "check size of list 2 days after #startDate"
        spotStream = archivedSpotRepo.findAllByBeginTimestamp(startDate.plusDays(2L))

        then: 'number of found items should be 0'
        spotStream.size() == 0

        where: 'items for test'
        values = [new ArchivedSpot("WWW66666", DriverType.REGULAR, startDate, startDate.plusHours(2L)),
                  new ArchivedSpot("WSQ77777", DriverType.REGULAR, startDate, startDate.plusHours(3L)),
                  new ArchivedSpot("QAZ88888", DriverType.REGULAR, startDate.plusDays(1L), startDate.plusDays(1L).plusHours(4L)),
                  new ArchivedSpot("EDC99999", DriverType.REGULAR, startDate.plusDays(1L), startDate.plusDays(1L).plusHours(2L)),
                  new ArchivedSpot("FDR99998", DriverType.REGULAR, startDate.plusDays(1L), startDate.plusDays(1L).plusHours(1L))]
    }

}
