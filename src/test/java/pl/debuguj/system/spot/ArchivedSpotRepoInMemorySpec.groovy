package pl.debuguj.system.spot

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArchivedSpotRepoInMemorySpec extends Specification {

    @Subject
    private final ArchivedSpotRepo sut = new ArchivedSpotRepoInMemory()

    static LocalDateTime defBeginDateTime
    static LocalDateTime defEndDateTime
    static String defaultRegistrationNo
    @Shared
    static ArchivedSpot archivedSpot

    def setup() {
        defBeginDateTime = LocalDateTime.now()
        defEndDateTime = defBeginDateTime.plusHours(2L)
        defaultRegistrationNo = "WZE12345"
        archivedSpot = new ArchivedSpot(defaultRegistrationNo, DriverType.REGULAR, defBeginDateTime, defEndDateTime)
    }

    def "Should return empty optional because of null archived spot value"() {
        when: "Save #archivedSpot to repository"
        Optional<ArchivedSpot> opt = sut.save(null)

        then: "Should return empty optional"
        !opt.isPresent()
    }

    def "Should save new archived spot to repository"() {
        when: "Save #archivedSpot to repository"
        Optional<ArchivedSpot> opt = sut.save(archivedSpot)

        then: "Should return not empty optional"
        opt.isPresent()

        and: "Values should be correct"
        opt.ifPresent({ value ->
            archivedSpot.uuid == value.uuid
            archivedSpot.beginLocalDateTime == value.beginLocalDateTime
            archivedSpot.endLocalDateTime == value.endLocalDateTime
            archivedSpot.driverType == value.driverType
        })
    }

    def "Should find all items by date"() {
        given: "A start date for tests"
        LocalDate startDate = LocalDate
                .parse("2020-06-21", DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        and: "Created test values"
        createArchiveSpotsList(startDate).forEach(sut.&save)

        when: "Get values by date #startDate"
        List<ArchivedSpot> spotStream = sut.getAllByDay(startDate)

        then: "Found size of elements should be 2"
        spotStream.size() == 2

        when: "Check size one day after start date #startDate"
        spotStream = sut.getAllByDay(startDate.plusDays(1L))

        then: "Number of found items should be 3"
        spotStream.size() == 3

        when: "Check size of list 2 days after #startDate"
        spotStream = sut.getAllByDay(startDate.plusDays(2L))

        then: "Number of found item should be 0"
        spotStream.size() == 0
    }

    //TODO: update to groovy version
    private static List<ArchivedSpot> createArchiveSpotsList(LocalDate date) {
        List<ArchivedSpot> list = new ArrayList<>()

        LocalDateTime ldt = date.atStartOfDay()

        list.add(new ArchivedSpot("WWW66666", DriverType.REGULAR, ldt, ldt.plusHours(2L)))
        list.add(new ArchivedSpot("WSQ77777", DriverType.REGULAR, ldt, ldt.plusHours(3L)))
        list.add(new ArchivedSpot("QAZ88888", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(4L)))
        list.add(new ArchivedSpot("EDC99999", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(2L)))
        list.add(new ArchivedSpot("FDR99998", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(1L)))

        return list
    }
}
