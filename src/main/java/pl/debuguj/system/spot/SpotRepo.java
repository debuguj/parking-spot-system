package pl.debuguj.system.spot;


import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by GB on 07.03.2020.
 */
@Transactional(readOnly = true)
public interface SpotRepo extends Repository<Spot, String> {

    Optional<Spot> save(final Spot spot);

    Optional<Spot> findByVehiclePlate(final String vehiclePlate);

    int deleteByVehiclePlate(final String vehiclePlate);
}
