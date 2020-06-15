package pl.debuguj.system.owner;

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
import pl.debuguj.system.spot.ArchivedSpot;
import pl.debuguj.system.spot.ArchivedSpotRepo;
import pl.debuguj.system.spot.DriverType;
import pl.debuguj.system.spot.Spot;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = OwnerController.class)
public class OwnerControllerTest {

    @Value("${uri.owner.income}")
    private String uriCheckDailyIncome;
    @Value("${date.format}")
    private String datePattern;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ArchivedSpotRepo archivedSpotRepo;
    
    private final Spot spot = new Spot("WCC12345", DriverType.REGULAR, new Date());

    @Test
    @DisplayName("Should return NotFoundException because any vehicle was registered")
    public void shouldReturnNotFoundExceptionBecauseOfEmptyDatabase() throws Exception {
        //final DailyIncome income = new DailyIncome(spot.getBeginDate(),new BigDecimal(3.0));
        //WHEN
        mockMvc.perform(get(uriCheckDailyIncome, createDayFromBeginDateInString(spot.getBeginDate()))
                .contentType(MediaType.APPLICATION_JSON))
                //THEN
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //.andExpect(content().json(objectMapper.writeValueAsString(income)))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("Should return income for one vehicle")
    public void shouldReturnIncomeForOneVehicle() throws Exception {

        final Date finishDate = createFinishTimeStartTimePlus2Hours(spot.getBeginDate());
        final ArchivedSpot archivedSpot = new ArchivedSpot(spot, finishDate);

        final Date day = createDayFromBeginDate(spot.getBeginDate());
        final DailyIncome income = new DailyIncome(day, new BigDecimal("3.0"));
        //WHEN
        when(archivedSpotRepo.getAllByDay(any())).thenReturn(new ArrayList<>(Collections.singletonList(archivedSpot)));

        mockMvc.perform(get(uriCheckDailyIncome, createDayFromBeginDateInString(spot.getBeginDate()))
                .contentType(MediaType.APPLICATION_JSON))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(income)))
                .andDo(print())
                .andReturn();
    }

    private String createDayFromBeginDateInString(final Date date) {
        return new SimpleDateFormat(datePattern).format(date);
    }

    private Date createDayFromBeginDate(final Date date) {
        final String stringDate = new SimpleDateFormat(datePattern).format(date);
        Date formattedDate = null;
        try {
            formattedDate = new SimpleDateFormat(datePattern).parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    private Date createFinishTimeStartTimePlus2Hours(final Date date) {
        return Date.from(date.toInstant().plus(Duration.ofHours(2)));
    }
}
