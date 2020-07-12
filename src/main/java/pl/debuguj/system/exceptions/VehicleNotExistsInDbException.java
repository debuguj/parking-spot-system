package pl.debuguj.system.exceptions;

public class VehicleNotExistsInDbException extends RuntimeException {
    public VehicleNotExistsInDbException(final String vehiclePlate) {
        super("Vehicle " + vehiclePlate + " not exists in database");
    }
}
