package pl.debuguj.system.spot

import org.springframework.util.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.validation.ConstraintViolation
import javax.validation.Validation
import java.time.LocalDateTime

class SpotSpec extends Specification {

    @Subject
    @Shared
    Spot spot

    @Shared
    def validator
    @Shared
    LocalDateTime defaultDateTime
    @Shared
    def defaultVehiclePlate

    def setupSpec() {
        defaultDateTime = LocalDateTime.now()
        defaultVehiclePlate = 'WZE12345'
        spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, defaultDateTime)
        def vf = Validation.buildDefaultValidatorFactory()
        validator = vf.getValidator()
    }

    def 'should be serialized correctly'() {
        given: "after #spot serialization to #other object"
        def other = (Spot) SerializationUtils.deserialize(SerializationUtils.serialize(spot))

        expect: 'should return valid and correct values'
        with(other) {
            vehiclePlate == spot.vehiclePlate
            driverType == spot.driverType
            beginDatetime == spot.beginDatetime
        }
    }

    def 'creating new spot with valid input'() {
        expect: 'valid variables values'
        with(spot) {
            vehiclePlate == 'WZE12345'
            beginDatetime == defaultDateTime
            driverType == DriverType.REGULAR
        }
    }

    def "should not return violations"() {
        when: "input is valid"
        Set<ConstraintViolation<Spot>> violations = validator.validate(spot)

        then: 'returns no violations'
        violations.isEmpty()
    }

    @Unroll
    def "should return violations because of one null parameters: #plate #driverType #beginDate"() {
        given: 'spot with invalid input'
        def invalidSpot = new Spot(plate, driverType, beginDate)

        when: 'checking by validator'
        Set<ConstraintViolation<Spot>> violations = validator.validate(invalidSpot)

        then: 'number of violation should be greater than 0'
        violations.size() > 0

        where: 'invalid input is: '
        plate      | driverType         | beginDate
        null       | DriverType.REGULAR | defaultDateTime
        'WCD12345' | null               | defaultDateTime
        'WCI12345' | DriverType.REGULAR | null
    }

    @Unroll
    def "should return violations because of incorrect vehicle plate: #plate"() {
        given: 'spot wih invalid vehicle plate'
        def invalidSpot = new Spot(plate, DriverType.REGULAR, defaultDateTime)

        when: 'Checking by validator'
        Set<ConstraintViolation<Spot>> violations = this.validator.validate(invalidSpot)

        then: 'violation should be greater than 0'
        violations.size() > 0

        where: 'sets of plates to check'
        plate << ['e12345', '', ' ', '     ', '12345', 'qeee12345', 'vehiclePlate', 'qwe123456',
                  'qwe123', 'E12345', '12345', 'QEEE12345', 'vehiclePlate123', 'QWE123456', 'QWE123']
    }

}
