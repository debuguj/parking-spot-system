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
    String defaultRegistrationNumber

    def setup() {
        defBeginDateTime = LocalDateTime.now()
        defEndDateTime = LocalDateTime.now().plusHours(2L)
        defaultRegistrationNumber = "WZE12345"
        archivedSpot = new ArchivedSpot(defaultRegistrationNumber, DriverType.REGULAR, defBeginDateTime, defEndDateTime)
    }

    def setupSpec() {
        def vf = Validation.buildDefaultValidatorFactory()
        this.validator = vf.getValidator()
    }

    def "Should be serialized correctly"() {
        given: "Serialization #archivedSpot to #other object"
        def other = (ArchivedSpot) SerializationUtils.deserialize(SerializationUtils.serialize(archivedSpot))

        expect: 'Should return valid and correct values'
        with(other) {
            uuid == archivedSpot.uuid
            vehiclePlate == archivedSpot.vehiclePlate
            driverType == archivedSpot.driverType
            beginLocalDateTime == archivedSpot.beginLocalDateTime
            endLocalDateTime == archivedSpot.endLocalDateTime
        }
    }

    def "Should returns no error after valid input params"() {
        expect:
        with(archivedSpot) {
            vehiclePlate == defaultRegistrationNumber
            driverType == DriverType.REGULAR
            driverType != DriverType.VIP
            beginLocalDateTime == defBeginDateTime
            endLocalDateTime == defEndDateTime
        }
    }

    def "Should return empty optional for fee because of null finish date"() {
        given: "An archiveSpot with invalid finish date"
        def invalidArchivedSpot = new ArchivedSpot(defaultRegistrationNumber, DriverType.REGULAR, defBeginDateTime, null)

        expect: "An empty optional"
        Optional.empty() == invalidArchivedSpot.getFee(currencyRate)
    }

    def "should throws an exception because finish date is before start date"() {
        given: "An incorrect endTimestamp"
        LocalDateTime invalidEndTimestamp = defBeginDateTime.minusHours(2L)

        and: "A simple Spot for test"
        def spot = new Spot(defaultRegistrationNumber, DriverType.REGULAR, defBeginDateTime)

        when:
        new ArchivedSpot(spot, invalidEndTimestamp)

        then:
        thrown(IncorrectFinishDateException)
    }

    @Unroll
    def "Should return a correct #fee for REGULAR driver and given default currency rate PLN"() {
        given: "An ArchivedSpot with valid input"
        def invalidSpot = new ArchivedSpot(defaultRegistrationNumber, DriverType.REGULAR,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        expect: "A correct value of fee"
        invalidSpot.getFee(currencyRate).ifPresent({ f -> new BigDecimal(fee) == f })

        where: "Valid input is: "
        beginDate             | endDate               || fee
        "2020-06-12T11:15:48" | "2020-06-12T11:35:12" || 1.0
        "2020-06-12T11:15:48" | "2020-06-12T12:35:12" || 3.0
        "2020-06-12T11:15:48" | "2020-06-12T13:35:12" || 7.0
        "2020-06-12T11:15:48" | "2020-06-12T16:35:12" || 63.0
        "2020-06-12T00:15:48" | "2020-06-12T15:35:12" || 65535.0
        "2020-06-12T11:15:48" | "2020-06-13T11:14:12" || 16777215.0
        "2020-06-12T10:10:10" | "2020-06-12T22:13:10" || 8191.0
    }

    @Unroll
    def "Should return a correct #fee for VIP driver and given default currency rate"() {
        given: "ArchivedSpot with valid input"
        ArchivedSpot invalidSpot = new ArchivedSpot(defaultRegistrationNumber, DriverType.VIP,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        expect: "A correct value of fee"
        invalidSpot.getFee(currencyRate).ifPresent({ f -> new BigDecimal(fee) == f })

        where: "Valid input is: "
        beginDate             | endDate               || fee
        "2020-10-12T11:15:48" | "2020-10-12T11:35:12" || 0.0
        "2020-10-12T11:15:48" | "2020-10-12T12:35:12" || 2.0
        "2020-10-12T11:15:48" | "2020-10-12T13:35:12" || 5.0
        "2020-10-12T11:15:48" | "2020-10-12T16:35:12" || 26.4
        "2020-10-12T00:15:48" | "2020-10-12T15:35:12" || 1747.6
        "2020-10-12T11:15:48" | "2020-10-13T11:14:12" || 44887.0
    }
}
