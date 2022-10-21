package com.infotech.docyard.cjs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class CronJobServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CronJobServiceApplication.class, args);
        System.out.println("JobServicesApplication server running.......");
    }

}
