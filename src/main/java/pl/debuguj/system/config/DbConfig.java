package pl.debuguj.system.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Data
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties("spring.datasource")
@Log
public class DbConfig {

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Profile("dev")
    @Bean
    public String devDatabaseConnection(){
        log.info(driverClassName);
        log.info(url);
        log.info(username);
        log.info(password);
        return "DB connection profile: DEV";
    }

    @Profile("test")
    @Bean
    public String testDatabaseConnection(){
        log.info(driverClassName);
        log.info(url);
        log.info(username);
        log.info(password);
        return "DB connection profile: TEST";
    }

    @Profile("prod")
    @Bean
    public String prodDatabaseConnection(){
        log.info(driverClassName);
        log.info(url);
        log.info(username);
        log.info(password);
        return "DB connection profile: PROD";
    }

}