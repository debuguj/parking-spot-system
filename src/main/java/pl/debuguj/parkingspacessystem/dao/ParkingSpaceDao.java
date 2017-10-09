package pl.debuguj.parkingspacessystem.dao;

import pl.debuguj.parkingspacessystem.domain.ParkingSpace;

import java.util.List;

/**
 * Created by grzesiek on 07.10.17.
 */
public interface ParkingSpaceDao {

    public void add(ParkingSpace ps);
    public boolean find(ParkingSpace ps);
    public List<ParkingSpace> getAllParkingSpaces();
}
