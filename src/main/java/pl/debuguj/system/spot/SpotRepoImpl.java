package pl.debuguj.system.spot;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Profile({"prod", "default"})
@Transactional
@Repository
public class SpotRepoImpl implements SpotRepo {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Spot> save(final Spot spot) {
        final Spot saved = entityManager.find(Spot.class, spot.getVehiclePlate());
        if (Objects.isNull(saved)) {
            entityManager.persist(spot);
            return Optional.of(spot);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spot> findVehicleByPlate(final String vehiclePlate) {
        final Spot spot = entityManager.find(Spot.class, vehiclePlate);
        if (Objects.nonNull(spot)) {
            return Optional.of(spot);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spot> delete(final String plate) {
        final Spot spot = entityManager.find(Spot.class, plate);
        if(Objects.nonNull(spot)){
            entityManager.remove(spot);
            return Optional.of(spot);
        }
        return Optional.empty();
    }
}