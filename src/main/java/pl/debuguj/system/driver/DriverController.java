package pl.debuguj.system.driver;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.debuguj.system.exceptions.VehicleActiveInDbException;
import pl.debuguj.system.exceptions.VehicleNotExistsInDbException;
import pl.debuguj.system.spot.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
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

        return spotRepo.save(spot)
                .map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElseThrow(() -> new VehicleActiveInDbException(spot.getVehiclePlate()));
    }

    @PatchMapping(value = "${uri.driver.stop}")
    public HttpEntity<Fee> stopParkingMeter(
            @Valid @PathVariable @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$") String plate,
            @Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") LocalDateTime finishDate) {

        final Spot spot = spotRepo.delete(plate).orElseThrow(() -> new VehicleNotExistsInDbException(plate));

        return archivedSpotRepo.save(new ArchivedSpot(spot, finishDate))
                .map(archivedSpot -> ResponseEntity.ok().body(new Fee(archivedSpot)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "${uri.simple}")
    public HttpEntity<Date> simpleReturn(@PathVariable @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$") String plate,
                                         @RequestBody @Valid Spot spot) {
        //spotRepo.findByVehiclePlate(spot.getVehiclePlate()).orElseThrow(() -> new VehicleActiveInDbException(spot.getVehiclePlate()));
        //final Spot savedSpot = spotRepo.save(spot).orElseThrow(() -> new VehicleCannotBeRegisteredInDbException(spot.getVehiclePlate()));
        return new ResponseEntity<>(new Date(), HttpStatus.OK);
    }
}
