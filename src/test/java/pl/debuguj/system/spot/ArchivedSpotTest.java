package pl.debuguj.system.spot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.util.SerializationUtils;
import pl.debuguj.system.external.CurrencyRate;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArchivedSpotTest {

    private CurrencyRate currencyRate = CurrencyRate.PLN;
    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void shouldBeSerializable() {
        final Date[] startStopTimestamps = createStartStopTimestamps();

        final ArchivedSpot archivedSpot = new ArchivedSpot("WZE12345", DriverType.REGULAR, startStopTimestamps[0], startStopTimestamps[1]);
        ArchivedSpot other = (ArchivedSpot) SerializationUtils.deserialize(SerializationUtils.serialize(archivedSpot));

        Objects.requireNonNull(other);
        assertThat(other.getUuid()).isEqualTo(archivedSpot.getUuid());
        assertThat(other.getVehiclePlate()).isEqualTo(archivedSpot.getVehiclePlate());
        assertThat(other.getDriverType()).isEqualTo(archivedSpot.getDriverType());
        assertThat(other.getBeginDate()).isEqualTo(archivedSpot.getBeginDate());
        assertThat(other.getFinishDate()).isEqualTo(archivedSpot.getFinishDate());
    }

    @Test
    public void shouldAcceptCorrectParameters() {
        final Date[] startStopTimestamps = createStartStopTimestamps();
        ArchivedSpot archivedSpot = new ArchivedSpot("WZE12345", DriverType.REGULAR, startStopTimestamps[0], startStopTimestamps[1]);

        assertThat(archivedSpot.getVehiclePlate()).isEqualTo("WZE12345");
        assertThat(archivedSpot.getDriverType()).isEqualTo(DriverType.REGULAR);
        assertThat(archivedSpot.getDriverType()).isNotEqualTo(DriverType.VIP);
        assertThat(archivedSpot.getBeginDate()).isEqualTo(startStopTimestamps[0]);
        assertThat(archivedSpot.getFinishDate()).isEqualTo(startStopTimestamps[1]);
    }

    @Test
    public void shouldReturnEmptyOptionalBecauseOfNullFinishDate() {
        final ArchivedSpot archivedSpot = new ArchivedSpot("WZE12345", DriverType.REGULAR, new Date(), null);

        assertEquals(Optional.empty(), archivedSpot.getFee());
    }

    @Test
    public void shouldThrowExceptionBecauseFinishDateIsBeforeStartDate() {
        final Date[] incorrectStartStopTimestamps = createIncorrectStartStopTimeStamps();
        final Spot spot = new Spot("WZE12345", DriverType.REGULAR, incorrectStartStopTimestamps[0]);

        Exception exception = assertThrows(DateTimeException.class, () ->
                new ArchivedSpot(spot, incorrectStartStopTimestamps[1]));

        assertEquals("Finish time is before start time", exception.getMessage());
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
    public void shouldReturnCorrectFeeForRegularDriver(String startTime, String stopTime, String fee) throws Exception {
        final Date[] startStopTimestamps = createStartStopTimestampsByDates(startTime, stopTime);
        final BigDecimal feeValue = new BigDecimal(fee);

        ArchivedSpot archivedSpot = new ArchivedSpot("WZE12345", DriverType.REGULAR, startStopTimestamps[0], startStopTimestamps[1]);

        archivedSpot.getFee(currencyRate).ifPresent(f -> assertEquals(feeValue, f));
    }

    @ParameterizedTest
    @CsvSource({"2020-10-12T11:15:48, 2020-10-12T11:35:12, 0.0",
            "2020-10-12T11:15:48, 2020-10-12T12:35:12, 2.0",
            "2020-10-12T11:15:48, 2020-10-12T13:35:12, 5.0",
            "2020-10-12T11:15:48, 2020-10-12T16:35:12, 26.4",
            "2020-10-12T00:15:48, 2020-10-12T15:35:12, 1747.6",
            "2020-10-12T11:15:48, 2020-10-13T11:14:12, 44887.0"
    })
    public void shouldReturnCorrectFeeForVipDriver(String startTime, String stopTime, String fee) throws Exception {
        final Date[] startStopTimestamps = createStartStopTimestampsByDates(startTime, stopTime);
        final BigDecimal feeValue = new BigDecimal(fee);

        ArchivedSpot archivedSpot = new ArchivedSpot("WZE12345", DriverType.VIP, startStopTimestamps[0], startStopTimestamps[1]);

        archivedSpot.getFee(currencyRate).ifPresent(f -> assertEquals(feeValue, f));
    }

    private Date[] createStartStopTimestamps() {
        final Calendar calendar = Calendar.getInstance();
        final Date startDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 2);
        final Date stopDate = calendar.getTime();

        return new Date[]{startDate, stopDate};
    }

    private Date[] createIncorrectStartStopTimeStamps() {
        final Calendar calendar = Calendar.getInstance();
        final Date stopDate = calendar.getTime();
        calendar.add(Calendar.HOUR, 2);
        final Date startDate = calendar.getTime();

        return new Date[]{startDate, stopDate};
    }

    private Date[] createStartStopTimestampsByDates(String startTime, String stopTime) throws ParseException {
        final SimpleDateFormat simpleDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        final Date start = simpleDateTimeFormatter.parse(startTime);
        final Date stop = simpleDateTimeFormatter.parse(stopTime);
        return new Date[]{start, stop};
    }
}





