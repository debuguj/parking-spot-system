package pl.debuguj.system.driver;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.debuguj.system.exceptions.VehicleActiveInDbException;
import pl.debuguj.system.exceptions.VehicleCannotBeRegisteredInDbException;
import pl.debuguj.system.exceptions.VehicleNotExistsInDbException;
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
@AllArgsConstructor
class DriverController {

    private final SpotRepo spotRepo;
    private final ArchivedSpotRepo archivedSpotRepo;

    @PostMapping(value = "${uri.driver.start}")
    public HttpEntity<Spot> startParkingMeter(@RequestBody @Valid Spot spot) {
        spotRepo.findVehicleByPlate(spot.getVehiclePlate())
                .ifPresent(e -> {
                    throw new VehicleActiveInDbException(spot.getVehiclePlate());
                });

        final Spot savedSpot = spotRepo.save(spot)
                .orElseThrow(() -> new VehicleCannotBeRegisteredInDbException(spot.getVehiclePlate()));

        return new ResponseEntity<>(savedSpot, HttpStatus.OK);
    }

    @PatchMapping(value = "${uri.driver.stop}")
    public HttpEntity<Fee> stopParkingMeter(
            @Valid @PathVariable @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$") String plate,
            @Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") Date finishDate) {

        final Spot spot = spotRepo.findVehicleByPlate(plate)
                .orElseThrow(() -> new VehicleNotExistsInDbException(plate));

        final ArchivedSpot archivedSpot = new ArchivedSpot(spot, finishDate);
        archivedSpotRepo.save(archivedSpot);
        spotRepo.delete(plate);

        return new ResponseEntity<>(new Fee(archivedSpot), HttpStatus.OK);
    }

    @PostMapping(value = "${uri.simple}")
    public HttpEntity<Date> simpleReturn(@PathVariable @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$") String plate,
                                         @RequestBody @Valid Spot spot) {
        //spotRepo.findByVehiclePlate(spot.getVehiclePlate()).orElseThrow(() -> new VehicleActiveInDbException(spot.getVehiclePlate()));
        //final Spot savedSpot = spotRepo.save(spot).orElseThrow(() -> new VehicleCannotBeRegisteredInDbException(spot.getVehiclePlate()));
        return new ResponseEntity<>(new Date(), HttpStatus.OK);
    }
}
