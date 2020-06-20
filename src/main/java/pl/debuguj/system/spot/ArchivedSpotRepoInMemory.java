package pl.debuguj.system.spot;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ArchivedSpotRepoInMemory implements ArchivedSpotRepo {


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
    public List<ArchivedSpot> getAllByDay(final Date date) {
        final Date end = createEndDate(date);
        return mapParkingSpots.values()
                .stream()
                .filter(ps -> date.before(ps.getBeginDate()) && end.after(ps.getBeginDate()))
                .collect(Collectors.toList());
    }

    private Date createEndDate(final Date d) {
        final LocalDateTime endDateTime = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Date.from(endDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
    }
}
