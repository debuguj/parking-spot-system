package pl.debuguj.system.spot;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.debuguj.system.driver.Fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.Assert.*;

class ArchivedSpotRepoInMemoryTest {

    private final ArchivedSpotRepo sut = new ArchivedSpotRepoInMemory();

    private static final LocalDateTime defBeginDateTime = LocalDateTime.now();
    private static final LocalDateTime defEndDateTime = LocalDateTime.now().plusHours(2L);
    private static final String defaultVehiclePlate = "WZE12345";
    private static ArchivedSpot archivedSpot;

    private final String basicDateString = "2020-06-21";

    @BeforeAll
    static void init() {
        archivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginDateTime, defEndDateTime);
    }

    @Test
    public void shouldReturnEmptyOptional() {
        Optional<ArchivedSpot> opt = sut.save(null);
        assertFalse(opt.isPresent());
    }

    @Test
    public void shouldSaveNewArchivedSpot() {

        Optional<ArchivedSpot> returned = sut.save(archivedSpot);

        assertTrue(returned.isPresent());

        returned.ifPresent(as -> {
            assertEquals(archivedSpot.getVehiclePlate(), as.getVehiclePlate());
            assertEquals(archivedSpot.getBeginLocalDateTime(), as.getBeginLocalDateTime());
            assertEquals(archivedSpot.getEndLocalDateTime(), as.getEndLocalDateTime());
        });
    }

    @Test
    public void shouldFindAllByDate() {

        createArchiveSpotsList().forEach(sut::save);

        LocalDate date = LocalDate.parse(basicDateString);

        List<ArchivedSpot> spotStream = sut.getAllByDay(date);
        assertEquals(2, spotStream.size());

        date = date.plusDays(1L);
        spotStream = sut.getAllByDay(date);
        assertEquals(3, spotStream.size());

        date = LocalDate.now().plusDays(2L);
        spotStream = sut.getAllByDay(date);

        assertEquals(0, spotStream.size());
    }

    private List<ArchivedSpot> createArchiveSpotsList() {
        List<ArchivedSpot> list = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime ldt = LocalDate.parse(basicDateString, dtf).atStartOfDay();

        list.add(new ArchivedSpot("WWW66666", DriverType.REGULAR, ldt, ldt.plusHours(2L)));
        list.add(new ArchivedSpot("WSQ77777", DriverType.REGULAR, ldt, ldt.plusHours(3L)));
        list.add(new ArchivedSpot("QAZ88888", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(4L)));
        list.add(new ArchivedSpot("EDC99999", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(2L)));
        list.add(new ArchivedSpot("FDR99998", DriverType.REGULAR, ldt.plusDays(1L), ldt.plusDays(1L).plusHours(1L)));

        return list;
    }
}
