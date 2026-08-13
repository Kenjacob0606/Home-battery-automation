package uk.ac.york.eng2.reactive.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import uk.ac.york.cs.eng2.reactive.rates.model.AgileRatesResponse;
import uk.ac.york.cs.eng2.reactive.rates.model.AgileSlotRates;
import uk.ac.york.eng2.reactive.domain.Component;
import uk.ac.york.eng2.reactive.repository.ComponentRepository;
import uk.ac.york.cs.eng2.reactive.rates.api.RatesApi;
import io.micronaut.serde.ObjectMapper;

import java.time.Instant;
import java.util.List;


@Singleton
public class AgileRatesFetcher {
    public static final String COMPONENT_NAME = "AgileRatesFetcher";

    @Inject
    ComponentRepository componentRepository;

    @Inject
    AgileRatesProducer agileRatesProducer;

    @Inject
    RatesApi ratesApi;

    @Inject
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        if (componentRepository.findByName(COMPONENT_NAME).isEmpty()) {
            Component c = new Component();
            c.setName(COMPONENT_NAME);
            componentRepository.save(c);
        }
    }

    @Scheduled(initialDelay = "0s",fixedDelay = "12h")
    public void fetchAndPublish(){
        System.out.println("FETCHER RUNNING");
        try{
            AgileRatesResponse response = ratesApi.getRates();
            System.out.println("RESPONSE RECEIVED: " + response);
            List<AgileSlotRates> rates = response.getRates();
            System.out.println("RATES SIZE: " + (rates == null ? "null" : rates.size()));
            if(rates==null || rates.isEmpty()){
                System.out.println("NO RATES - EXITING");
                return;
            }
            Instant validFrom = rates.get(0).getDeliveryStart().toInstant();
            Instant validTo = rates.get(rates.size()-1).getDeliveryEnd().toInstant();

            String json = objectMapper.writeValueAsString(response);
            agileRatesProducer.publishAgileRates("rates", new AgileRatesEvent(json, validFrom, validTo));
            System.out.println("PUBLISHING TO KAFKA");
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
