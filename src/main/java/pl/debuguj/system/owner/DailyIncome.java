package pl.debuguj.system.owner;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@Getter
final class DailyIncome implements Serializable {
    private final LocalDate date;
    private final BigDecimal income;
}
