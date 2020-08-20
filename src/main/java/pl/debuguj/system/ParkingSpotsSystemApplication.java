package pl.debuguj.system;

import lombok.extern.java.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log
@SpringBootApplication
public class ParkingSpotsSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingSpotsSystemApplication.class, args);
    }
}
