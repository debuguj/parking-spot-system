package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ArchivedSpotRepo extends BaseArchivedSpotRepo<ArchivedSpot, Long>{

    List<ArchivedSpot> getArchivedSpotByDate(LocalDate date);
}
