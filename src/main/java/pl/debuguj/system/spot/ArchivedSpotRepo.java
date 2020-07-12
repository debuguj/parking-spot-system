package pl.debuguj.system.spot;

import pl.debuguj.system.driver.Fee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ArchivedSpotRepo {

    Optional<Fee> save(final ArchivedSpot archivedSpot);

    List<ArchivedSpot> getAllByDay(final LocalDate date);

}
