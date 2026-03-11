package aleosh.online.vivia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ViviaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViviaApplication.class, args);
    }

}
