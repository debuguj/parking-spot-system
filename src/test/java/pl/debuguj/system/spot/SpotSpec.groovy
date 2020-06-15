package pl.debuguj.system.spot

import spock.lang.Shared
import spock.lang.Specification

import java.text.SimpleDateFormat

class SpotSpec extends Specification {

    @Shared
            simpleDateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    def registrationNumber = "WZE12345"
    @Shared
    Date startDate

    def setupSpec() {
        startDate = simpleDateTimeFormatter.parse("2017-10-12T10:15:10")
    }

    def 'creating new spot with validated input'() {
        given:
        def spot = new Spot(registrationNumber, DriverType.REGULAR, startDate)
        expect:
        spot.vehiclePlate == "WZE12345"
        spot.beginDate == simpleDateTimeFormatter.parse("2017-10-12T10:15:10")
        spot.driverType == DriverType.REGULAR
    }

}
