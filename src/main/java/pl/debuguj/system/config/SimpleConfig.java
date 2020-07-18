package pl.debuguj.system.config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;

@Log
//@Configuration
//@PropertySource("classpath:global.properties")
public class SimpleConfig {

    @Value("${db.test}")
    private String simpleString;

    @Bean
    ApplicationRunner applicationConfigRunner() {
        return args -> log.info("Msg from CONFIG class: " + simpleString);
    }
}