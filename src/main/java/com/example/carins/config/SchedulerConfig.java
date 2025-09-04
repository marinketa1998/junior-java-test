package com.example.carins.config;

import com.example.carins.service.CarService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final CarService carService;
    private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);
    public SchedulerConfig(CarService carService) {
        this.carService = carService;
    }
    @Scheduled(cron = "0 30 0 * * *") //runs at 0:30 AM
    public void schedulePolicyExp() {
        carService.getExpiredPolicies()
                .forEach(i -> log.info("Policy {} for car {} expired on {}",
                        i.getId(), i.getCar().getId(), i.getEndDate())); //logs message
    }
}
