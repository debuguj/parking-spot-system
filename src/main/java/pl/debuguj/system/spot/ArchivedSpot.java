package pl.debuguj.system.spot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@AllArgsConstructor
@Getter
public class ArchivedSpot implements Serializable {

    private final UUID uuid = UUID.randomUUID();

    private final String vehiclePlate;
    private final DriverType driverType;
    private final Date beginDate;
    private final Date finishDate;

    public ArchivedSpot(final Spot spot, final Date finishDate) throws DateTimeException {
        if (spot.getBeginDate().after(finishDate)) {
            throw new DateTimeException("Finish date is to late");
        }
        this.vehiclePlate = spot.getVehiclePlate();
        this.driverType = spot.getDriverType();
        this.beginDate = spot.getBeginDate();
        this.finishDate = finishDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchivedSpot that = (ArchivedSpot) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public Optional<BigDecimal> getFee() {
        if (Objects.nonNull(getFinishDate()) && checkFinishDate()) {
            final BigDecimal fee = getBasicFee();
            final BigDecimal rate = new CurrencyRate(new BigDecimal(1.0), Currency.getInstance(Locale.getDefault())).getRate();

            return Optional.ofNullable(fee.multiply(rate).setScale(1, BigDecimal.ROUND_CEILING));
        } else {
            return Optional.empty();
        }
    }

    private boolean checkFinishDate() {
        return getFinishDate().after(getBeginDate());
    }

    public Optional<BigDecimal> getFee(final CurrencyRate cr) {
        if (Objects.nonNull(getFinishDate())) {
            BigDecimal fee = getBasicFee();
            return Optional.ofNullable(fee.multiply(cr.getRate()).setScale(1, BigDecimal.ROUND_CEILING));
        } else {
            return Optional.empty();
        }
    }

    private BigDecimal getBasicFee() {
        final BigDecimal period = getPeriod();
        BigDecimal startSum = this.getDriverType().getBeginValue();
        final BigDecimal factor = this.getDriverType().getFactor();

        int compResult = period.compareTo(BigDecimal.ONE);

        if (compResult == 0) {
            return startSum;
        } else if (compResult == 1) {
            BigDecimal current = new BigDecimal("2.0");

            for (int i = 1; i < period.intValueExact(); i++) {
                startSum = startSum.add(current);
                current = current.multiply(factor);
            }
            return startSum;
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Return period rounds to ceil (hours)
     *
     * @return Period of parking time in hours
     */
    private BigDecimal getPeriod() {
        LocalDateTime from = getBeginDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime to = getFinishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        BigDecimal minutes = new BigDecimal(from.until(to, ChronoUnit.MINUTES));
        BigDecimal div = new BigDecimal(60);

        return minutes.divide(div, BigDecimal.ROUND_CEILING);
    }
}
