package pl.debuguj.system.owner

import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.debuguj.system.external.systems.CurrencyRate
import pl.debuguj.system.external.systems.CurrencyRateHandler
import pl.debuguj.system.spot.ArchivedSpot
import pl.debuguj.system.spot.BaseArchivedSpotRepo
import pl.debuguj.system.spot.DriverType
import pl.debuguj.system.spot.Spot
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [OwnerController])
class OwnerControllerSpec extends Specification {

    @Value('${uri.owner.income}')
    private String uriCheckDailyIncome
    @Value('${date.format}')
    private String datePattern

    @Autowired
    private MockMvc mockMvc
    @Autowired
    private ObjectMapper objectMapper
    @SpringBean
    private BaseArchivedSpotRepo archivedSpotRepo = Stub()
    @SpringBean
    private CurrencyRateHandler currencyRateHandler = Stub()

    @Shared
    Spot spot
    static String vehiclePlate = 'WZE12345'

    def setupSpec() {
        spot = new Spot(vehiclePlate, DriverType.REGULAR, LocalDateTime.now())
    }

    def 'MockMvc should be created'() {
        expect: 'mockMve must be not null'
        mockMvc
    }

    def 'should return NotFoundException because any vehicle was registered'(){
        when: 'perform request'
        def results = mockMvc.perform(get(uriCheckDailyIncome, spot.getBeginDatetime().format(DateTimeFormatter.ofPattern(datePattern)))
                .contentType(MediaType.APPLICATION_JSON))

        then: 'return status NotFound'
        results.andExpect(status().isNotFound())

        and: 'correct type of context negotiation'
        results.andExpect(content().contentType("application/json"))
    }

    def 'should return income for one vehicle'(){
        given: 'one archived spot for test'
        archivedSpotRepo.getAllByDay(_ as LocalDate) >> createArchivedSpot(spot)

        and: 'PLN as default currency and rate'
        currencyRateHandler.getCurrencyRate() >> CurrencyRate.PLN

        when: 'perform request'
        def results = mockMvc.perform(get(uriCheckDailyIncome, spot.getBeginDatetime().toLocalDate())
                .contentType('application/json'))

        then: 'return status 200'
        results.andExpect(status().isOk())

        and: 'correct type of context negotiation'
        results.andExpect(content().contentType('application/json'))
        results.andExpect(content().json(objectMapper.writeValueAsString(createIncome(spot))))
    }

    def createArchivedSpot(Spot spot){
        Collections.singletonList(new ArchivedSpot(spot, spot.getBeginDatetime().plusHours(2L)))
    }

    def createIncome(Spot spot){
        new DailyIncome(spot.getBeginDatetime().toLocalDate(), new BigDecimal("3.0"))
    }
}
