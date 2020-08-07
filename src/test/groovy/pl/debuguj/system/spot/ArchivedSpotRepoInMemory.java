package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;
import pl.debuguj.system.driver.Fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
class ArchivedSpotRepoInMemory implements ArchivedSpotRepo {

    private static final Map<Long, ArchivedSpot> mapParkingSpots = new ConcurrentHashMap<>();

    private Long counter = 0L;
    @Override
    public Optional<ArchivedSpot> save(final ArchivedSpot archivedSpot) {
        if (Objects.nonNull(archivedSpot)) {
            mapParkingSpots.put(counter++, archivedSpot);
            return Optional.of(archivedSpot);
        }
        return Optional.empty();
    }

    @Override
    public List<ArchivedSpot> getAllByDay(final LocalDate date) {
        return mapParkingSpots.values()
                .stream()
                .filter(as -> checkItem(date, as))
                .collect(Collectors.toList());
    }

    private boolean checkItem(final LocalDate date, final ArchivedSpot as) {
        final LocalDateTime ldtBegin = date.atStartOfDay();
        final LocalDateTime ldtEnd = ldtBegin.plusDays(1L);

        return as.getBeginLocalDateTime().isAfter(ldtBegin)
                && as.getBeginLocalDateTime().isBefore(ldtEnd)
                || as.getBeginLocalDateTime().isEqual(ldtBegin);
    }

}
