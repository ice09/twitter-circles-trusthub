package tech.blockchainers.circles.twitter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Slf4j
@EnableScheduling
public class TwitterCirclesTrustHub {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "trusthub");
        SpringApplication.run(TwitterCirclesTrustHub.class, args);
    }

}
