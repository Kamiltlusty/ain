package pl.kamil.infrastructure.io;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelExport  {
    public void save(Map<String, Integer> data, String fileName) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(fileName + ".xlsx")) {

            XSSFSheet spreadsheet = workbook.createSheet(fileName);
            XSSFRow row;

            int rowId = 0;
            int binId = 2;
            int countId = 3;
            row = spreadsheet.createRow(rowId);
            row.createCell(binId).setCellValue("bin");
            row.createCell(countId).setCellValue("count");

            for (var entry : data.entrySet()) {
                var key = entry.getKey();
                var value = entry.getValue();

                row = spreadsheet.createRow(++rowId);
                // bin
                Cell bin = row.createCell(binId);
                bin.setCellValue(key);
                // value
                Cell count = row.createCell(countId);
                count.setCellValue(value);
            }

            workbook.write(out);
            System.out.println(fileName + " written successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public void save(List<Double> data1, String fileName) {
//
//    }
//
//    @Override
//    public void save(List<Double> data, List<Double> data1, List<Double> data2, String fileName) {
//
//    }
}
