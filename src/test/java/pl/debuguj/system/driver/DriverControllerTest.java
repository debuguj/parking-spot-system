package pl.debuguj.system.driver;

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
import pl.debuguj.system.exceptions.VehicleActiveInDbException;
import pl.debuguj.system.exceptions.VehicleCannotBeRegisteredInDbException;
import pl.debuguj.system.exceptions.VehicleNotExistsInDbException;
import pl.debuguj.system.spot.*;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = DriverController.class)
public class DriverControllerTest {

    @Value("${uri.driver.start}")
    private String uriStartMeter;
    @Value("${uri.driver.stop}")
    private String uriStopMeter;
    @Value("${date.time.format}")
    private String dateTimePattern;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SpotRepo spotRepo;
    @MockBean
    private ArchivedSpotRepo archivedSpotRepo;

    private final Spot spot = new Spot("WCC12345", DriverType.REGULAR, new Date());

    @Test
    @DisplayName("Should return a correct payload after request")
    public void shouldReturnCorrectPayloadAndFormatAndValue() throws Exception {
        //WHEN
        when(spotRepo.findByVehiclePlate(spot.getVehiclePlate())).thenReturn(Optional.empty());
        when(spotRepo.save(spot)).thenReturn(Optional.of(spot));

        mockMvc.perform(post(uriStartMeter)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(spot)))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(spot)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("Should return redirection because vehicle is active")
    public void shouldReturnRedirectionBecauseOfVehicleIsActive() throws Exception {
        //WHEN
        when(spotRepo.findByVehiclePlate(spot.getVehiclePlate()))
                .thenThrow(new VehicleActiveInDbException(spot.getVehiclePlate()));

        mockMvc.perform(post(uriStartMeter)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(spot)))
                //THEN
                .andExpect(status().is3xxRedirection())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("Should return lock because a vehicle cannot be registered")
    public void shouldReturnLockedBecauseOfVehicleCannotBeRegistered() throws Exception {
        //WHEN
        when(spotRepo.findByVehiclePlate(spot.getVehiclePlate())).thenReturn(Optional.empty());
        when(spotRepo.save(spot)).thenThrow(new VehicleCannotBeRegisteredInDbException(spot.getVehiclePlate()));

        mockMvc.perform(post(uriStartMeter)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(spot)))
                //THEN
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("Should return Not Found because vehicle is not active")
    public void shouldReturnNotFoundBecauseVehicleIsNotActive() throws Exception {
        //WHEN
        when(spotRepo.findByVehiclePlate(spot.getVehiclePlate())).thenThrow(new VehicleNotExistsInDbException(spot.getVehiclePlate()));

        mockMvc.perform(patch(uriStopMeter, spot.getVehiclePlate())
                .param("finishDate", createTimeAfter2hInString(spot.getBeginDate()))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //THEN
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("Should return correct fee")
    public void shouldReturnCorrectFee() throws Exception {

        final Fee fee = new Fee(new ArchivedSpot(spot, createTimeAfter2h(spot.getBeginDate())));
        //WHEN
        when(spotRepo.findByVehiclePlate(spot.getVehiclePlate())).thenReturn(Optional.of(spot));

        mockMvc.perform(patch(uriStopMeter, spot.getVehiclePlate())
                .param("finishDate", createTimeAfter2hInString(spot.getBeginDate()))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(fee)))
                .andDo(print())
                .andReturn();
    }

    private String createTimeAfter2hInString(final Date startDate) {
        final Date newDate = Date.from(startDate.toInstant().plus(Duration.ofHours(2)));
        return new SimpleDateFormat(dateTimePattern).format(newDate);
    }

    private Date createTimeAfter2h(final Date startDate) {
        return Date.from(startDate.toInstant().plus(Duration.ofHours(2)));
    }
}
