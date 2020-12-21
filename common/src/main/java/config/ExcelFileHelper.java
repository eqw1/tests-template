package config;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelFileHelper {

    public static File createFile(String filename, Workbook wb) throws IOException {
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
        return new File(filename);
    }

    public static void createHeaders(Sheet sheet, List<String> columns) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i));
        }
    }

    public static void autosizeColumns(Sheet sheet, List<String> columns) {
        // Resize all columns to fit the content size
        for (int i = 0; i < columns.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static void createRowsInSheet(Sheet sheet, int rowCount) {
        for (int i = 0; i <= rowCount; i++) {
            Row row = sheet.createRow(i);
        }
    }

//    public static <T> void addInfo(Sheet sheet, List<T> info) {
//        int rowNum = 1;
//        for (T tmp : info) {
//
//        }

//    }
}
