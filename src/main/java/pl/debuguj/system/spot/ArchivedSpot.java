package pl.debuguj.system.spot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import pl.debuguj.system.exceptions.IncorrectFinishDateException;
import pl.debuguj.system.external.systems.CurrencyRate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "archived_spot")
@Entity
public final class ArchivedSpot implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator="native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    @Getter
    @Column(name = "id", unique=true, nullable=false, updatable=false)
    private Long id;

    @Getter
    @Column(name = "vehicle_plate", columnDefinition="CHAR(8)", unique=true, nullable=false, updatable=false)
    private String vehiclePlate;

    @Getter
    @Column(name = "driver_type", columnDefinition="CHAR(7)", nullable=false, updatable=false)
    @Enumerated(EnumType.STRING)
    private DriverType driverType;

    @Getter
    @Column(name = "begin_datetime", nullable=false, updatable=false)
    private LocalDateTime beginTimestamp;

    @Getter
    @Column(name = "end_datetime", nullable=false, updatable=false)
    private LocalDateTime endTimestamp;

    @Getter
    @Column(name = "uuid", columnDefinition = "BINARY(16)", nullable=false, updatable=false, unique=true)
    private UUID uuid = UUID.randomUUID();

    public ArchivedSpot(final Spot spot, final LocalDateTime endTimestamp) throws IncorrectFinishDateException {
        Objects.requireNonNull(spot, "Spot cannot be null");

        if (endTimestamp.isBefore(spot.getBeginDatetime())) {
            throw new IncorrectFinishDateException(spot.getBeginDatetime(), endTimestamp);
        }
        this.vehiclePlate = spot.getVehiclePlate();
        this.driverType = spot.getDriverType();
        this.beginTimestamp = spot.getBeginDatetime();
        this.endTimestamp = endTimestamp;
    }

    public ArchivedSpot(String defaultVehiclePlate, DriverType regular, LocalDateTime defBeginDateTime, LocalDateTime defEndDateTime) {
        this.vehiclePlate = defaultVehiclePlate;
        this.driverType = regular;
        this.beginTimestamp = defBeginDateTime;
        this.endTimestamp = defEndDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArchivedSpot other = (ArchivedSpot) o;
        return Objects.equals(uuid, other.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "ArchivedSpot{" +
                "id=" + id +
                ", vehiclePlate='" + vehiclePlate + '\'' +
                ", driverType=" + driverType +
                ", beginLocalDateTime=" + beginTimestamp +
                ", endLocalDateTime=" + endTimestamp +
                '}';
    }

    public Optional<BigDecimal> getFee() {
        if (Objects.nonNull(getEndTimestamp()) && checkFinishDate()) {
            final BigDecimal fee = getBasicFee();
            final BigDecimal rate = CurrencyRate.PLN.getRate();

            return Optional.ofNullable(fee.multiply(rate).setScale(1, BigDecimal.ROUND_CEILING));
        } else {
            return Optional.empty();
        }
    }

    private boolean checkFinishDate() {
        return getEndTimestamp().isAfter(getBeginTimestamp());
    }

    public Optional<BigDecimal> getFee(final CurrencyRate cr) {
        if (Objects.nonNull(getEndTimestamp())) {
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

        BigDecimal minutes = new BigDecimal(getBeginTimestamp().until(getEndTimestamp(), ChronoUnit.MINUTES));
        BigDecimal div = new BigDecimal(60);

        return minutes.divide(div, BigDecimal.ROUND_CEILING);
    }
}
