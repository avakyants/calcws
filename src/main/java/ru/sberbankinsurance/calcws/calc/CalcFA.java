package ru.sberbankinsurance.calcws.calc;

import java.io.*;

import https.calc_pfp_sberbank_insurance_ru.ws.xsd.*;
import https.calc_pfp_sberbank_insurance_ru.ws.xsd.Chart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


@Component
public class CalcFA {

    public static final String FA_OFFER_SHEET = "СА_Предложение";
    public static final String CLIENT_INFO_SHEET = "Информация о клиенте";
    public static final String FA_CALC_SHEET = "СА_Расчет";

    //some values
    public static final String EXCL_RISK = "Исключить риск";
    public static final String MAXS_GSS = "Максимальные ГСС";
    public static final String MAX_GSS = "Максимальная ГСС";
    public static final String MANUAL_GSS = "Ручной ввод ГСС";

    public static FileInputStream file;
    public static Workbook workbook;

    public static void init(String xlsxFile) throws IOException{
        file = new FileInputStream(new File(xlsxFile));
        workbook = new XSSFWorkbook(file);
    }

    public static void destroy() throws IOException {
        workbook.close();
        file.close();
    }


    public static Cell getCellByAddress(Sheet sheet, String cellAddress){
        CellAddress cellAddressWrite = new CellAddress(cellAddress);
        Row rowWrite = sheet.getRow(cellAddressWrite.getRow());
        return rowWrite.getCell(cellAddressWrite.getColumn());
    }

