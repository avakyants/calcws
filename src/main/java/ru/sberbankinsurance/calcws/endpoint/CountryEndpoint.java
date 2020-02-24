package ru.sberbankinsurance.calcws.endpoint;

import io.spring.guides.gs_producing_web_service.GetCountryRequest;
import io.spring.guides.gs_producing_web_service.GetCountryResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.sberbankinsurance.calcws.calc.CountryRepository;
import ru.sberbankinsurance.calcws.calc.Calc;
import ru.sberbankinsurance.calcws.model.Test;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Random;

@Endpoint
public class CountryEndpoint {
    Logger log = LogManager.getLogger(CountryEndpoint.class);
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";

    @Autowired
    private CountryRepository countryRepository;

    @Value("${calcws.file}")
    private String xlsxFile;

    @PostConstruct
    private void init(){
        try {
            Calc.init(this.xlsxFile);
        }catch (IOException e){
            log.error(e.getMessage());
        }

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
    @ResponsePayload
    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request){
        int rnd = new Random().nextInt((69 - 18) + 1) + 18;
        System.out.println(rnd);
        try {
            Test test = Calc.readFromExcel(rnd);
            log.info(rnd+" with content "+test.getContent());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        GetCountryResponse response = new GetCountryResponse();
        response.setCountry(countryRepository.findCountry(request.getName()));
        return response;
    }

}
