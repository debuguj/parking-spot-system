package pl.debuguj.system.spot

import spock.lang.Shared
import spock.lang.Specification

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory
import java.text.ParseException
import java.text.SimpleDateFormat

class ArchivedSpotSpec extends Specification {

    @Shared
    Validator validator

    def setupSpec() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

//    def 'Should be serialized'() {
//        given:
//            Spot spot = new Spot(registrationNumber, DriverType.REGULAR, startDate);
//            def spot = createSimpleSpot("WZE12345", date)
//        expect:
//            spot.vehiclePlate == "WZE12345"
//            spot.beginDate == date
//            spot.driverType == DriverType.REGULAR
//    }
//
//
//
//    private Date[] createStartStopTimestamps() {
//        final Calendar calendar = Calendar.getInstance();
//        final Date startDate = calendar.getTime();
//        calendar.add(Calendar.HOUR, 2);
//        final Date stopDate = calendar.getTime();
//
//        return new Date[]{startDate, stopDate};
//    }
//
//    private Date[] createIncorrectStartStopTimeStamps() {
//        final Calendar calendar = Calendar.getInstance();
//        final Date stopDate = calendar.getTime();
//        calendar.add(Calendar.HOUR, 2);
//        final Date startDate = calendar.getTime();
//
//        return new Date[]{startDate, stopDate};
//    }
//
//    private Date[] createStartStopTimestampsByDates(String startTime, String stopTime) throws ParseException {
//        final SimpleDateFormat simpleDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//
//        final Date start = simpleDateTimeFormatter.parse(startTime);
//        final Date stop = simpleDateTimeFormatter.parse(stopTime);
//        return new Date[]{start, stop};
//    }
}
