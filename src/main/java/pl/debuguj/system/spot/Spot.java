package pl.debuguj.system.spot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name= "spots")
@Entity
public final class Spot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "vehicle_plate")
    @NotEmpty(message = "Vehicle plate cannot be empty.")
    @NotNull(message = "Vehicle plate must be provided.")
    @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$", message = "Invalid plate number.")
    private String vehiclePlate;

    @Column(name = "driver_type")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Driver type must be provided.")
    @DriverTypeSubSet(anyOf = {DriverType.REGULAR, DriverType.VIP})
    private DriverType driverType;

    @Column(name = "begin_datetime")
    @NotNull(message = "Begin datetime must be provided.")
    private LocalDateTime beginDatetime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spot other = (Spot) o;
        return Objects.equals(vehiclePlate, other.getVehiclePlate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehiclePlate);
    }
}
