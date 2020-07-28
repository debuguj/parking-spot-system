package pl.debuguj.system;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import pl.debuguj.system.spot.*;

import java.time.LocalDateTime;

@Log
@SpringBootApplication
@EnableConfigurationProperties(MyCustomProperties.class)
public class ParkingSpotsSystemApplication {

    @Autowired
    private final SpotRepo spotRepo;
    @Autowired
    private final ArchivedSpotRepo archivedSpotRepo;

    public ParkingSpotsSystemApplication(SpotRepo spotRepo, ArchivedSpotRepo archivedSpotRepo) {
        this.spotRepo = spotRepo;
        this.archivedSpotRepo = archivedSpotRepo;
    }

    public static void main(String[] args) {
        SpringApplication.run(ParkingSpotsSystemApplication.class, args);
    }

//    @Bean
//    ApplicationRunner applicationRunner() {
//        return args -> {
//            spotRepo.save(new Spot("WCI12345", DriverType.REGULAR, LocalDateTime.now()));
//            spotRepo.save(new Spot("WCI12346", DriverType.REGULAR, LocalDateTime.now()));
//            spotRepo.save(new Spot("WCI12347", DriverType.REGULAR, LocalDateTime.now()));
//            spotRepo.save(new Spot("WCI12348", DriverType.REGULAR, LocalDateTime.now()));
//            archivedSpotRepo.save(new ArchivedSpot("WCI12348", DriverType.REGULAR, LocalDateTime.now(), LocalDateTime.now().plusHours(2L)));
//            archivedSpotRepo.save(new ArchivedSpot("WCI12349", DriverType.REGULAR, LocalDateTime.now(), LocalDateTime.now().plusHours(3L)));
//            archivedSpotRepo.save(new ArchivedSpot("WCI12323", DriverType.REGULAR, LocalDateTime.now(), LocalDateTime.now().plusHours(5L)));
//            archivedSpotRepo.save(new ArchivedSpot("WCI12322", DriverType.REGULAR, LocalDateTime.now(), LocalDateTime.now().plusHours(12L)));
//        };
//    }
//    @Bean
//    ApplicationRunner applicationRunner(Environment environment,
//                                        @Value("${greetings-msg:Default hello}") String defaultValue,
//                                        @Value("${HOME}") String myHome,
//                                        @Value("${mycustom-message}") String myCustomMsg,
//                                        MyCustomProperties myCustomProperties
//    ) {
//        return args -> {
//            log.info("Msg from app: " + environment.getProperty("test.prop"));
//            log.info("Msg 2 from app: " + defaultValue);
//            log.info("Msg 2 from app: " + myHome);
//            log.info("Msg custom: " + myCustomMsg);
//            log.info("Msg custom properties: " + myCustomProperties.getMessage());
//        };
//    }

    @Autowired
    void contributeToPropertySources(ConfigurableEnvironment environment) {
        environment.getPropertySources().addLast(new MyCustomPropertySource());
    }

    static class MyCustomPropertySource extends PropertySource<String> {
        public MyCustomPropertySource() {
            super("mycustom");
        }

        @Override
        public Object getProperty(String name) {
            if (name.equalsIgnoreCase("mycustom-message")) {
                return "Hello from " + MyCustomPropertySource.class.getSimpleName();
            }
            return null;
        }
    }
}

@Data
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("gb")
class MyCustomProperties {
    private final String message;
}
