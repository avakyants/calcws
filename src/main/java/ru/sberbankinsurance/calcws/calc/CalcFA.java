package ru.sberbankinsurance.calcws.calc;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import https.calc_pfp_sberbank_insurance_ru.ws.xsd.GetFamilyActiveBatchV2;
import https.calc_pfp_sberbank_insurance_ru.ws.xsd.GetFamilyActiveBatchV2Response;
import https.calc_pfp_sberbank_insurance_ru.ws.xsd.Item;
import https.calc_pfp_sberbank_insurance_ru.ws.xsd.Return;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;


@Component
public class CalcFA {

    public static final String FA_OFFER_SHEET = "СА_Предложение";
    public static final String CLIENT_INFO_SHEET = "Информация о клиенте";
    public static final String FA_CALC_SHEET = "СА_Расчет";

    public static FileInputStream file;
    public static Workbook workbook;

    public static void init(String xlsxFile) throws IOException{
        file = new FileInputStream(new File(xlsxFile));
        workbook = new XSSFWorkbook(file);
    }


    public static Cell getCellByAddress(Sheet sheet, String cellAddress){
        CellAddress cellAddressWrite = new CellAddress(cellAddress);
        Row rowWrite = sheet.getRow(cellAddressWrite.getRow());
        Cell cellWrite = rowWrite.getCell(cellAddressWrite.getColumn());
        return cellWrite;
    }

    public static void setInputParams1(GetFamilyActiveBatchV2 request){
        Sheet sheetCurrent = workbook.getSheet(CLIENT_INFO_SHEET);

        getCellByAddress(sheetCurrent,"E4").setCellValue("Премьер");// Канал продаж
        getCellByAddress(sheetCurrent,"E5").setCellValue(request.getGender());// Пол страхователя
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
        getCellByAddress(sheetCurrent, "L36").setCellValue("Ручной ввод ГСС");
        getCellByAddress(sheetCurrent, "F36").setCellValue("Не включено");
        getCellByAddress(sheetCurrent, "L42").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L44").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L46").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L48").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L50").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L52").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L54").setCellValue("Исключить риск");
    }

    public static void setInputParams2(GetFamilyActiveBatchV2 request){
        Sheet sheetCurrent = workbook.getSheet(FA_CALC_SHEET);
        // additional inputs
        getCellByAddress(sheetCurrent, "L36").setCellValue("Максимальные ГСС");
        getCellByAddress(sheetCurrent, "F36").setCellValue("Не включено");
    }

    public static void setInputParams3(GetFamilyActiveBatchV2 request){
        Sheet sheetCurrent = workbook.getSheet(FA_CALC_SHEET);
        // additional inputs
        getCellByAddress(sheetCurrent, "F36").setCellValue("x1");
        getCellByAddress(sheetCurrent, "L36").setCellValue("Ручной ввод ГСС");
        getCellByAddress(sheetCurrent, "L42").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L44").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L46").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L48").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L50").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L52").setCellValue("Исключить риск");
        getCellByAddress(sheetCurrent, "L54").setCellValue("Исключить риск");
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
        }
        return 0.0d;
    }

    public static Item getItem(String k, Object v){
        Item i = new Item();
        https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value val = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();
        val.getContent().add(v);

        i.setKey(k);
        i.setValue(val);

        return i;

    }

    public static GetFamilyActiveBatchV2Response calcExcel(GetFamilyActiveBatchV2 request) throws IOException {

        /*SET INPUTS*/
        setInputParams1(request);

        //SET RESPONSE
        GetFamilyActiveBatchV2Response response = new GetFamilyActiveBatchV2Response();
        Return r = new Return();



        /* KEY=1*/
        Item i = new Item();
        i.setKey("1");

        https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value v = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();
        v.getContent().add(getItem("sum", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J28")))));
        v.getContent().add(getItem("payment", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58")))));

        if(request.getTime()>0){
            Item chartItem = new Item();
            chartItem.setKey("chart");
            https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value chartVal = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();

            for (int g=0;g<request.getTime();g++){
                chartVal.getContent().add(getItem(String.valueOf(g+1),String.format("%.0f", getCellByAddress(workbook.getSheet(FA_OFFER_SHEET),"F"+(g+90)).getNumericCellValue())));
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
        v2.getContent().add(getItem("sum", String.format("%.0f", getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H34").getNumericCellValue())));
        v2.getContent().add(getItem("payment", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58")))));

        if(request.getTime()>0){
            Item chartItem2 = new Item();
            chartItem2.setKey("chart");
            https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value chartVal2 = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();

            for (int g=0;g<request.getTime();g++){
                chartVal2.getContent().add(getItem(String.valueOf(g+1),String.format("%.0f", getCellByAddress(workbook.getSheet(FA_OFFER_SHEET),"F"+(g+90)).getNumericCellValue())));
            }

            chartItem2.setValue(chartVal2);
            v2.getContent().add(chartItem2);
        }

        v2.getContent().add(getItem("riskSum", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58"))-evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H28")))));
        v2.getContent().add(getItem("special_diseases_sum", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H42")))));
        v2.getContent().add(getItem("special_diseases", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J42")))));
        v2.getContent().add(getItem("care_accident_sum", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H44")))));
        v2.getContent().add(getItem("care_accident", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J44")))));
        v2.getContent().add(getItem("care_transport_sum", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H46")))));
        v2.getContent().add(getItem("care_transport", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J46")))));
        v2.getContent().add(getItem("disability_sum", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H48")))));
        v2.getContent().add(getItem("disability", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J48")))));
        v2.getContent().add(getItem("injury_accident_sum", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H50")))));
        v2.getContent().add(getItem("injury_accident", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J50")))));
        v2.getContent().add(getItem("hospitalization_sum", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H52")))));
        v2.getContent().add(getItem("hospitalization ", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J52")))));
        v2.getContent().add(getItem("surgery_accident_sum", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H54")))));
        v2.getContent().add(getItem("surgery_accident ", String.format("%.11f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J54")))));


        i2.setValue(v2);

        r.getItem().add(i2);


        /* key=3 */
        setInputParams3(request);

        Item i3 = new Item();
        i3.setKey("3");

        https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value v3 = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();
        v3.getContent().add(getItem("sum", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"H36")))));
        v3.getContent().add(getItem("payment", String.format("%.0f", evaluateAndGet(getCellByAddress(workbook.getSheet(FA_CALC_SHEET),"J58")))));

        if(request.getTime()>0){
            Item chartItem3 = new Item();
            chartItem3.setKey("chart");
            https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value chartVal3 = new https.calc_pfp_sberbank_insurance_ru.ws.xsd.Value();

            for (int g=0;g<request.getTime();g++){
                chartVal3.getContent().add(getItem(String.valueOf(g+1),String.format("%.0f", getCellByAddress(workbook.getSheet(FA_OFFER_SHEET),"F"+(g+90)).getNumericCellValue())));
            }

            chartItem3.setValue(chartVal3);
            v3.getContent().add(chartItem3);
        }

        i3.setValue(v3);

        r.getItem().add(i3);


        response.setReturn(r);

        workbook.close();

        return response;
    }


}