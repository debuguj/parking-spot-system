package pl.debuguj.system.spot;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Profile({"prod","test", "default"})
@Transactional
@Repository
public class ArchivedSpotRepoImpl implements ArchivedSpotRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ArchivedSpot> save(ArchivedSpot archivedSpot) {
        if(Objects.nonNull(archivedSpot)){
            entityManager.persist(archivedSpot);
            return Optional.of(archivedSpot);
        }
        return Optional.empty();
    }

    @Override
    public List<ArchivedSpot> getArchivedSpotByDate(final LocalDate date) {
        String hql = "FROM archived_spot as arsp ORDER BY arsp.id";
        return (List<ArchivedSpot>) entityManager.createQuery(hql).getResultList();
    }

}