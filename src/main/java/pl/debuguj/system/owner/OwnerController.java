package pl.debuguj.system.owner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.debuguj.system.external.systems.CurrencyRateHandler;
import pl.debuguj.system.spot.ArchivedSpot;
import pl.debuguj.system.spot.ArchivedSpotRepo;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by GB on 07.03.2020.
 */
@RestController
@Slf4j
@AllArgsConstructor
class OwnerController {

    private final ArchivedSpotRepo archivedSpotRepo;
    private final CurrencyRateHandler currencyRateHandler;

    @GetMapping(value = "${uri.owner.income}")
    public HttpEntity<DailyIncome> getIncomePerDay(@Valid @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {


        final Collection<ArchivedSpot> archivedSpotList = archivedSpotRepo.findAllByBeginTimestamp(date.atStartOfDay());

        if (archivedSpotList.size() > 0) {
            final BigDecimal income = archivedSpotList.stream()
                    .map(as -> as.getFee(currencyRateHandler.getCurrencyRate()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return new ResponseEntity<>(new DailyIncome(date, income), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new DailyIncome(date, BigDecimal.ZERO), HttpStatus.NOT_FOUND);
        }
    }
}
