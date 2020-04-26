package ru.rgs.csvparser.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import ru.rgs.csvparser.service.CsvParserService;
import ru.rgs.csvparser.service.CsvParserServiceImpl;


@Configuration
public class MainConfiguration {

    @Bean
    public CsvParserService csvParserService() {
            return new CsvParserServiceImpl();
    }

    @Bean
    public RestTemplate restTemplate() {
        return  new RestTemplate();
    }
}