package pl.debuguj.system.spot

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class SpotRepoInMemorySpec extends Specification {
    @Subject
    SpotRepo sut = new SpotRepoInMemory()

    static LocalDateTime defaultDateTime
    static String defaultRegistrationNo
    @Shared
    static Spot spot

    def setup() {
        defaultDateTime = LocalDateTime.now()
        defaultRegistrationNo = "WZE12345"
        spot = new Spot(defaultRegistrationNo, DriverType.REGULAR, defaultDateTime)
    }

    def "Should return empty optional because of null spot value"() {
        when: "save #spot to repository"
        Optional<Spot> opt = sut.save(null)
        then: "should return empty optional"
        !opt.isPresent()
    }

    def "Should return empty optional because vehicle is active"() {
        when: "Saved one spot"
        sut.save(spot)
        and: "Save second time the same spot"
        Optional<Spot> opt = sut.save(spot)
        then: "Should return empty optional"
        !opt.isPresent()
    }

    def "Should return not empty optional because vehicle is not saved"() {
        when: "Save spot to repository"
        Optional<Spot> opt = sut.save(spot)
        then: "Should return value in optional"
        opt.isPresent()
    }

    def "Should delete active Spot"() {
        when: "Save one spot"
        sut.save(spot)
        and: "try to find saved spot by registration number"
        Optional<Spot> found = sut.findVehicleByPlate(defaultRegistrationNo)
        then: "saved spot should be found"
        found.isPresent()
        when: "Delete previous spot"
        sut.delete(spot.getVehiclePlate())
        and: "Try find previously saved spot"
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultRegistrationNo)
        then: "Should be not found"
        !notFound.isPresent()
    }

    def "should not find active parking space"() {
        when: "Try to find unsaved vehicle by plate"
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultRegistrationNo)
        then: "Should not be find"
        !notFound.isPresent()
    }
}
