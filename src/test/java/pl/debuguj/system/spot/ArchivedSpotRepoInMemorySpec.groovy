package pl.debuguj.system.spot

import pl.debuguj.system.driver.Fee
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ArchivedSpotRepoInMemorySpec extends Specification {

    @Subject
    @Shared
    ArchivedSpotRepo sut = new ArchivedSpotRepoInMemory()

    @Shared
    LocalDateTime defBeginDateTime
    @Shared
    LocalDateTime defEndDateTime
    @Shared
    ArchivedSpot archivedSpot

    @Shared
    LocalDate startDate = LocalDate.parse('2020-06-25', DateTimeFormatter.ofPattern('yyyy-MM-dd'))
    @Shared
    LocalDateTime ldt = startDate.atStartOfDay()

    def setup() {
        defBeginDateTime = LocalDateTime.now()
        defEndDateTime = defBeginDateTime.plusHours(2L)
        archivedSpot = new ArchivedSpot('WZE12345', DriverType.REGULAR, defBeginDateTime, defEndDateTime)
    }

    def 'should return empty optional because of null archived spot value'() {
        when: 'save archived spot as null to repository'
        Optional<Fee> opt = sut.save(null)

        then: 'should return empty optional'
        opt == Optional.empty()
    }

    def 'should save new archived spot to repository'() {
        when: 'save archived spot to repository'
        Optional<Fee> opt = sut.save(archivedSpot)

        then: 'should return not empty optional'
        opt != Optional.empty()

        and: 'values should be correct'
        with(archivedSpot) {
            vehiclePlate == opt.get().plate
            beginLocalDateTime == opt.get().startTime
            endLocalDateTime == opt.get().stopTime
         }
    }

    def 'should find all items by date'() {
        given: 'Values loaded to sut'
        values.forEach(sut.&save)

        when: "get values by date #startDate"
        def spotStream = sut.getAllByDay(startDate)

        then: 'Size of elements should be equal 2'
        spotStream.size() == 2

        when: "Check size one day after start date #startDate"
        spotStream = sut.getAllByDay(startDate.plusDays(1L))

        then: 'number of found items should be 3'
        spotStream.size() == 3

        when: "check size of list 2 days after #startDate"
        spotStream = sut.getAllByDay(startDate.plusDays(2L))

        then: 'number of found items should be 0'
        spotStream.size() == 0

        where: 'items for test'
        values = [new ArchivedSpot("WWW66666", DriverType.REGULAR, ldt, ldt.plusHours(2L)),
                  new ArchivedSpot("WSQ77777", DriverType.REGULAR, ldt, ldt.plusHours(3L)),
                  new ArchivedSpot("QAZ88888", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(4L)),
                  new ArchivedSpot("EDC99999", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(2L)),
                  new ArchivedSpot("FDR99998", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(1L))]
    }
}
