package com.infotech.docyard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class UMServicesApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(UMServicesApplication.class, args);
        System.out.println("UMServicesApplication server running.......");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UMServicesApplication.class);
    }

}
