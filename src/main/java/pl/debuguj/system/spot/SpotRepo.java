package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by GB on 07.03.2020.
 */
@Repository
@Transactional(readOnly = true)
public interface SpotRepo {

    Optional<Spot> save(final Spot spot);

    Optional<Spot> findVehicleByPlate(final String vehiclePlate);

    Optional<Spot> delete(final String plate);
}
