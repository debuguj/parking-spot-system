package pl.debuguj.system.spot

import org.springframework.util.SerializationUtils
import pl.debuguj.system.exceptions.IncorrectFinishDateException
import pl.debuguj.system.external.CurrencyRate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.validation.Validation
import java.time.LocalDateTime

class ArchivedSpotSpec extends Specification {

    @Shared
    @Subject
    ArchivedSpot archivedSpot
    @Shared
    def validator
    @Shared
    def currencyRate = CurrencyRate.PLN
    @Shared
    LocalDateTime defBeginDateTime
    @Shared
    LocalDateTime defEndDateTime
    @Shared
    String defaultVehiclePlate

    def setup() {
        defBeginDateTime = LocalDateTime.now()
        defEndDateTime = LocalDateTime.now().plusHours(2L)
        defaultVehiclePlate = 'WZE12345'
        archivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginDateTime, defEndDateTime)
    }

    def setupSpec() {
        def vf = Validation.buildDefaultValidatorFactory()
        this.validator = vf.getValidator()
    }

    def 'should be serialized correctly'() {
        given: "serialization #archivedSpot to #other object"
        def other = (ArchivedSpot) SerializationUtils.deserialize(SerializationUtils.serialize(archivedSpot))

        expect: 'should return valid and correct values'
        with(other) {
            uuid == archivedSpot.uuid
            vehiclePlate == archivedSpot.vehiclePlate
            driverType == archivedSpot.driverType
            beginLocalDateTime == archivedSpot.beginLocalDateTime
            endLocalDateTime == archivedSpot.endLocalDateTime
        }
    }

    def 'should returns no error after valid input params'() {
        expect:
        with(archivedSpot) {
            vehiclePlate == defaultVehiclePlate
            driverType == DriverType.REGULAR
            driverType != DriverType.VIP
            beginLocalDateTime == defBeginDateTime
            endLocalDateTime == defEndDateTime
        }
    }

    def 'should return empty optional for fee because of null finish date'() {
        given: 'archive spot with invalid finish date'
        def invalidArchivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginDateTime, null)

        expect: 'empty optional'
        Optional.empty() == invalidArchivedSpot.getFee(currencyRate)
    }

    def 'should throw an exception because finish date is before start date'() {
        given: 'incorrect end timestamp'
        def invalidEndTimestamp = defBeginDateTime.minusHours(2L)

        and: 'simple spot for test'
        def spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, defBeginDateTime)

        when: 'new archived spot created'
        new ArchivedSpot(spot, invalidEndTimestamp)

        then: 'should throw an exception'
        thrown(IncorrectFinishDateException)
    }

    @Unroll
    def "should return a correct #fee for REGULAR driver and given default currency rate PLN"() {
        given: 'archivedSpot with valid input'
        def invalidSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        expect: 'correct value of fee'
        invalidSpot.getFee(currencyRate).ifPresent({ f -> new BigDecimal(fee) == f })

        where: "valid #fee for period between #beginDate and #endDate"
        beginDate             | endDate               || fee
        '2020-06-12T11:15:48' | '2020-06-12T11:35:12' || 1.0
        '2020-06-12T11:15:48' | '2020-06-12T12:35:12' || 3.0
        '2020-06-12T11:15:48' | '2020-06-12T13:35:12' || 7.0
        '2020-06-12T11:15:48' | '2020-06-12T16:35:12' || 63.0
        '2020-06-12T00:15:48' | '2020-06-12T15:35:12' || 65535.0
        '2020-06-12T11:15:48' | '2020-06-13T11:14:12' || 16777215.0
        '2020-06-12T10:10:10' | '2020-06-12T22:13:10' || 8191.0
    }

    @Unroll
    def "should return a correct #fee for VIP driver and given default currency rate"() {
        given: 'archived spot with valid input'
        ArchivedSpot invalidSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.VIP,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        expect: 'correct value of fee'
        invalidSpot.getFee(currencyRate).ifPresent({ f -> new BigDecimal(fee) == f })

        where: "valid #fee between #beginDate and #endDate"
        beginDate             | endDate               || fee
        '2020-10-12T11:15:48' | '2020-10-12T11:35:12' || 0.0
        '2020-10-12T11:15:48' | '2020-10-12T12:35:12' || 2.0
        '2020-10-12T11:15:48' | '2020-10-12T13:35:12' || 5.0
        '2020-10-12T11:15:48' | '2020-10-12T16:35:12' || 26.4
        '2020-10-12T00:15:48' | '2020-10-12T15:35:12' || 1747.6
        '2020-10-12T11:15:48' | '2020-10-13T11:14:12' || 44887.0
    }
}
