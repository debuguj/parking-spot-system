package pl.debuguj.system.operator

import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import pl.debuguj.system.exceptions.VehicleCannotBeRegisteredInDbException
import pl.debuguj.system.exceptions.VehicleNotFoundException
import pl.debuguj.system.spot.DriverType
import pl.debuguj.system.spot.Spot
import pl.debuguj.system.spot.SpotRepo
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ContextConfiguration
@WebMvcTest(controllers = [OperatorController])
class OperatorControllerSpec  extends Specification {

    @Value('${uri.operator.check}')
    private String uriCheckVehicle

    @Autowired
    private MockMvc mockMvc
    @Autowired
    private ObjectMapper objectMapper

    @SpringBean
    SpotRepo spotRepo = Stub()

    @Shared
    Spot spot
    static String vehiclePlate = 'WZE12345'

    def setupSpec() {
        spot = new Spot(vehiclePlate, DriverType.REGULAR, LocalDateTime.now())
    }

    def 'MockMvc should be created'() {
        expect: 'mockMvc must be not null'
        mockMvc
    }

    def 'should return VehicleNotFoundException because vehicle is not active'(){
        given: 'exception returned by findVehicleByPlate method'
        spotRepo.findVehicleByPlate(_ as String) >> { throw new VehicleNotFoundException('WZE12345') }

        when: 'perform request'
        def results = mockMvc.perform(get(uriCheckVehicle, vehiclePlate)
                .contentType('application/json'))

        then: 'return status NotFound'
        results.andExpect(status().isNotFound())

        and: 'correct type of context negotiation'
        results.andExpect(content().contentType('application/json'))
    }

    def 'should return status OK because vehicle is active in db'(){
        given: 'exception returned by findVehicleByPlate method'
        spotRepo.findVehicleByPlate(_ as String) >> Optional.of(spot)

        when: 'perform request'
        def results = mockMvc.perform(get(uriCheckVehicle, vehiclePlate)
                .contentType('application/json'))

        then: 'return status 200'
        results.andExpect(status().isOk())

        and: 'correct type of context negotiation'
        results.andExpect(content().contentType('application/json'))
    }
}
