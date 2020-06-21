package pl.debuguj.system.spot;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by GB on 05.03.2020.
 */

class SpotRepoInMemoryTest {

    private final SpotRepoInMemory sut = new SpotRepoInMemory();

    @Test
    public void shouldReturnEmptyOptional() {
        Optional<Spot> opt = sut.save(null);
        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldReturnEmptyOptionalBecauseVehicleIsActive() {
        final Spot spot1 = createSimpleSpot("WZE12345");
        sut.save(spot1);

        Optional<Spot> opt = sut.save(spot1);

        assertFalse(opt.isPresent());
    }


    @Test
    public void shouldReturnNotEmptyOptionalBecauseVehicleIsNotActive() {
        final Spot spot = createSimpleSpot("WZE12345");
        Optional<Spot> opt = sut.save(spot);

        assertTrue(opt.isPresent());
    }

    @Test
    public void shouldDeleteActiveSpot() {
        final Spot spot = createSimpleSpot("WZE12345");
        sut.save(spot);
        Optional<Spot> found = sut.findVehicleByPlate("WZE12345");

        assertTrue(found.isPresent());

        sut.delete(spot.getVehiclePlate());
        Optional<Spot> notFound = sut.findVehicleByPlate("WZE12345");

        assertFalse(notFound.isPresent());
    }

    @Test
    public void shouldNotFindActiveParkingSpace() {
        Optional<Spot> notFound = sut.findVehicleByPlate("WZE12345");

        assertNotNull(notFound);
        assertFalse(notFound.isPresent());
    }

    private Spot createSimpleSpot(String wze12345) {
        return new Spot("WZE12345", DriverType.REGULAR, new Date());
    }
}