package com.yjmedia.yvisbig.bizcom.util.excel;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SimpleExcelFile {

  private XSSFWorkbook wb;
  private Sheet sheet;
  private Row row;
  private Cell cell;
  private Class<?> clazz;
  private List<?> list;
  private XSSFCellStyle headerStyle;
  private XSSFCellStyle bodyStyle;

  private int lastRow = 0;

  public SimpleExcelFile(String sheetName, Class<?> clazz, List<?> list)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    wb = new XSSFWorkbook(); // xlsx
    sheet = wb.createSheet(sheetName);
    sheet.setDefaultColumnWidth(25);
    headerStyle = wb.createCellStyle();
    bodyStyle = wb.createCellStyle();
    this.list = list;
    this.clazz = clazz;
    setExcelColumnName();
    setExcelBody();
  }

  private void setExcelColumnName() {
    row = sheet.createRow(0);
    Field[] fields = clazz.getDeclaredFields();

    // style
    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    for (int i = 0; i < fields.length; i++) {
      ExcelColumn excelColumn = fields[i].getAnnotation(ExcelColumn.class);
      if (excelColumn != null) {
        cell = row.createCell(i);
        cell.setCellValue(excelColumn.headerName());
        cell.setCellStyle(headerStyle);
      }
    }
  }

  private void setExcelBody()
      throws IllegalAccessException, IllegalArgumentException, SecurityException {
    for (int i = 0; i < list.size(); i++) {
      row = sheet.createRow(i + 1);
      lastRow = i + 1;
      int rowNum = 0;

      Field[] fields = clazz.getDeclaredFields();

      // format
      XSSFDataFormat df = wb.createDataFormat();
      bodyStyle.setDataFormat(df.getFormat("#,##0"));

      for (int j = 0; j < fields.length; j++) {
        cell = row.createCell(rowNum++);
        ExcelColumn excelColumn = fields[j].getAnnotation(ExcelColumn.class);
        if (excelColumn != null) {
          fields[j].setAccessible(true);
          Object obj = fields[j].get(list.get(i));
          if (obj != null) {
            if (obj instanceof Integer || obj instanceof Long) {
              if (obj instanceof Integer) {
                cell.setCellValue((Integer) obj);
              } else {
                cell.setCellValue((Long) obj);
              }
              cell.setCellStyle(bodyStyle);
            } else {
              cell.setCellValue(obj.toString());
            }
          }
        }
      }
    }
  }

  public void addCustomRow(Class<?> clazz, Object obj1)
      throws IllegalAccessException, IllegalArgumentException, SecurityException {
    row = sheet.createRow(lastRow + 1);
    int rowNum = 0;
    Field[] fields = clazz.getDeclaredFields();

    for (int i = 0; i < fields.length; i++) {
      cell = row.createCell(rowNum++);

      fields[i].setAccessible(true);
      Object obj2 = fields[i].get(obj1);
      if (obj2 != null) {
        if (obj2 instanceof Integer || obj2 instanceof Long) {
          if (obj2 instanceof Integer) {
            cell.setCellValue((Integer) obj2);
          } else {
            cell.setCellValue((Long) obj2);
          }
          cell.setCellStyle(bodyStyle);
        } else {
          cell.setCellValue(obj2.toString());
        }
      }
    }
  }

  public void write(OutputStream outputStream) throws IOException {
    wb.write(outputStream);
    outputStream.close();
    // wb.close(); //xlsx는 close자동으로 해줌. xls 할거면 close따로 해주기
  }
}
