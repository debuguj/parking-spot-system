package pl.debuguj.system.spot;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

class ArchivedSpotRepoInMemoryTest {

    private final ArchivedSpotRepo sut = new ArchivedSpotRepoInMemory();

    @Test
    public void shouldReturnEmptyOptional() {
        Optional<ArchivedSpot> opt = sut.save(null);
        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldSaveNewArchivedSpot() {
        final ArchivedSpot archivedSpot = createSimpleArchivedSpot();

        Optional<ArchivedSpot> returned = sut.save(archivedSpot);

        assertTrue(returned.isPresent());

        returned.ifPresent(as -> {
            assertEquals(archivedSpot.getUuid(), as.getUuid());
            assertEquals(archivedSpot.getBeginDate(), as.getBeginDate());
            assertEquals(archivedSpot.getFinishDate(), as.getFinishDate());
            assertEquals(archivedSpot.getDriverType(), as.getDriverType());
        });
    }

    @Test
    public void shouldFindAllByDate() throws Exception {

        List<ArchivedSpot> loadings = createArchiveSpotsList();
        loadings.forEach(sut::save);

        Date date = createDateByGivenString("2020-10-14");
        List<ArchivedSpot> spotStream = sut.getAllByDay(date);

        assertEquals(2, spotStream.size());

        date = createDateByGivenString("2020-10-13");
        spotStream = sut.getAllByDay(date);

        assertEquals(3, spotStream.size());

        date = createDateByGivenString("2020-10-1");
        spotStream = sut.getAllByDay(date);

        assertEquals(0, spotStream.size());
    }

    private Date createDateByGivenString(String stringDate) throws ParseException {
        final SimpleDateFormat dayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dayDateFormat.parse(stringDate);
    }

    private List<ArchivedSpot> createArchiveSpotsList() throws Exception {
        Date[] startTimestamps = createStartTimestamps();
        Date[] stopTimestamps = createStopTimestamps();

        List<ArchivedSpot> list = new ArrayList<>();
        list.add(new ArchivedSpot("WWW66666", DriverType.REGULAR, startTimestamps[0], stopTimestamps[0]));
        list.add(new ArchivedSpot("WSQ77777", DriverType.REGULAR, startTimestamps[1], stopTimestamps[1]));
        list.add(new ArchivedSpot("QAZ88888", DriverType.REGULAR, startTimestamps[2], stopTimestamps[2]));
        list.add(new ArchivedSpot("EDC99999", DriverType.REGULAR, startTimestamps[3], stopTimestamps[3]));
        list.add(new ArchivedSpot("FDR99998", DriverType.REGULAR, startTimestamps[4], stopTimestamps[4]));

        return list;
    }

    private Date[] createStopTimestamps() throws ParseException {
        final SimpleDateFormat timeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        return new Date[]{
                timeDateFormat.parse("2020-10-13T13:35:12"),
                timeDateFormat.parse("2020-10-13T17:35:12"),
                timeDateFormat.parse("2020-10-13T16:35:12"),
                timeDateFormat.parse("2020-10-14T21:35:12"),
                timeDateFormat.parse("2020-10-14T12:35:12")
        };
    }

    private Date[] createStartTimestamps() throws ParseException {
        final SimpleDateFormat timeDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        return new Date[]{
                timeDateFormat.parse("2020-10-13T10:25:48"),
                timeDateFormat.parse("2020-10-13T12:25:48"),
                timeDateFormat.parse("2020-10-13T15:25:48"),
                timeDateFormat.parse("2020-10-14T20:25:48"),
                timeDateFormat.parse("2020-10-14T11:15:48"),
        };
    }

    private ArchivedSpot createSimpleArchivedSpot() {
        Date[] startStopTimestamps = createStartStopTimestampsWith2HourPeriod();
        return new ArchivedSpot("WZE12345", DriverType.REGULAR, startStopTimestamps[0], startStopTimestamps[1]);
    }

    private Date[] createStartStopTimestampsWith2HourPeriod() {

        final Calendar calendar = Calendar.getInstance();
        final Date startDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 2);
        final Date stopDate = calendar.getTime();

        return new Date[]{startDate, stopDate};
    }
}
