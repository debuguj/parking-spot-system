package pl.debuguj.system.spot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.util.SerializationUtils
import pl.debuguj.system.exceptions.IncorrectFinishDateException
import pl.debuguj.system.external.systems.CurrencyRate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.validation.Validation
import javax.validation.Validator
import java.time.LocalDateTime

@DataJpaTest
class ArchivedSpotSpec extends Specification {

    @Shared @Subject ArchivedSpot archivedSpot

    @Autowired TestEntityManager entityManager
    @Shared Validator validator

    @Shared CurrencyRate currencyRate = CurrencyRate.PLN
    @Shared LocalDateTime defBeginTimestamp = LocalDateTime.now()
    @Shared LocalDateTime defEndTimestamp = LocalDateTime.now().plusHours(2L)
    @Shared String defaultVehiclePlate = 'WZE12345'

    @Shared Set<ArchivedSpot> archivedSpots = new HashSet<>()

    def setupSpec() {
        def vf = Validation.buildDefaultValidatorFactory()
        this.validator = vf.getValidator()
    }

    def setup(){
        archivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginTimestamp, defEndTimestamp)
        archivedSpots.add(archivedSpot)
    }

    def cleanup(){
        archivedSpots.removeAll()
    }

    def 'given archived spot should be stored in set'(){
        expect: 'archived spot in set'
        archivedSpots.contains(archivedSpot)
    }

    def 'after persist to database archived spot should have id'(){
        expect: 'empty id in archived spot object'
        !archivedSpot.getId()

        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        then: 'should have id from database'
        archivedSpot.getId()

        and: 'archived spot should be found in set'
        archivedSpots.contains(archivedSpot)
    }

    def 'merge should be succeed'(){
        when: 'merge archived spot'
        ArchivedSpot mergedArchivedSpot = entityManager.merge(archivedSpot)

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(mergedArchivedSpot)
    }

    def 'archived spot should persist in database'(){
        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot foundArchivedSpot = entityManager.find(ArchivedSpot.class, archivedSpot.getId())

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(foundArchivedSpot)
    }

    def 'check detached archived spot'(){
        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot foundArchivedSpot = entityManager.find(ArchivedSpot.class, archivedSpot.getId())

        and: 'flush persistent context'
        entityManager.flush()

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(foundArchivedSpot)

        when: 'removing from set'
        archivedSpots.remove(foundArchivedSpot)

        then: 'set should not contains archived spot'
        !archivedSpots.contains(foundArchivedSpot)
    }

    def 'check finding and detaching'(){
        when: 'persist to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot foundArchivedSpot = entityManager.find(ArchivedSpot.class, archivedSpot.getId())

        and: 'detached object'
        entityManager.detach(foundArchivedSpot)

        then: 'set contains archived spot should contains default archived spot'
        archivedSpots.contains(foundArchivedSpot)
    }

    def 'validation of saved archived spot'(){
        when: 'save to database'
        entityManager.persistAndFlush(archivedSpot)

        and: 'archived spot was found'
        ArchivedSpot found = entityManager.find(ArchivedSpot.class, archivedSpot.getId())

        then: 'parameters should be valid'
        with(archivedSpot){
            vehiclePlate == found.getVehiclePlate()
            driverType == found.getDriverType()
            beginTimestamp == found.getBeginTimestamp()
            endTimestamp == found.getEndTimestamp()
            uuid == found.getUuid()
        }
    }

    def 'should be serialized correctly'() {
        given: "serialization #archivedSpot to #other object"
        def other = (ArchivedSpot) SerializationUtils.deserialize(SerializationUtils.serialize(archivedSpot))

        expect: 'should return valid and correct values'
        with(other) {
            vehiclePlate == archivedSpot.vehiclePlate
            driverType == archivedSpot.driverType
            beginTimestamp == archivedSpot.beginTimestamp
            endTimestamp == archivedSpot.endTimestamp
        }
    }

    def 'should returns no error after valid input params'() {
        expect: 'valid parameters'
        with(archivedSpot) {
            vehiclePlate == defaultVehiclePlate
            driverType == DriverType.REGULAR
            driverType != DriverType.VIP
            beginTimestamp == defBeginTimestamp
            endTimestamp == defEndTimestamp
        }
    }

    def 'should returns non null params'() {
        expect: 'not null params'
        with(archivedSpot) {
            vehiclePlate
            driverType
            beginTimestamp
            endTimestamp
        }
    }

    def 'should return empty optional for fee because of null finish date'() {
        given: 'archive spot with invalid finish date'
        def invalidArchivedSpot = new ArchivedSpot(
                defaultVehiclePlate, DriverType.REGULAR, defBeginTimestamp, null)

        expect: 'empty optional'
        Optional.empty() == invalidArchivedSpot.getFee(currencyRate)
    }

    def 'should throw an exception because finish date is before start date'() {
        given: 'incorrect end timestamp'
        def invalidEndTimestamp = defBeginTimestamp.minusHours(2L)

        and: 'simple spot for test'
        def spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, defBeginTimestamp)

        when: 'new archived spot created'
        def invalidArchivedSpot = new ArchivedSpot(spot, invalidEndTimestamp)

        then: 'should be null'
        !invalidArchivedSpot

        and: 'should throw an exception'
        IncorrectFinishDateException e = thrown()
        !e.cause
    }

    @Unroll
    def "should return fee equals to #assumedFee for REGULAR driver and given default currency rate PLN"() {
        given: 'archivedSpot with valid input'
        def invalidSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        expect: 'correct fee value'
        invalidSpot.getFee(currencyRate)
                .ifPresent({ estimatedFee -> compareValues(assumedFee, estimatedFee)})

        where: "valid #fee for period between #beginDate and #endDate"
        beginDate             | endDate               || assumedFee
        '2020-06-12T11:15:48' | '2020-06-12T11:35:12' || 1.0
        '2020-06-12T11:15:48' | '2020-06-12T12:35:12' || 3.0
        '2020-06-12T11:15:48' | '2020-06-12T13:35:12' || 7.0
        '2020-06-12T11:15:48' | '2020-06-12T16:35:12' || 63.0
        '2020-06-12T00:15:48' | '2020-06-12T15:35:12' || 65535.0
        '2020-06-12T11:15:48' | '2020-06-13T11:14:12' || 16777215.0
        '2020-06-12T10:10:10' | '2020-06-12T22:13:10' || 8191.0
    }

    @Unroll
    def "should return fee equals to #assumedFee for period #beginDate - #endDate for VIP driver and given default currency rate"() {
        given: 'archived spot with valid input'
        ArchivedSpot invalidSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.VIP,
                LocalDateTime.parse(beginDate), LocalDateTime.parse(endDate))

        expect: 'correct value of fee'
        invalidSpot.getFee(currencyRate).ifPresent({
                    estimatedFee -> compareValues(assumedFee, estimatedFee)})

        where: "valid #assumedFee between #beginDate and #endDate"
        beginDate             | endDate               || assumedFee
        '2020-10-12T11:15:48' | '2020-10-12T11:35:12' || 0.0
        '2020-10-12T11:15:48' | '2020-10-12T12:35:12' || 2.0
        '2020-10-12T11:15:48' | '2020-10-12T13:35:12' || 5.0
        '2020-10-12T11:15:48' | '2020-10-12T16:35:12' || 26.4
        '2020-10-12T00:15:48' | '2020-10-12T15:35:12' || 1747.6
        '2020-10-12T11:15:48' | '2020-10-13T11:14:12' || 44887.0
    }

    def compareValues(BigDecimal assumedFee, BigDecimal estimatedFee){
        assert assumedFee == estimatedFee
    }
}
