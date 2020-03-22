package pl.debuguj.system.spot;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class ArchivedSpotRepoStubTest {

    private final ArchivedSpotRepoStub archivedSpotRepoStub = new ArchivedSpotRepoStub();

    private final SimpleDateFormat timeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String registrationNumber = "WZE12345";
    private Date beginDate;
    private Date endDate;

    public ArchivedSpotRepoStubTest() {
        try {
            beginDate = timeDateFormat.parse("2017-10-14T11:15:48");
            endDate = timeDateFormat.parse("2017-10-14T21:35:12");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void after() {
        archivedSpotRepoStub.clearRepo();
    }

    @Test
    public void shouldReturnEmptyOptional() {
        Optional<ArchivedSpot> opt = archivedSpotRepoStub.save(null);
        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldSaveNewArchivedSpot() {
        ArchivedSpot archivedSpot = new ArchivedSpot(registrationNumber, DriverType.REGULAR, beginDate, endDate);
        Optional<ArchivedSpot> opt = archivedSpotRepoStub.save(archivedSpot);

        assertTrue(opt.isPresent());
        assertEquals(archivedSpot, opt.get());
        assertEquals(archivedSpot.getUuid(), opt.get().getUuid());
        assertEquals(archivedSpot.getBeginDate(), opt.get().getBeginDate());
        assertEquals(archivedSpot.getFinishDate(), opt.get().getFinishDate());
        assertEquals(archivedSpot.getDriverType(), opt.get().getDriverType());
    }

    @Test
    public void shouldFindAllByDate() throws Exception {

        String[] registrationNumbers = {"WWW66666", "WSQ77777", "QAZ88888", "EDC99999", "FDR99998"};
        DriverType[] driverTypes = {DriverType.REGULAR, DriverType.REGULAR, DriverType.REGULAR, DriverType.REGULAR, DriverType.REGULAR};
        String[] datesBegin = {"2017-10-13T10:25:48", "2017-10-13T12:25:48", "2017-10-13T15:25:48", "2017-10-14T20:25:48", "2017-10-14T11:15:48"};
        String[] datesFinish = {"2017-10-13T13:35:12", "2017-10-13T17:35:12", "2017-10-13T16:35:12", "2017-10-14T21:35:12", "2017-10-14T12:35:12"};

        for (int i = 0; i < registrationNumbers.length; i++) {
            ArchivedSpot spot = new ArchivedSpot(registrationNumbers[i], driverTypes[i], timeDateFormat.parse(datesBegin[i]), timeDateFormat.parse(datesFinish[i]));
            archivedSpotRepoStub.save(spot);
        }

        Date date = dayDateFormat.parse("2017-10-14");
        Stream<ArchivedSpot> spotStream = archivedSpotRepoStub.getAllByDay(date);

        assertEquals(2, spotStream.count());

        date = dayDateFormat.parse("2017-10-13");
        spotStream = archivedSpotRepoStub.getAllByDay(date);

        assertEquals(3, spotStream.count());

        date = dayDateFormat.parse("2017-10-1");
        spotStream = archivedSpotRepoStub.getAllByDay(date);

        assertEquals(0, spotStream.count());
    }
}
