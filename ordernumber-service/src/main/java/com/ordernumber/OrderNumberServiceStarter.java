package com.ordernumber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(value={"com.shys.cloud"})
public class OrderNumberServiceStarter extends SpringBootServletInitializer{
	
	//eclipse内嵌tomcat启动
	public static void main(String[] args) {
		SpringApplication.run(OrderNumberServiceStarter.class, args);
	}
	
	//war包启动
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(OrderNumberServiceStarter.class);
    }
}
