package com.utknl.butlers.eval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ButlersEvalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ButlersEvalApplication.class, args);
    }

}
