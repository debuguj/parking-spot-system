package pl.debuguj.system.spot

import org.springframework.util.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import java.time.LocalDateTime

class SpotSpec extends Specification {

    @Subject
    @Shared
    Spot spot

    @Shared
            validator
    @Shared
    LocalDateTime defaultDateTime
    @Shared
            defaultRegistrationNumber

    def setup() {
        defaultDateTime = LocalDateTime.now()
        defaultRegistrationNumber = "WZE12345"
        spot = new Spot(defaultRegistrationNumber, DriverType.REGULAR, defaultDateTime)
    }

    def setupSpec() {
        def vf = Validation.buildDefaultValidatorFactory()
        validator = vf.getValidator()
    }

    def "Should be serialized correctly"() {
        given: "After #spot serialization to #other object"
        def other = (Spot) SerializationUtils.deserialize(SerializationUtils.serialize(spot))

        expect: 'Should return valid and correct values'
        with(other) {
            vehiclePlate == spot.vehiclePlate
            driverType == spot.driverType
            beginDatetime == spot.beginDatetime
        }
    }

    def "Creating new spot with valid input"() {
        expect: "Valid variables values"
        with(spot) {
            vehiclePlate == "WZE12345"
            beginDatetime == defaultDateTime
            driverType == DriverType.REGULAR
        }
    }

    def "Should not return violations"() {
        when: "Input is valid"
        Set<ConstraintViolation<Spot>> violations = validator.validate(spot)

        then: "Returns no violations"
        violations.isEmpty()
    }

    @Unroll
    def "Should return violations because of one null parameters: #plate #driverType #beginDate"() {
        given: "Spot with invalid input"
        def invalidSpot = new Spot(plate, driverType, beginDate)

        when: "Checking by validator"
        Set<ConstraintViolation<Spot>> violations = validator.validate(invalidSpot)

        then: "Number of violation should be greater than 0"
        violations.size() > 0

        where: "Invalid input is: "
        plate      | driverType         | beginDate
        null       | DriverType.REGULAR | defaultDateTime
        "WCD12345" | null               | defaultDateTime
        "WCI12345" | DriverType.REGULAR | null
    }

    @Unroll
    def "Should return violations because of incorrect registration number: #plate"() {
        given: "Spot wih invalid registration number"
        def invalidSpot = new Spot(plate, DriverType.REGULAR, defaultDateTime)

        when: "Checking by validator"
        Set<ConstraintViolation<Spot>> violations = this.validator.validate(invalidSpot)

        then: "Violation should be greater than 0"
        violations.size() > 0

        where: "Sets of plates to check"
        plate << ["e12345", "", " ", "     ", "12345", "qeee12345", "registrationNo", "qwe123456",
                  "qwe123", "E12345", "12345", "QEEE12345", "registrationNo", "QWE123456", "QWE123"]
    }

}
