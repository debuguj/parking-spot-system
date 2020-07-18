package pl.debuguj.system.spot;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.SerializationUtils;
import pl.debuguj.system.exceptions.IncorrectFinishDateException;
import pl.debuguj.system.external.CurrencyRate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ArchivedSpotTest {

    private final CurrencyRate currencyRate = CurrencyRate.PLN;

    private static final LocalDateTime defBeginDateTime = LocalDateTime.now();
    private static final LocalDateTime defEndDate = LocalDateTime.now().plusHours(2L);
    private static final String defaultVehiclePlate = "WZE12345";
    private static ArchivedSpot archivedSpot;

    @BeforeAll
    static void init() {
        archivedSpot = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginDateTime, defEndDate);
    }

    @Test
    public void shouldBeSerializable() {

        final ArchivedSpot other = (ArchivedSpot) SerializationUtils.deserialize(SerializationUtils.serialize(archivedSpot));

        Objects.requireNonNull(other);
        assertEquals(other.getUuid(), archivedSpot.getUuid());
        assertEquals(other.getVehiclePlate(), archivedSpot.getVehiclePlate());
        assertEquals(other.getDriverType(), archivedSpot.getDriverType());
        assertEquals(other.getBeginLocalDateTime(), archivedSpot.getBeginLocalDateTime());
        assertEquals(other.getEndLocalDateTime(), archivedSpot.getEndLocalDateTime());
    }

    @Test
    public void shouldAcceptCorrectParameters() {

        assertEquals(archivedSpot.getVehiclePlate(), defaultVehiclePlate);
        assertEquals(archivedSpot.getDriverType(), DriverType.REGULAR);
        assertNotEquals(archivedSpot.getDriverType(), DriverType.VIP);
        assertEquals(archivedSpot.getBeginLocalDateTime(), defBeginDateTime);
        assertEquals(archivedSpot.getEndLocalDateTime(), defEndDate);
    }

    @Test
    public void shouldReturnEmptyOptionalBecauseOfNullFinishDate() {
        final ArchivedSpot archivedSpot
                = new ArchivedSpot(defaultVehiclePlate, DriverType.REGULAR, defBeginDateTime, null);

        assertEquals(Optional.empty(), archivedSpot.getFee());
    }

    @Test
    public void shouldThrowExceptionBecauseFinishDateIsBeforeStartDate() {
        final LocalDateTime incorrectLocalDateTime = defBeginDateTime.minusHours(2L);
        final Spot spot = new Spot(defaultVehiclePlate, DriverType.REGULAR, defBeginDateTime);

        Exception exception = assertThrows(IncorrectFinishDateException.class, () ->
                new ArchivedSpot(spot, incorrectLocalDateTime));

        assertThat(exception.getMessage().contains("Finish date:"));
    }

    @ParameterizedTest
    @CsvSource({"2020-06-12T11:15:48, 2020-06-12T11:35:12, 1.0",
            "2020-06-12T11:15:48, 2020-06-12T12:35:12, 3.0",
            "2020-06-12T11:15:48, 2020-06-12T13:35:12, 7.0",
            "2020-06-12T11:15:48, 2020-06-12T16:35:12, 63.0",
            "2020-06-12T00:15:48, 2020-06-12T15:35:12, 65535.0",
            "2020-06-12T11:15:48, 2020-06-13T11:14:12, 16777215.0",
            "2020-06-12T10:10:10, 2020-06-12T22:13:10, 8191.0"
    })
    public void shouldReturnCorrectFeeForRegularDriver(ArgumentsAccessor arguments) {
        ArchivedSpot archivedSpot = new ArchivedSpot(
                defaultVehiclePlate,
                DriverType.REGULAR,
                arguments.get(0, LocalDateTime.class),
                arguments.get(1, LocalDateTime.class));

        archivedSpot.getFee(currencyRate).ifPresent(f -> assertEquals(arguments.get(2, BigDecimal.class), f));
    }

    @ParameterizedTest
    @CsvSource({"2020-10-12T11:15:48, 2020-10-12T11:35:12, 0.0",
            "2020-10-12T11:15:48, 2020-10-12T12:35:12, 2.0",
            "2020-10-12T11:15:48, 2020-10-12T13:35:12, 5.0",
            "2020-10-12T11:15:48, 2020-10-12T16:35:12, 26.4",
            "2020-10-12T00:15:48, 2020-10-12T15:35:12, 1747.6",
            "2020-10-12T11:15:48, 2020-10-13T11:14:12, 44887.0"
    })
    public void shouldReturnCorrectFeeForVipDriver(ArgumentsAccessor arguments) {
        ArchivedSpot archivedSpot = new ArchivedSpot(
                defaultVehiclePlate,
                DriverType.VIP,
                arguments.get(0, LocalDateTime.class),
                arguments.get(1, LocalDateTime.class));

        archivedSpot.getFee(currencyRate).ifPresent(f -> assertEquals(arguments.get(2, BigDecimal.class), f));
    }
}





