package com.infotech.docyard.um;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UMServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(UMServicesApplication.class, args);
        System.out.println("UMServicesApplication server running.......");
    }
}
