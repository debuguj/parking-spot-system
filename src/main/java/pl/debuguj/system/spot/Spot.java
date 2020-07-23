package pl.debuguj.system.spot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Table(name= "spots")
public final class Spot implements Serializable {

    @Id
    @Column(name = "vehicle_plate")
    @NotEmpty(message = "Vehicle plate cannot be empty.")
    @NotNull(message = "Vehicle plate must be provided.")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$", message = "Invalid plate number.")
    private final String vehiclePlate;

    @Column(name = "driver_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Driver type must be provided.")
    @DriverTypeSubSet(anyOf = {DriverType.REGULAR, DriverType.VIP})
    private final DriverType driverType;

    @Column(name = "begin_datetime")
    @NotNull(message = "Begin datetime must be provided.")
    private final LocalDateTime beginDatetime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Spot)) return false;
        Spot spot = (Spot) o;
        return getVehiclePlate().equals(spot.getVehiclePlate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVehiclePlate());
    }
}
