package pl.debuguj.system.operator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.debuguj.system.spot.Spot;
import pl.debuguj.system.spot.SpotRepo;

import javax.validation.constraints.Pattern;

@RestController
@Slf4j
@AllArgsConstructor
@Validated
class OperatorController {

    private final SpotRepo spotRepo;

    @GetMapping("${uri.operator.check}")
    public HttpEntity<Spot> checkVehicleByPlate(@PathVariable
                                                    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$", message="Invalid value of vehicle plate. ")
                                                            String plate) {

        return spotRepo.findByVehiclePlate(plate)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
