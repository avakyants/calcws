package ru.sberbankinsurance.calcws.calc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;
import ru.sberbankinsurance.calcws.model.Test;


@Component
public class Calc {

    public static final String XLSX_WORKBOOK_SHEET = "СА_Расчет_единовременно";

    public static FileInputStream file;
    public static Workbook workbook;

    public static void init(String xlsxFile) throws IOException{
        file = new FileInputStream(new File(xlsxFile));
        workbook = new XSSFWorkbook(file);
    }

    public static Test readFromExcel(int d8) throws IOException {

        Test test = null;

        Sheet sheet = workbook.getSheet(XLSX_WORKBOOK_SHEET);
        CellAddress cellAddress = new CellAddress("H53");

        CellAddress cellAddressWrite = new CellAddress("D8");
        Row rowWrite = sheet.getRow(cellAddressWrite.getRow());
        Cell cellWrite = rowWrite.getCell(cellAddressWrite.getColumn());
        cellWrite.setCellValue(d8);

        Row row = sheet.getRow(cellAddress.getRow());
        Cell cell = row.getCell(cellAddress.getColumn());

        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

        if (cell.getCellType() == CellType.FORMULA) {
            switch (evaluator.evaluateFormulaCell(cell)) {
                case BOOLEAN:
                    System.out.println(cell.getBooleanCellValue());
                    break;
                case NUMERIC:
                    System.out.println(cell.getNumericCellValue());
                    test = new Test(1, String.valueOf(cell.getNumericCellValue()));
                    break;
                case STRING:
                    System.out.println(cell.getStringCellValue());
                    break;
            }
        }
        workbook.close();
        return test;
    }


}