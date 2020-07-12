package pl.debuguj.system.operator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.debuguj.system.exceptions.VehicleNotFoundException;
import pl.debuguj.system.spot.DriverType;
import pl.debuguj.system.spot.Spot;
import pl.debuguj.system.spot.SpotRepo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = OperatorController.class)
public class OperatorControllerTest {

    @Value("${uri.operator.check}")
    private String uriCheckVehicle;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SpotRepo spotRepo;

    private final Spot spot = new Spot("WCC12345", DriverType.REGULAR, LocalDateTime.now());

    @Test
    @DisplayName("Should return VehicleNotFoundException because vehicle is not active")
    public void shouldReturnExceptionBecauseVehicleIsNotActive() throws Exception {
        //WHEN
        when(spotRepo.findVehicleByPlate(spot.getVehiclePlate())).thenThrow(new VehicleNotFoundException(spot.getVehiclePlate()));

        mockMvc.perform(get(uriCheckVehicle, spot.getVehiclePlate())
                .contentType(MediaType.APPLICATION_JSON))
                //THEN
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("Should return status OK because vehicle is active in db")
    public void shouldReturnOkBecauseVehicleIsActive() throws Exception {
        //WHEN
        when(spotRepo.findVehicleByPlate(spot.getVehiclePlate())).thenReturn(Optional.of(spot));

        mockMvc.perform(get(uriCheckVehicle, spot.getVehiclePlate())
                .contentType("application/hal+json"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                //.andExpect(content().json(objectMapper.writeValueAsString(spot)))
                .andDo(print())
                .andReturn();
    }
}
