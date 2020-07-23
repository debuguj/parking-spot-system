package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;
import pl.debuguj.system.driver.Fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ArchivedSpotRepoInMemory implements ArchivedSpotRepo {

    private static final Map<Long, ArchivedSpot> mapParkingSpots = new ConcurrentHashMap<>();

    @Override
    public Optional<Fee> save(final ArchivedSpot archivedSpot) {
        if (Objects.nonNull(archivedSpot)) {
            mapParkingSpots.put(archivedSpot.getId(), archivedSpot);
            return Optional.of(new Fee(archivedSpot));
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
