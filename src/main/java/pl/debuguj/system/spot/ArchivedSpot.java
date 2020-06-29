package pl.debuguj.system.spot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.debuguj.system.exceptions.IncorrectFinishDateException;
import pl.debuguj.system.external.CurrencyRate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class ArchivedSpot implements Serializable {

    private final UUID uuid = UUID.randomUUID();

    private final String vehiclePlate;
    private final DriverType driverType;
    private final LocalDateTime beginLocalDateTime;
    private final LocalDateTime endLocalDateTime;

    public ArchivedSpot(final Spot spot, final LocalDateTime endLocalDateTime) throws IncorrectFinishDateException {
        if (endLocalDateTime.isBefore(spot.getBeginDatetime())) {
            throw new IncorrectFinishDateException(spot.getBeginDatetime(), endLocalDateTime);
        }
        this.vehiclePlate = spot.getVehiclePlate();
        this.driverType = spot.getDriverType();
        this.beginLocalDateTime = spot.getBeginDatetime();
        this.endLocalDateTime = endLocalDateTime;
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
        if (Objects.nonNull(getEndLocalDateTime()) && checkFinishDate()) {
            final BigDecimal fee = getBasicFee();
            final BigDecimal rate = CurrencyRate.PLN.getRate();

            return Optional.ofNullable(fee.multiply(rate).setScale(1, BigDecimal.ROUND_CEILING));
        } else {
            return Optional.empty();
        }
    }

    private boolean checkFinishDate() {
        return getEndLocalDateTime().isAfter(getBeginLocalDateTime());
    }

    public Optional<BigDecimal> getFee(final CurrencyRate cr) {
        if (Objects.nonNull(getEndLocalDateTime())) {
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
        } else if (compResult > 0) {
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

        BigDecimal minutes = new BigDecimal(getBeginLocalDateTime().until(getEndLocalDateTime(), ChronoUnit.MINUTES));
        BigDecimal div = new BigDecimal(60);

        return minutes.divide(div, BigDecimal.ROUND_CEILING);
    }
}
