package pl.debuguj.system.owner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.debuguj.system.spot.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by GB on 07.03.2020.
 */
@RestController
@Slf4j
@AllArgsConstructor
class OwnerController {

    private final ArchivedSpotRepo archivedSpotRepo;

    @GetMapping(value = "${uri.owner.income}")
    public HttpEntity<DailyIncome> getIncomePerDay(@Valid @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {

        List<ArchivedSpot> archivedSpotList = archivedSpotRepo.getAllByDay(date);

        if (archivedSpotList.size() > 0) {
            BigDecimal income = archivedSpotList.stream()
                    .map(ArchivedSpot::getFee)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new ResponseEntity<>(new DailyIncome(date, income), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new DailyIncome(date, BigDecimal.ZERO), HttpStatus.NOT_FOUND);
        }
    }
}
