package pl.debuguj.system.spot;

import java.util.Date;
import java.util.List;
import java.util.Optional;


public interface ArchivedSpotRepo {

    Optional<ArchivedSpot> save(final ArchivedSpot archivedSpot);

    List<ArchivedSpot> getAllByDay(final Date date);

}
