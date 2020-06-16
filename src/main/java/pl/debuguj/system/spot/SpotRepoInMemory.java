package pl.debuguj.system.spot;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GB on 05.03.2020.
 */
@Repository
public class SpotRepoInMemory implements SpotRepo {

    //TODO: add limit for parking capacity
    private Map<String, Spot> mapParkingSpots = new ConcurrentHashMap<>();

    @Override
    public Optional<Spot> save(final Spot spot) {
        if (Objects.nonNull(spot) && !mapParkingSpots.containsKey(spot.getVehiclePlate())) {
            mapParkingSpots.put(spot.getVehiclePlate(), spot);
            return Optional.of(spot);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Spot> findVehicleByPlate(final String vehiclePlate) {
        return mapParkingSpots.values()
                .stream()
                .filter(s -> vehiclePlate.equals(s.getVehiclePlate()))
                .findAny();
    }

    @Override
    public Optional<Spot> delete(final String plate) {
        return Optional.ofNullable(mapParkingSpots.remove(plate));
    }

    public void clearRepo() {
        this.mapParkingSpots.clear();
    }
}
