package org.example.baboobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BabooBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BabooBackendApplication.class, args);
    }

}
