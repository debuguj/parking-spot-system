package pl.debuguj.system.spot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArchivedSpotTest {

    private final SimpleDateFormat simpleDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final String registrationNumber = "WZE12345";
    private Date startDate;
    private Date finishDate;
    private Date incorrectFinishDate;
    private CurrencyRate currencyRate = new CurrencyRate(new BigDecimal(1.0), Currency.getInstance(new Locale("pl", "PL")));
    private Validator validator;

    public ArchivedSpotTest() {
        try {
            startDate = simpleDateTimeFormatter.parse("2017-10-12T10:15:10");
            finishDate = simpleDateTimeFormatter.parse("2017-10-12T15:15:10");
            incorrectFinishDate = simpleDateTimeFormatter.parse("2017-10-10T15:15:10");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setup() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void shouldBeSerializable() {
        ArchivedSpot archivedSpot = new ArchivedSpot(registrationNumber, DriverType.REGULAR, startDate, finishDate);
        ArchivedSpot other = (ArchivedSpot) SerializationUtils.deserialize(SerializationUtils.serialize(archivedSpot));

        assertThat(other.getUuid()).isEqualTo(archivedSpot.getUuid());
        assertThat(other.getVehiclePlate()).isEqualTo(archivedSpot.getVehiclePlate());
        assertThat(other.getDriverType()).isEqualTo(archivedSpot.getDriverType());
        assertThat(other.getBeginDate()).isEqualTo(archivedSpot.getBeginDate());
        assertThat(other.getFinishDate()).isEqualTo(archivedSpot.getFinishDate());
    }

    @Test
    public void shouldBeSetCorrectParameters() {
        ArchivedSpot archivedSpot = new ArchivedSpot(registrationNumber, DriverType.REGULAR, startDate, finishDate);

        assertThat(archivedSpot.getVehiclePlate()).isEqualTo(registrationNumber);
        assertThat(archivedSpot.getDriverType()).isEqualTo(DriverType.REGULAR);
        assertThat(archivedSpot.getDriverType()).isNotEqualTo(DriverType.VIP);
        assertThat(archivedSpot.getBeginDate()).isEqualTo(startDate);
        assertThat(archivedSpot.getFinishDate()).isEqualTo(finishDate);
    }

    @Test
    public void shouldReturnEmptyOptionalBecauseOfNullFinishDate() {
        ArchivedSpot archivedSpot = new ArchivedSpot(registrationNumber, DriverType.REGULAR, startDate, null);

        assertEquals(Optional.empty(), archivedSpot.getFee());
    }

    @Test
    public void shouldReturnEmptyOptionalBecauseOfIncorrectFinishDate() {
        ArchivedSpot archivedSpot = new ArchivedSpot(registrationNumber, DriverType.REGULAR, startDate, incorrectFinishDate);

        assertEquals(Optional.empty(), archivedSpot.getFee());
    }

    @Test
    public void shouldReturnCorrectFeeForRegularDriver() throws Exception {
        String[] startDates = {"2017-10-12T11:15:48", "2017-10-12T11:15:48",
                "2017-10-12T11:15:48", "2017-10-12T11:15:48",
                "2017-10-12T00:15:48", "2017-10-12T11:15:48", "2020-12-12T10:10:10"
        };
        String[] finishDates = {"2017-10-12T11:35:12", "2017-10-12T12:35:12",
                "2017-10-12T13:35:12", "2017-10-12T16:35:12",
                "2017-10-12T15:35:12", "2017-10-13T11:14:12", "2020-12-12T22:13:10"
        };
        BigDecimal[] returnedFee = {new BigDecimal("1.0"), new BigDecimal("3.0"),
                new BigDecimal("7.0"), new BigDecimal("63.0"),
                new BigDecimal("65535.0"), new BigDecimal("16777215.0"), new BigDecimal("8191.0")
        };

        for (int i = 0; i < startDates.length; i++) {
            Date start = simpleDateTimeFormatter.parse(startDates[i]);
            Date stop = simpleDateTimeFormatter.parse(finishDates[i]);
            ArchivedSpot archivedSpot = new ArchivedSpot(registrationNumber, DriverType.REGULAR, start, stop);

            assertEquals(returnedFee[i], archivedSpot.getFee(currencyRate).get());
        }
    }

    @Test
    public void shouldReturnCorrectFeeForVipDriver() throws Exception {
        String[] beginDates = {"2017-10-12T11:15:48", "2017-10-12T11:15:48",
                "2017-10-12T11:15:48", "2017-10-12T11:15:48",
                "2017-10-12T00:15:48", "2017-10-12T11:15:48"
        };
        String[] endDates = {"2017-10-12T11:35:12", "2017-10-12T12:35:12",
                "2017-10-12T13:35:12", "2017-10-12T16:35:12",
                "2017-10-12T15:35:12", "2017-10-13T11:14:12"
        };
        BigDecimal[] returnedFee = {new BigDecimal("0.0"), new BigDecimal("2.0"),
                new BigDecimal("5.0"), new BigDecimal("26.4"),
                new BigDecimal("1747.6"), new BigDecimal("44887.0")
        };

        for (int i = 0; i < beginDates.length; i++) {
            Date start = simpleDateTimeFormatter.parse(beginDates[i]);
            Date stop = simpleDateTimeFormatter.parse(endDates[i]);
            ArchivedSpot archivedSpot = new ArchivedSpot(registrationNumber, DriverType.VIP, start, stop);

            assertEquals(returnedFee[i], archivedSpot.getFee(currencyRate).get());
        }
    }
}





