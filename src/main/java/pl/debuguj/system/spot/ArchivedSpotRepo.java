package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ArchivedSpotRepo extends BaseArchivedSpotRepo<ArchivedSpot, Long>{

    List<ArchivedSpot> findAllByBeginTimestamp(LocalDateTime beginTimestamp);
}
