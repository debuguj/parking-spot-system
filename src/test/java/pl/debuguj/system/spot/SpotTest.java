package pl.debuguj.system.spot;

import org.junit.jupiter.api.BeforeAll;
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
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SpotTest {

    private Validator validator;

    private static final LocalDateTime defaultDateTime = LocalDateTime.now();
    private static final String defaultRegistrationNumber = "WZE12345";
    private Spot spot;

    @BeforeAll
    void init() {
        spot = new Spot(defaultRegistrationNumber, DriverType.REGULAR, defaultDateTime);
    }

    @BeforeEach
    void setup() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    void shouldBeSerializable() {
        final Spot other = (Spot) SerializationUtils.deserialize(SerializationUtils.serialize(spot));

        Objects.requireNonNull(other);
        assertEquals(other.getVehiclePlate(), spot.getVehiclePlate());
        assertEquals(other.getDriverType(), spot.getDriverType());
        assertEquals(other.getBeginDatetime(), spot.getBeginDatetime());
    }

    @Test
    void shouldAcceptCorrectParameters() {
        assertEquals(spot.getVehiclePlate(), defaultRegistrationNumber);
        assertEquals(spot.getDriverType(), DriverType.REGULAR);
        assertEquals(spot.getBeginDatetime(), defaultDateTime);
    }

    @Test
    public void shouldNotReturnErrorsForSpot() {
        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"e12345", "", " ", "     ", "12345", "qeee12345", "registrationNo", "qwe123456",
            "qwe123", "E12345", "12345", "QEEE12345", "registrationNo", "QWE123456", "QWE123"})
    void shouldReturnViolationBecauseOfIncorrectRegistrationNumber(String number) {
        final Spot spot = new Spot(number, DriverType.REGULAR, defaultDateTime);

        Set<ConstraintViolation<Spot>> violations = this.validator.validate(spot);
        assertTrue(violations.size() <= 2, "Failure no:" + violations.size());
    }
}
