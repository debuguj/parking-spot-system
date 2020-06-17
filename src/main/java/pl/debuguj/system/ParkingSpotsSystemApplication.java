package pl.debuguj.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

@Log
@SpringBootApplication
@EnableConfigurationProperties(MyCustomProperties.class)
public class ParkingSpotsSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingSpotsSystemApplication.class, args);
    }


    @Bean
    ApplicationRunner applicationRunner(Environment environment,
                                        @Value("${greetings-msg:Default hello}") String defaultValue,
                                        @Value("${HOME}") String myHome,
                                        @Value("${mycustom-message}") String myCustomMsg,
                                        MyCustomProperties myCustomProperties
    ) {
        return args -> {
            log.info("Msg from app: " + environment.getProperty("test.prop"));
            log.info("Msg 2 from app: " + defaultValue);
            log.info("Msg 2 from app: " + myHome);
            log.info("Msg custom: " + myCustomMsg);
            log.info("Msg custom properties: " + myCustomProperties.getMessage());
        };
    }

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
