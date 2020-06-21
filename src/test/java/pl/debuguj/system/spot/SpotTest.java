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
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpotTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void shouldBeSerializable() {
        final Spot spot = new Spot("WZE12345", DriverType.REGULAR, new Date());
        final Spot other = (Spot) SerializationUtils.deserialize(SerializationUtils.serialize(spot));

        Objects.requireNonNull(other);
        assertThat(other.getVehiclePlate()).isEqualTo(spot.getVehiclePlate());
        assertThat(other.getDriverType()).isEqualTo(spot.getDriverType());
        assertThat(other.getBeginDate()).isEqualTo(spot.getBeginDate());
    }

    @Test
    public void shouldAcceptCorrectParameters() {
        final Date date = new Date();
        final Spot spot = createSimpleSpot("WZE12345", date);

        assertThat(spot.getVehiclePlate()).isEqualTo("WZE12345");
        assertThat(spot.getDriverType()).isEqualTo(DriverType.REGULAR);
        assertThat(spot.getDriverType()).isNotEqualTo(DriverType.VIP);
        assertThat(spot.getBeginDate()).isEqualTo(date);
    }

    @Test
    public void shouldNotReturnErrorsForSpot() {
        final Spot spot = createSimpleSpot();

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"e12345", "", " ", "     ", "12345", "qeee12345", "registrationNo", "qwe123456",
            "qwe123", "E12345", "12345", "QEEE12345", "registrationNo", "QWE123456", "QWE123"})
    void shouldReturnViolationBecauseOfIncorrectRegistrationNumber(String number) {
        final Spot spot = new Spot(number, DriverType.REGULAR, new Date());

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.size() <= 2, "Failure no:" + violations.size());
    }

    private Spot createSimpleSpot() {
        return new Spot("WZE12345", DriverType.REGULAR, new Date());
    }

    private Spot createSimpleSpot(String registrationNumber, Date startDate) {
        return new Spot(registrationNumber, DriverType.REGULAR, startDate);
    }
}
