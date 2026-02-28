//package com.svrmslk.customer;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//
//
//@SpringBootApplication
//@EnableFeignClients
//public class CustomerServiceApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(CustomerServiceApplication.class, args);
//    }
//}

package com.svrmslk.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan("com.svrmslk.customer.shared.security")
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
