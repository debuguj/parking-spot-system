package pl.debuguj.system.spot

import org.springframework.util.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class SpotSpec extends Specification {

    @Shared
    Validator validator

    def setupSpec() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    def 'Creating new spot with validated input'() {
        given:
        def date = new Date()
        def spot = createSimpleSpot("WZE12345", date)
        expect:
        spot.vehiclePlate == "WZE12345"
        spot.beginDatetime == date
        spot.driverType == DriverType.REGULAR
    }

    def 'Serialization should be valid'() {
        given:
        def spot = createSimpleSpot()
        def other = (Spot) SerializationUtils.deserialize(SerializationUtils.serialize(spot))
        expect:
        other.beginDatetime == spot.beginDatetime
        other.driverType == spot.driverType
        other.vehiclePlate == spot.vehiclePlate
    }

    def 'Should not return violations'() {
        given:
        def spot = createSimpleSpot()
        when:
        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot)
        then:
        violations.isEmpty()
    }

    @Unroll
    def "Should return violations because of one null parameters: #plate #driverType #beginDate "() {
        given:
        def invalidSpot = createSimpleSpot(plate, driverType, beginDate)
        when:
        Set<ConstraintViolation<Spot>> violations = this.validator.validate(invalidSpot)
        then:
        violations.size() > 0
        where:
        plate      | driverType         | beginDate
        null       | DriverType.REGULAR | new Date()
        "WCD12345" | null               | new Date()
        "WCI12345" | DriverType.REGULAR | null
    }

    @Unroll
    def "Should return violations because of incorrect registration number: #plate"() {
        given:
        def invalidSpot = createSimpleSpot(plate)
        when:
        Set<ConstraintViolation<Spot>> violations = this.validator.validate(invalidSpot)
        then:
        violations.size() > 0
        where:
        plate << ["e12345", "", " ", "     ", "12345", "qeee12345", "registrationNo", "qwe123456",
                  "qwe123", "E12345", "12345", "QEEE12345", "registrationNo", "QWE123456", "QWE123"]
    }

    def createSimpleSpot() {
        new Spot("WZE12345", DriverType.REGULAR, new Date())
    }

    def createSimpleSpot(def plate, def date) {
        new Spot(plate, DriverType.REGULAR, date)
    }

    def createSimpleSpot(def registrationNumber, def driverType, def startDate) {
        new Spot(registrationNumber, driverType, startDate)
    }

    def createSimpleSpot(def registrationNumber) {
        new Spot(registrationNumber, DriverType.REGULAR, new Date())
    }


}
