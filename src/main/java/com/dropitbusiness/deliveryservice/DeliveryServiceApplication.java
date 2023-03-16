package com.dropitbusiness.deliveryservice;

import com.dropitbusiness.deliveryservice.data.AppData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class DeliveryServiceApplication implements CommandLineRunner {

    @Autowired
    private AppData appData;

    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            appData.loadData();
            Thread.currentThread().join();
        } catch (InterruptedException exc) {
            log.error(String.format("EXCEPTION: %s : %s", exc.getMessage(), Arrays.toString(exc.getStackTrace())));
        }
    }
}