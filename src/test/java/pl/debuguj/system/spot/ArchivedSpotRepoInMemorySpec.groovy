package pl.debuguj.system.spot

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArchivedSpotRepoInMemorySpec extends Specification {

    @Subject
    ArchivedSpotRepo sut = new ArchivedSpotRepoInMemory()

    @Shared
    LocalDateTime defBeginDateTime
    @Shared
    LocalDateTime defEndDateTime
    @Shared
    ArchivedSpot archivedSpot

    @Shared
    LocalDate startDate = LocalDate.parse("2020-06-21", DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    @Shared
    LocalDateTime ldt = startDate.atStartOfDay()

    def setup() {
        defBeginDateTime = LocalDateTime.now()
        defEndDateTime = defBeginDateTime.plusHours(2L)
        archivedSpot = new ArchivedSpot("WZE12345", DriverType.REGULAR, defBeginDateTime, defEndDateTime)
    }

    def "Should return empty optional because of null archived spot value"() {
        when: "Save #archivedSpot to repository"
        Optional<ArchivedSpot> opt = sut.save(null)

        then: "Should return empty optional"
        opt == Optional.empty()
    }

    def "Should save new archived spot to repository"() {
        when: "Save #archivedSpot to repository"
        Optional<ArchivedSpot> opt = sut.save(archivedSpot)

        then: "Should return not empty optional"
        opt != Optional.empty()

        and: "Values should be correct"
        with(archivedSpot) {
            uuid == opt.get().uuid
            beginLocalDateTime == opt.get().beginLocalDateTime
            endLocalDateTime == opt.get().endLocalDateTime
            driverType == opt.get().driverType
        }
    }

    def "Should find all items by date"() {
        given: "Values loaded to sut"
        values.forEach(sut.&save)

        when: "Get values by date #startDate"
        def spotStream = sut.getAllByDay(startDate)

        then: "Size of elements should be equal 2"
        spotStream.size() == 2

        when: "Check size one day after start date #startDate"
        spotStream = sut.getAllByDay(startDate.plusDays(1L))

        then: "The number of found items should be 3"
        spotStream.size() == 3

        when: "Check size of list 2 days after #startDate"
        spotStream = sut.getAllByDay(startDate.plusDays(2L))

        then: "The number of found items should be 0"
        spotStream.size() == 0

        where: "Items for test"
        values = [new ArchivedSpot("WWW66666", DriverType.REGULAR, ldt, ldt.plusHours(2L)),
                  new ArchivedSpot("WSQ77777", DriverType.REGULAR, ldt, ldt.plusHours(3L)),
                  new ArchivedSpot("QAZ88888", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(4L)),
                  new ArchivedSpot("EDC99999", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(2L)),
                  new ArchivedSpot("FDR99998", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(1L))]
    }
}
