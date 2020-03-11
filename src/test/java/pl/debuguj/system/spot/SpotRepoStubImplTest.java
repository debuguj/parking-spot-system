package pl.debuguj.system.spot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by GB on 05.03.2020.
 */

public class SpotRepoStubImplTest {

    private final SpotRepoStubImpl parkingSpaceRepo = new SpotRepoStubImpl();

    private final SimpleDateFormat timeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    private String registrationNumber = "WZE12345";
    private Date beginDate;
    private Date endDate;

    public SpotRepoStubImplTest() {
        try {
            beginDate = timeDateFormat.parse("2017-10-14T11:15:48");
            endDate = timeDateFormat.parse("2017-10-14T21:35:12");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void after() {
        parkingSpaceRepo.clearRepo();
    }

    @Test
    public void shouldReturnEmptyOptional() {
        Optional<Spot> opt = parkingSpaceRepo.save(null);
        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldReturnFalseBecauseVehicleIsActive() {
        Spot spot1 = new Spot(registrationNumber, DriverType.REGULAR, beginDate);
        parkingSpaceRepo.save(spot1);
        Spot spot2 = new Spot(registrationNumber, DriverType.REGULAR, beginDate);
        Optional<Spot> opt = parkingSpaceRepo.save(spot2);

        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldReturnTrueBecauseVehicleIsNotActive() {
        Spot spot = new Spot(registrationNumber, DriverType.REGULAR, beginDate);
        Optional<Spot> opt = parkingSpaceRepo.save(spot);

        assertTrue(opt.isPresent());
    }

    @Test
    public void shouldSaveNewActiveParkingSpace() {
        Spot spot = new Spot(registrationNumber, DriverType.REGULAR, beginDate);
        parkingSpaceRepo.save(spot);

        Optional<Spot> psFromRepo = parkingSpaceRepo.findActive(registrationNumber);

        assertNotNull(psFromRepo);
        assertEquals(spot, psFromRepo.get());
        assertEquals(spot.getBeginDate(), psFromRepo.get().getBeginDate());
        assertEquals(spot.getDriverType(), psFromRepo.get().getDriverType());
    }

    @Test
    public void shouldFindActiveParkingSpaceByRegistrationNumber() {
        Spot spot = new Spot(registrationNumber, DriverType.REGULAR, beginDate);
        parkingSpaceRepo.save(spot);

        Optional<Spot> psFromDao = parkingSpaceRepo.findActive(registrationNumber);

        assertTrue(psFromDao.isPresent());

        String registrationNo = "WCI997755";
        psFromDao = parkingSpaceRepo.findActive(registrationNo);

        assertFalse(psFromDao.isPresent());
    }


    @Test
    public void shouldFindAllByDate() throws Exception {

        createTestItems();

        Date date = dayDateFormat.parse("2017-10-14");
        Stream<Spot> spotStream = parkingSpaceRepo.findAllFinished(date);

        assertEquals(2, spotStream.count());

        date = dayDateFormat.parse("2017-10-13");
        spotStream = parkingSpaceRepo.findAllFinished(date);

        assertEquals(3, spotStream.count());

        date = dayDateFormat.parse("2017-10-1");
        spotStream = parkingSpaceRepo.findAllFinished(date);

        assertEquals(0, spotStream.count());
    }

    private void createTestItems() throws ParseException {

        String[] registrationNumbers = {"WWW66666", "WSQ77777", "QAZ88888", "EDC99999", "FDR99998"};
        DriverType[] driverTypes = {DriverType.REGULAR, DriverType.REGULAR, DriverType.REGULAR, DriverType.REGULAR, DriverType.REGULAR};
        String[] datesBegin = {"2017-10-13T10:25:48", "2017-10-13T12:25:48", "2017-10-13T15:25:48", "2017-10-14T20:25:48", "2017-10-14T11:15:48"};
        String[] datesFinish = {"2017-10-13T13:35:12", "2017-10-13T17:35:12", "2017-10-13T16:35:12", "2017-10-14T21:35:12", "2017-10-14T12:35:12"};

        for (int i = 0; i < registrationNumbers.length; i++) {
            Spot spot = new Spot(registrationNumbers[i], driverTypes[i], timeDateFormat.parse(datesBegin[i]));
            parkingSpaceRepo.save(spot);
            parkingSpaceRepo.updateFinishDateByPlate(spot.getVehicleRegistrationNumber(), timeDateFormat.parse(datesFinish[i]));
        }
    }
}