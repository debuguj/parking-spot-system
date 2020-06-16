package pl.debuguj.system.spot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.SerializationUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpotTest {

    private final SimpleDateFormat simpleDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final String registrationNumber = "WZE12345";
    private Date startDate;

    private Validator validator;

    public SpotTest() {
        try {
            startDate = simpleDateTimeFormatter.parse("2017-10-12T10:15:10");
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
        Spot spot = new Spot(registrationNumber, DriverType.REGULAR, startDate);
        Spot other = (Spot) SerializationUtils.deserialize(SerializationUtils.serialize(spot));

        Objects.requireNonNull(other);
        assertThat(other.getVehiclePlate()).isEqualTo(spot.getVehiclePlate());
        assertThat(other.getDriverType()).isEqualTo(spot.getDriverType());
        assertThat(other.getBeginDate()).isEqualTo(spot.getBeginDate());
    }

    @Test
    public void shouldBeSetCorrectParameters() {
        Spot spot = new Spot(registrationNumber, DriverType.REGULAR, startDate);

        assertThat(spot.getVehiclePlate()).isEqualTo(registrationNumber);
        assertThat(spot.getDriverType()).isEqualTo(DriverType.REGULAR);
        assertThat(spot.getDriverType()).isNotEqualTo(DriverType.VIP);
        assertThat(spot.getBeginDate()).isEqualTo(startDate);
    }

    @Test
    public void shouldNotReturnErrorsForSpot() {
        Spot spot = new Spot(registrationNumber, DriverType.REGULAR, startDate);

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void shouldReturnOneViolationBecauseOfRegistrationPlateAsNullParameter() {
        Spot spot = new Spot(null, DriverType.REGULAR, startDate);

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.size() > 0);
    }

    @Test
    void shouldReturnOneViolationBecauseOfDriverTypeAsNull() {
        Spot spot = new Spot(registrationNumber, null, startDate);

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.size() > 0);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"e12345",
            "",
            " ",
            "     ",
            "12345",
            "qeee12345",
            "registrationNo",
            "qwe123456",
            "qwe123",
            "E12345",
            "12345",
            "QEEE12345",
            "registrationNo",
            "QWE123456",
            "QWE123"})
        //@CsvFileSource()
    void shouldReturnViolationBecauseOfIncorrectRegistrationNumber(String number) {
        Spot spot = new Spot(number, DriverType.REGULAR, startDate);

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.size() <= 2, "Failure no:" + violations.size());
    }

    @Test
    void shouldReturnOneViolationBecauseOfStartDateAsNull() {
        Spot spot = new Spot(registrationNumber, DriverType.REGULAR, null);

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.size() > 0);
    }

}
