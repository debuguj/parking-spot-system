package pl.debuguj.system.driver;

import lombok.Getter;
import pl.debuguj.system.spot.ArchivedSpot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
class Fee implements Serializable {
    private final String plate;
    private final LocalDateTime startTime;
    private final LocalDateTime stopTime;
    private final BigDecimal fee;

    public Fee(final ArchivedSpot archivedSpot) {
        this.plate = archivedSpot.getVehiclePlate();
        this.startTime = archivedSpot.getBeginLocalDateTime();
        this.stopTime = archivedSpot.getEndLocalDateTime();
        this.fee = archivedSpot.getFee().get();
    }
}
