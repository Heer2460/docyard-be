package com.infotech.dms.autentication.service;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AuthenticationServiceApplication {

    public static void main(String[] args)  {
        SpringApplication.run(AuthenticationServiceApplication.class, args);            // it wil start application
    }

//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
}
