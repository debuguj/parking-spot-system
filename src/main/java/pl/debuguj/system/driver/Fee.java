package pl.debuguj.system.driver;

import lombok.Getter;
import pl.debuguj.system.exceptions.NullArchivedSpotException;
import pl.debuguj.system.spot.ArchivedSpot;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public final class Fee implements Serializable {
    private final String plate;
    private final LocalDateTime startTime;
    private final LocalDateTime stopTime;
    private final String fee;

    public Fee(final ArchivedSpot archivedSpot) {
        if (!Objects.nonNull(archivedSpot)) {
            throw new NullArchivedSpotException();
        }
        this.plate = archivedSpot.getVehiclePlate();
        this.startTime = archivedSpot.getBeginTimestamp();
        this.stopTime = archivedSpot.getEndTimestamp();
        this.fee = archivedSpot.getFee().map(Objects::toString).orElseThrow(IllegalArgumentException::new);
    }
}
