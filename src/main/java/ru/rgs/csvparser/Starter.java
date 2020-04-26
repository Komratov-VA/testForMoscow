package ru.rgs.csvparser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import ru.rgs.csvparser.configuration.MainConfiguration;

import java.util.ArrayList;
import java.util.concurrent.Future;

@EnableFeignClients
@SpringBootApplication
@Import(MainConfiguration.class)
public class Starter {
    public static void main(String[] args) {
        SpringApplication.run(Starter.class, args);
    }
}
