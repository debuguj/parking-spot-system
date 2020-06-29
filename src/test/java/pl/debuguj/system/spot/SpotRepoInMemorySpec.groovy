package pl.debuguj.system.spot

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class SpotRepoInMemorySpec extends Specification {

    @Subject
    SpotRepo sut = new SpotRepoInMemory()

    @Shared
    String defaultRegistrationNo
    @Shared
    Spot spot

    def setup() {
        defaultRegistrationNo = "WZE12345"
        spot = new Spot(defaultRegistrationNo, DriverType.REGULAR, LocalDateTime.now())
    }

    def "Should return empty optional because of null spot value"() {
        when: "save #spot to repository"
        Optional<Spot> opt = sut.save(null)

        then: "should return empty optional"
        opt == Optional.empty()
    }

    def "Should return empty optional because vehicle is active"() {
        when: "Saved one spot"
        sut.save(spot)

        and: "Save the same spot again"
        Optional<Spot> opt = sut.save(spot)

        then: "Should return empty optional"
        opt == Optional.empty()
    }

    def "Should return not empty optional because vehicle is not saved"() {
        when: "Save spot to repository"
        Optional<Spot> opt = sut.save(spot)

        then: "Should return value in optional"
        opt != Optional.empty()
    }

    def "Should delete active Spot"() {
        when: "Save one spot"
        sut.save(spot)

        and: "try to find saved spot by registration number"
        Optional<Spot> found = sut.findVehicleByPlate(defaultRegistrationNo)

        then: "saved spot should be found"
        found?.isPresent()

        when: "Delete previous spot"
        sut.delete(spot.getVehiclePlate())

        and: "Try find previously saved spot"
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultRegistrationNo)

        then: "Should not be found"
        notFound == Optional.empty()
    }

    def "should not find active parking space"() {
        when: "Try to find unsaved vehicle by plate"
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultRegistrationNo)

        then: "Should not be find"
        notFound == Optional.empty()
    }
}
