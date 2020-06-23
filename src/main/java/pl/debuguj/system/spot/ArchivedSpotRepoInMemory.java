package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
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
    public List<ArchivedSpot> getAllByDay(final LocalDate date) {

        return mapParkingSpots.values()
                .stream()
                .filter(ps -> date.atStartOfDay().isBefore(ps.getBeginLocalDateTime()) && date.plusDays(1L).atStartOfDay().isAfter(ps.getBeginLocalDateTime()))
                .collect(Collectors.toList());
    }

//    private Date createEndDate(final Date d) {
//        final LocalDateTime endDateTime = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        return Date.from(endDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
//    }
}
