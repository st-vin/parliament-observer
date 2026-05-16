package ke.co.bungesummary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BungeSummaryApplication {

    public static void main(String[] args) {
        SpringApplication.run(BungeSummaryApplication.class, args);
    }
}
