package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
public class ArchivedSpotRepoStub implements ArchivedSpotRepo {
    private static Map<UUID, ArchivedSpot> mapParkingSpots = new ConcurrentHashMap<>();

    @Override
    public Optional<ArchivedSpot> save(final ArchivedSpot archivedSpot) {
        if (Objects.nonNull(archivedSpot)) {
            mapParkingSpots.put(archivedSpot.getUuid(), archivedSpot);
            return Optional.of(archivedSpot);
        }
        return Optional.empty();
    }

    @Override
    public Stream<ArchivedSpot> getAllByDay(final Date date) {
        final Date end = createEndDate(date);
        return mapParkingSpots.values()
                .stream()
                .filter(ps -> date.before(ps.getFinishDate()) && end.after(ps.getBeginDate()));
    }

    private Date createEndDate(final Date d) {
        final LocalDateTime endDateTime = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Date.from(endDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
    }

    public void clearRepo() {
        this.mapParkingSpots.clear();
    }
}
