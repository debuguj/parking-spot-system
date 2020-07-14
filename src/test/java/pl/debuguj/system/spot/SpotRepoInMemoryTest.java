package pl.debuguj.system.spot;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by GB on 05.03.2020.
 */

class SpotRepoInMemoryTest {

    private final SpotRepo sut = new SpotRepoInMemory();

    private static final LocalDateTime defaultDateTime = LocalDateTime.now();
    private static final String defaultVehiclePlate = "WZE12345";
    private static Spot spot;

    @BeforeAll
    static void init() {
        spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, defaultDateTime);
    }

    @Test
    public void shouldReturnEmptyOptional() {
        Optional<Spot> opt = sut.save(null);
        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldReturnEmptyOptionalBecauseVehicleIsActive() {
        sut.save(spot);
        Optional<Spot> opt = sut.save(spot);
        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldReturnNotEmptyOptionalBecauseVehicleIsNotActive() {
        Optional<Spot> opt = sut.save(spot);
        assertTrue(opt.isPresent());
    }

    @Test
    public void shouldDeleteActiveSpot() {
        sut.save(spot);
        Optional<Spot> found = sut.findVehicleByPlate(defaultVehiclePlate);
        assertTrue(found.isPresent());

        sut.delete(spot.getVehiclePlate());
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultVehiclePlate);

        assertFalse(notFound.isPresent());
    }

    @Test
    public void shouldNotFindActiveParkingSpace() {
        Optional<Spot> notFound = sut.findVehicleByPlate(defaultVehiclePlate);

        assertNotNull(notFound);
        assertFalse(notFound.isPresent());
    }
}