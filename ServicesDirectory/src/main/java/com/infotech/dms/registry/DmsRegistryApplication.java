package com.infotech.dms.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
//@EnableEurekaServer
public class DmsRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(DmsRegistryApplication.class, args);
	}

}