    public static void setInputParams1(GetFamilyActiveBatchV2 request){
        Sheet sheetCurrent = workbook.getSheet(CLIENT_INFO_SHEET);

        getCellByAddress(sheetCurrent,"E4").setCellValue("Премьер");// Канал продаж
        getCellByAddress(sheetCurrent,"E5").setCellValue(request.getGender());// Пол страхователя
        getCellByAddress(sheetCurrent,"E6").setCellValue(request.getAge());// Пол страхователя
        getCellByAddress(sheetCurrent,"E7").setCellValue("Рубли");//валюта

        sheetCurrent = workbook.getSheet(FA_CALC_SHEET);

        getCellByAddress(sheetCurrent, "D8").setCellValue(request.getGender());// Пол страхователя
        getCellByAddress(sheetCurrent, "D10").setCellValue(request.getAge());// Возраст Страхователя
        getCellByAddress(sheetCurrent, "D16").setCellValue(request.getTime());// Срок страхования
        getCellByAddress(sheetCurrent, "D18").setCellValue("Ежегодно");// Периодичность уплаты взносов
        getCellByAddress(sheetCurrent, "D20").setCellValue("Рубли");// валюта
        getCellByAddress(sheetCurrent, "H28").setCellValue(request.getPayment());// Страховой взнос
        getCellByAddress(sheetCurrent, "L24").setCellValue("6.5%");// Ожидаемая доходность

        getCellByAddress(sheetCurrent, "H34").setCellValue(evaluateAndGet(getCellByAddress(sheetCurrent,"J28")));// ГСС

        // additional inputs
        getCellByAddress(sheetCurrent, "L36").setCellValue(MANUAL_GSS);
        getCellByAddress(sheetCurrent, "F36").setCellValue("Не включено");
        getCellByAddress(sheetCurrent, "L42").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L44").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L46").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L48").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L50").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L52").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L54").setCellValue(EXCL_RISK);
    }

    public static void setInputParams2(GetFamilyActiveBatchV2 request){
        Sheet sheetCurrent = workbook.getSheet(FA_CALC_SHEET);
        // additional inputs
        getCellByAddress(sheetCurrent, "L36").setCellValue(MAXS_GSS);
        getCellByAddress(sheetCurrent, "F36").setCellValue("Не включено");
    }

    public static void setInputParams3(GetFamilyActiveBatchV2 request){
        Sheet sheetCurrent = workbook.getSheet(FA_CALC_SHEET);
        // additional inputs
        getCellByAddress(sheetCurrent, "F36").setCellValue("x1");
        getCellByAddress(sheetCurrent, "L36").setCellValue(MANUAL_GSS);
        getCellByAddress(sheetCurrent, "L42").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L44").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L46").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L48").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L50").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L52").setCellValue(EXCL_RISK);
        getCellByAddress(sheetCurrent, "L54").setCellValue(EXCL_RISK);
    }

    public static void setInputParamsDetail(GetFamilyActiveBatchDetail request){
        Sheet sheetCurrent = workbook.getSheet(CLIENT_INFO_SHEET);

        getCellByAddress(sheetCurrent,"E4").setCellValue("Премьер");// Канал продаж
        getCellByAddress(sheetCurrent,"E5").setCellValue(request.getOptions().getGender());// Пол страхователя
        getCellByAddress(sheetCurrent,"E6").setCellValue(request.getOptions().getAge());// Возраст страхователя
        getCellByAddress(sheetCurrent,"E7").setCellValue(request.getOptions().getCurrency());//валюта

        sheetCurrent = workbook.getSheet(FA_CALC_SHEET);

        getCellByAddress(sheetCurrent, "D8").setCellValue(request.getOptions().getGender());// Пол страхователя
        getCellByAddress(sheetCurrent, "D10").setCellValue(request.getOptions().getAge());// Возраст Страхователя
        getCellByAddress(sheetCurrent, "D16").setCellValue(request.getOptions().getTime());// Срок страхования
        getCellByAddress(sheetCurrent, "D18").setCellValue(request.getOptions().getFrequency());// Периодичность уплаты взносов
        getCellByAddress(sheetCurrent, "D20").setCellValue(request.getOptions().getCurrency());// валюта
        getCellByAddress(sheetCurrent, "L24").setCellValue("6.5%");// Ожидаемая доходность
        getCellByAddress(sheetCurrent, "H28").setCellValue(((request.getOptions().getPaymentType().equals("взнос"))?request.getOptions().getPayment():50000.0f));// Страховой взнос
        getCellByAddress(sheetCurrent, "H34").setCellValue(((request.getOptions().getPaymentType().equals("сумма"))?request.getOptions().getPayment():1.0f));// ГСС
        getCellByAddress(sheetCurrent, "F36").setCellValue(((request.getOptions().getRaider().equalsIgnoreCase("Нет"))?"Не включено":request.getOptions().getRaider()));

        if(request.getOptions().getPaymentType().equalsIgnoreCase("взнос")){
            getCellByAddress(sheetCurrent, "H34").setCellValue(evaluateAndGet(getCellByAddress(sheetCurrent,"J28")));
        }else if(request.getOptions().getPaymentType().equalsIgnoreCase("сумма")){
            getCellByAddress(sheetCurrent, "H28").setCellValue(evaluateAndGet(getCellByAddress(sheetCurrent,"J34")));
        }

        getCellByAddress(sheetCurrent, "L36").setCellValue(MANUAL_GSS);


        if(request.getRisks().getSpecialDiseases()==1)
            getCellByAddress(sheetCurrent, "L42").setCellValue(MAX_GSS);
        else
            getCellByAddress(sheetCurrent, "L42").setCellValue(EXCL_RISK);

        evaluateAndGet(getCellByAddress(sheetCurrent,"L42"));

        if(request.getRisks().getCareAccident()==1)
            getCellByAddress(sheetCurrent, "L44").setCellValue(MAX_GSS);
        else
            getCellByAddress(sheetCurrent, "L44").setCellValue(EXCL_RISK);

        if(request.getRisks().getCareTransport()==1)
            getCellByAddress(sheetCurrent, "L46").setCellValue(MAX_GSS);
        else
            getCellByAddress(sheetCurrent, "L46").setCellValue(EXCL_RISK);

        if(request.getRisks().getDisability()==1)
            getCellByAddress(sheetCurrent, "L48").setCellValue(MAX_GSS);
        else
            getCellByAddress(sheetCurrent, "L48").setCellValue(EXCL_RISK);

        if(request.getRisks().getInjuryAccident()==1)
            getCellByAddress(sheetCurrent, "L50").setCellValue(MAX_GSS);
        else
            getCellByAddress(sheetCurrent, "L50").setCellValue(EXCL_RISK);

        if(request.getRisks().getHospitalization()==1)
            getCellByAddress(sheetCurrent, "L52").setCellValue(MAX_GSS);
        else
            getCellByAddress(sheetCurrent, "L52").setCellValue(EXCL_RISK);

        if(request.getRisks().getSurgeryAccident()==1)
            getCellByAddress(sheetCurrent, "L54").setCellValue(MAX_GSS);
        else
            getCellByAddress(sheetCurrent, "L54").setCellValue(EXCL_RISK);
    }

    public static double evaluateAndGet(Cell cell){

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        if (cell.getCellType() == CellType.FORMULA) {
            switch (evaluator.evaluateFormulaCell(cell)) {
                case BOOLEAN:
                    break;
                case STRING:
                    break;
                case NUMERIC:
                    return cell.getNumericCellValue();
            }
        }else if(cell.getCellType() == CellType.NUMERIC){
            return cell.getNumericCellValue();
        }
        return -1.0d;
    }

    public static Item getItem(String k, Object v){
        Item i = new Item();
        https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value val = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();
        val.getContent().add(v);
        i.setKey(k);
        i.setValue(val);
        return i;
    }

    public static Item getRiskItem(String name, double sum, double payment){
        Item i = new Item();
        i.setName(name);
        i.setSum(Double.valueOf(sum).floatValue());
        i.setPayment(Double.valueOf(payment).floatValue());
        return i;
    }

    private static void checkBatchRequest(GetFamilyActiveBatchV2 request) throws IllegalArgumentException{
        if(request.getAge()==0) throw new IllegalArgumentException("Invalid age");
        if(!request.getGender().equalsIgnoreCase("м") && !request.getGender().equalsIgnoreCase("ж")) throw new IllegalArgumentException("Invalid gender");
        if(request.getTime()==0) throw new IllegalArgumentException("Invalid time");
        if(request.getPayment()==0) throw new IllegalArgumentException("Invalid payment");
    }

    public static GetFamilyActiveBatchV2Response calc(GetFamilyActiveBatchV2 request){

        checkBatchRequest(request);

        /*SET INPUTS*/
        setInputParams1(request);

        //SET RESPONSE
        GetFamilyActiveBatchV2Response response = new GetFamilyActiveBatchV2Response();
        Return r = new Return();

        /* KEY=1*/
        Item i = new Item();
        i.setKey("1");

        https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value v = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();
        v.getContent().add(getItem("sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J28")))));
        v.getContent().add(getItem("payment", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58")))));

        if(request.getTime()>0){
            Item chartItem = new Item();
            chartItem.setKey("chart");
            https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value chartVal = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();

            for (int g=0;g<request.getTime();g++){
                chartVal.getContent().add(getItem(String.valueOf(g+1),String.format("%.2f", getCellByAddress(workbook.getSheet(FA_OFFER_SHEET),"F"+(g+90)).getNumericCellValue())));
            }

            chartItem.setValue(chartVal);
            v.getContent().add(chartItem);
        }


        i.setValue(v);

        r.getItem().add(i);

        /* KEY=2 */
        setInputParams2(request);

        Item i2 = new Item();
        i2.setKey("2");

        https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value v2 = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();
        v2.getContent().add(getItem("sum", String.format("%.2f", getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H34").getNumericCellValue())));
        v2.getContent().add(getItem("payment", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58")))));

        if(request.getTime()>0){
            Item chartItem2 = new Item();
            chartItem2.setKey("chart");
            https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value chartVal2 = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();

            for (int g=0;g<request.getTime();g++){
                chartVal2.getContent().add(getItem(String.valueOf(g+1),String.format("%.2f", getCellByAddress(workbook.getSheet(FA_OFFER_SHEET),"F"+(g+90)).getNumericCellValue())));
            }

            chartItem2.setValue(chartVal2);
            v2.getContent().add(chartItem2);
        }

        v2.getContent().add(getItem("riskSum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58"))-evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H28")))));
        v2.getContent().add(getItem("special_diseases_sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H42")))));
        v2.getContent().add(getItem("special_diseases", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J42")))));
        v2.getContent().add(getItem("care_accident_sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H44")))));
        v2.getContent().add(getItem("care_accident", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J44")))));
        v2.getContent().add(getItem("care_transport_sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H46")))));
        v2.getContent().add(getItem("care_transport", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J46")))));
        v2.getContent().add(getItem("disability_sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H48")))));
        v2.getContent().add(getItem("disability", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J48")))));
        v2.getContent().add(getItem("injury_accident_sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H50")))));
        v2.getContent().add(getItem("injury_accident", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J50")))));
        v2.getContent().add(getItem("hospitalization_sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H52")))));
        v2.getContent().add(getItem("hospitalization ", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J52")))));
        v2.getContent().add(getItem("surgery_accident_sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H54")))));
        v2.getContent().add(getItem("surgery_accident ", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J54")))));


        i2.setValue(v2);

        r.getItem().add(i2);


        /* key=3 */
        setInputParams3(request);

        Item i3 = new Item();
        i3.setKey("3");

        https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value v3 = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();
        v3.getContent().add(getItem("sum", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H36")))));
        v3.getContent().add(getItem("payment", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58")))));

        if(request.getTime()>0){
            Item chartItem3 = new Item();
            chartItem3.setKey("chart");
            https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value chartVal3 = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();

            for (int g=0;g<request.getTime();g++){
                chartVal3.getContent().add(getItem(String.valueOf(g+1),String.format("%.2f", getCellByAddress(workbook.getSheet(FA_OFFER_SHEET),"F"+(g+90)).getNumericCellValue())));
            }

            chartItem3.setValue(chartVal3);
            v3.getContent().add(chartItem3);
        }

        v3.getContent().add(getItem("raider", String.format("%.2f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J36")))));

        i3.setValue(v3);

        r.getItem().add(i3);


        response.setReturn(r);
        return response;
    }


    private static void checkDetailRequest(GetFamilyActiveBatchDetail request) throws IllegalArgumentException{
        if(request.getOptions().getAge()==0) throw new IllegalArgumentException("Invalid age");
        if(request.getOptions().getTime()==0) throw new IllegalArgumentException("Invalid time");
        if(!request.getOptions().getGender().equalsIgnoreCase("м") && !request.getOptions().getGender().equalsIgnoreCase("ж")) throw new IllegalArgumentException("Invalid gender");
        if(!request.getOptions().getPaymentType().equalsIgnoreCase("взнос") &&
                !request.getOptions().getPaymentType().equalsIgnoreCase("сумма")) throw new IllegalArgumentException("Invalid payment_type");
        if(request.getOptions().getPayment()==0) throw new IllegalArgumentException("Invalid payment");
        if(!request.getOptions().getFrequency().equalsIgnoreCase("Единовременно") &&
                !request.getOptions().getFrequency().equalsIgnoreCase("Ежегодно") &&
                !request.getOptions().getFrequency().equalsIgnoreCase("Раз в полгода") &&
                !request.getOptions().getFrequency().equalsIgnoreCase("Ежеквартально")
        ) throw new IllegalArgumentException("Invalid frequency");

        if(!request.getOptions().getCurrency().equalsIgnoreCase("Рубли")) throw new IllegalArgumentException("Invalid currency, only RUB");

        if(!request.getOptions().getRaider().equalsIgnoreCase("Нет") &&
                !request.getOptions().getFrequency().equalsIgnoreCase("x1") &&
                !request.getOptions().getFrequency().equalsIgnoreCase("x2") &&
                !request.getOptions().getFrequency().equalsIgnoreCase("x3")
        ) throw new IllegalArgumentException("Invalid raider");

        if(request.getRisks().getSpecialDiseases()!=0 && request.getRisks().getSpecialDiseases()!=1) throw new IllegalArgumentException("Invalid special_diseases");
        if(request.getRisks().getCareAccident()!=0 && request.getRisks().getCareAccident()!=1) throw new IllegalArgumentException("Invalid care_accident");
        if(request.getRisks().getCareTransport()!=0 && request.getRisks().getCareTransport()!=1) throw new IllegalArgumentException("Invalid care_transport");
        if(request.getRisks().getDisability()!=0 && request.getRisks().getDisability()!=1) throw new IllegalArgumentException("Invalid disability");
        if(request.getRisks().getInjuryAccident()!=0 && request.getRisks().getInjuryAccident()!=1) throw new IllegalArgumentException("Invalid injury_accident");
        if(request.getRisks().getSurgeryAccident()!=0 && request.getRisks().getSurgeryAccident()!=1) throw new IllegalArgumentException("Invalid surgery_accident");
        if(request.getRisks().getHospitalization()!=0 && request.getRisks().getHospitalization()!=1) throw new IllegalArgumentException("Invalid hospitalization");

    }

    public static GetFamilyActiveBatchDetailResponse calcDetail(GetFamilyActiveBatchDetail request){

        checkDetailRequest(request);

        /*SET INPUTS*/
        setInputParamsDetail(request);

        GetFamilyActiveBatchDetailResponse response = new GetFamilyActiveBatchDetailResponse();
        Return r = new Return();

        r.setSum(Double.valueOf(evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J28"))).floatValue());
        r.setPayment(Double.valueOf(evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58"))).floatValue());
        r.setRaider(String.format("%.2f", Double.valueOf(evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J36"))).floatValue()));
        r.setRelease(Double.valueOf(evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J56"))).intValue());


        //Risks
        Risks risks = new Risks();

        risks.getItem().add(getRiskItem("special_diseases",
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H42")),
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J42"))));

        risks.getItem().add(getRiskItem("care_accident",
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H44")),
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J44"))));

        risks.getItem().add(getRiskItem("care_transport",
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H46")),
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J46"))));

        risks.getItem().add(getRiskItem("disability",
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H48")),
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J48"))));

        risks.getItem().add(getRiskItem("injury_accident",
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H50")),
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J50"))));

        risks.getItem().add(getRiskItem("hospitalization",
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H52")),
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J52"))));

        risks.getItem().add(getRiskItem("surgery_accident",
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H54")),
                evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J54"))));

        r.setRisks(risks);

        //chart

        if(request.getOptions().getTime()>0){
            Chart chart = new Chart();
            for (int g=0;g<request.getOptions().getTime();g++){
                chart.getItem().add((getItem(String.valueOf(g+1),String.format("%.2f", getCellByAddress(workbook.getSheet(FA_OFFER_SHEET),"F"+(g+90)).getNumericCellValue()))));
            }

            r.setChart(chart);
        }

        response.setReturn(r);
        return response;
    }

    public static void saveWorkBookToFile(String filename) throws IOException{
        try(FileOutputStream out = new FileOutputStream(filename)){
            workbook.write(out);
        }
    }


}