package pl.debuguj.system.spot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.util.SerializationUtils
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator
import java.time.LocalDateTime

@DataJpaTest
class SpotSpec extends Specification {

    @Subject @Shared Spot spot

    @Autowired TestEntityManager entityManager
    @Shared Validator validator

    @Shared LocalDateTime defaultDateTime = LocalDateTime.now()
    @Shared String defaultVehiclePlate = 'WZE12345'

    @Shared Set<ArchivedSpot> spots = new HashSet<>()

    def setupSpec() {
        def vf = Validation.buildDefaultValidatorFactory()
        validator = vf.getValidator()
    }

    def setup(){
        spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, defaultDateTime)
        spots.add(spot)
    }

    def cleanup(){
        spots.removeAll()
    }

    def 'given spot should be stored in set'(){
        expect: 'spot in set'
        spots.contains(spot)
    }

    def 'merge should be succeed'(){
        when: 'merge archived spot'
        Spot mergedSpot = entityManager.merge(spot)

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains spot should contains default spot'
        spots.contains(mergedSpot)
    }

    def 'spot should persist in database'(){
        when: 'persist to database'
        entityManager.persistAndFlush(spot)

        and: 'spot was found'
        Spot foundSpot = entityManager.find(Spot.class, spot.getVehiclePlate())

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains spot should contains default spot'
        spots.contains(foundSpot)
    }

    def 'check detached spot'(){
        when: 'persist to database'
        entityManager.persistAndFlush(spot)

        and: 'spot was found'
        Spot found = entityManager.find(Spot.class, spot.getVehiclePlate())

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains spot should contains default spot'
        spots.contains(found)

        when: 'removing from set'
        spots.remove(found)

        then: 'set should not contains spot'
        !spots.contains(found)
    }

    def 'check finding and detaching'(){
        when: 'persist to database'
        entityManager.persistAndFlush(spot)

        and: 'spot was found'
        Spot found = entityManager.find(Spot.class, spot.getVehiclePlate())

        and: 'detached object'
        entityManager.detach(found)

        then: 'set contains archived spot should contains default archived spot'
        spots.contains(found)
    }

    def 'validation of saved spot'(){
        when: 'save to database'
        entityManager.persistAndFlush(spot)

        and: 'archived spot was found'
        Spot found = entityManager.find(Spot.class, spot.getVehiclePlate())

        then: 'parameters should be valid'
        with(found){
            vehiclePlate == found.getVehiclePlate()
            driverType == found.getDriverType()
            beginDatetime == found.getBeginDatetime()
        }
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
