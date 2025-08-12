package com.example.airassist.batch;

import com.example.airassist.persistence.dao.AirlineRepository;
import com.example.airassist.persistence.model.Airline;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class AirlineDataInitializer {
    private final AirlineRepository airlineRepository;


  //  @PostConstruct
    @Scheduled(fixedRate = 6000000)
    public void loadData(){
        try(Reader reader = new FileReader("../database/airlines_clean.csv")){
            List<Airline> airlines = new CsvToBeanBuilder<Airline>(reader)
                    .withType(Airline.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();
            airlineRepository.saveAll(airlines);
            log.info("Airlines data loaded successfully");
        }catch (Exception e){
            log.error("Error loading airlines data: {}", e.getMessage());
            throw new RuntimeException("Failed to load airlines data", e);
        }
    }
}