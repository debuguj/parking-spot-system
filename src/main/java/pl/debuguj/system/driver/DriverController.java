package pl.debuguj.system.driver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.debuguj.system.driver.exceptions.VehicleActiveInDbException;
import pl.debuguj.system.driver.exceptions.VehicleCannotBeRegisteredInDbException;
import pl.debuguj.system.driver.exceptions.VehicleNotExistsInDbException;
import pl.debuguj.system.spot.ArchivedSpot;
import pl.debuguj.system.spot.ArchivedSpotRepo;
import pl.debuguj.system.spot.Spot;
import pl.debuguj.system.spot.SpotRepo;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * Created by GB on 07.03.20.
 */
@RestController
@Slf4j
@Validated
@PropertySource("classpath:global.properties")
class DriverController {

    private final SpotRepo spotRepo;
    private final ArchivedSpotRepo archivedSpotRepo;

    public DriverController(SpotRepo spotRepo, ArchivedSpotRepo archivedSpotRepo) {
        this.spotRepo = spotRepo;
        this.archivedSpotRepo = archivedSpotRepo;
    }

    @PostMapping(value = "${uri.driver.start}", consumes = "application/json", produces = "application/json")
    public HttpEntity<Spot> startParkingMeter(@RequestBody @Valid Spot spot) {

        spotRepo.findByVehiclePlate(spot.getVehiclePlate()).ifPresent(e -> {
            throw new VehicleActiveInDbException(spot.getVehiclePlate());
        });

        final Spot savedSpot = spotRepo.save(spot).orElseThrow(() -> new VehicleCannotBeRegisteredInDbException(spot.getVehiclePlate()));

        return new ResponseEntity<>(savedSpot, HttpStatus.OK);
    }

    @PatchMapping(value = "${uri.driver.stop}", produces = "application/json", consumes = "application/json")
    public HttpEntity<Fee> stopParkingMeter(
            @PathVariable @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$") String vehiclePlate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date finishDate) {

        final Spot spot = spotRepo.findByVehiclePlate(vehiclePlate).orElseThrow(() -> new VehicleNotExistsInDbException(vehiclePlate));

        final ArchivedSpot archivedSpot = new ArchivedSpot(spot.getVehiclePlate(), spot.getDriverType(), spot.getBeginDate(), finishDate);

        archivedSpotRepo.save(archivedSpot);
        spotRepo.delete(vehiclePlate);

        return new ResponseEntity(new Fee(archivedSpot), HttpStatus.OK);
    }

    @PostMapping(value = "${uri.simple}")
    public HttpEntity<Spot> simpleReturn(@PathVariable @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$") String plate,
                                         @RequestBody @Valid Spot spot) {
        //spotRepo.findByVehiclePlate(spot.getVehiclePlate()).orElseThrow(() -> new VehicleActiveInDbException(spot.getVehiclePlate()));
        final Spot savedSpot = spotRepo.save(spot).orElseThrow(() -> new VehicleCannotBeRegisteredInDbException(spot.getVehiclePlate()));
        return new ResponseEntity<>(spot, HttpStatus.OK);
    }
}
