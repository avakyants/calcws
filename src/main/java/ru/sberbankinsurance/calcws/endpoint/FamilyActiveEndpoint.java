package ru.sberbankinsurance.calcws.endpoint;

import https.calc_pfp_sberbank_insurance_ru.ws.xsd.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.sberbankinsurance.calcws.calc.CalcFA;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Endpoint
public class FamilyActiveEndpoint {
    Logger log = LogManager.getLogger(FamilyActiveEndpoint.class);
    private static final String NAMESPACE_URI = "https://calc-pfp.sberbank-insurance.ru/ws/xsd";

    @Value("${calcws.file}")
    private String xlsxFile;

    @Value("${fareq.age}")
    private String testProp;

    @PostConstruct
    private void init(){
        try {
            long startTime = System.currentTimeMillis();
            CalcFA.init(this.xlsxFile);
            log.info("Calc.init() done in "+(System.currentTimeMillis()-startTime)+" ms");
        }catch (IOException e){
            log.error(e.getMessage());
        }

    }

    @PreDestroy
    public void destroy() {
        try {
            long startTime = System.currentTimeMillis();
            CalcFA.destroy();
            log.info("Calc.destroy() done in "+(System.currentTimeMillis()-startTime)+" ms");
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFamilyActiveBatchV2")
    @ResponsePayload
    public GetFamilyActiveBatchV2Response getFamilyActiveBatchV2(@RequestPayload GetFamilyActiveBatchV2 request){

        GetFamilyActiveBatchV2Response response = null;

        try {
            long startTime = System.currentTimeMillis();
            response = CalcFA.calc(request);
            log.info("CalcFA.calc() done in "+(System.currentTimeMillis()-startTime)+" ms");

        } catch (IOException e) {
            log.error(e.getMessage());
        }


        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getFamilyActiveBatchDetail")
    @ResponsePayload
    public GetFamilyActiveBatchDetailResponse getFamilyActiveBatchDetailResponse(@RequestPayload GetFamilyActiveBatchDetail request){
        GetFamilyActiveBatchDetailResponse response = null;
        try {
            long startTime = System.currentTimeMillis();
            response = CalcFA.calcDetail(request);
            log.info("CalcFA.calcDetail() done in "+(System.currentTimeMillis()-startTime)+" ms");

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return response;
    }



}
