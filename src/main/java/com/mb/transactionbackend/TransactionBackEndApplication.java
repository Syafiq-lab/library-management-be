package com.mb.transactionbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TransactionBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionBackEndApplication.class, args);
    }
}