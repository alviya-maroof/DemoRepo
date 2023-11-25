package com.zinfitech.data;

import com.google.gson.Gson;
import com.qapitol.sauron.common.utils.FileAssistant;
import com.zinfitech.headers.Headers.DeFaultTestData;
import com.zinfitech.headers.Headers.Locators;
import com.zinfitech.headers.StepHeader;
import com.zinfitech.headers.TestCaseHeader;
import com.zinfitech.pojo.TestCase;
import com.zinfitech.pojo.TestStep;
import com.zinfitech.pojo.ZiniFunctionalInterface;
import com.zinfitech.zinfiexception.ZinfiTechException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel implements ZiniFunctionalInterface {

  Gson gson = new Gson();

  public List<List<Object>> readExcelData(String folder, String fileName, String sheetName,
      int size) {
    InputStream in = FileAssistant.loadFile(folder + File.separator + fileName);
    return readExcelData(in, sheetName, size);
  }

  public List<List<Object>> readExcelData(InputStream inputStream, String sheetName, int size) {
    List<List<Object>> lists = new ArrayList<>();
    try {
      Workbook workbook = new XSSFWorkbook(inputStream);
      Sheet sheet = workbook.getSheet(sheetName);
      for (int row = 0; row <= sheet.getLastRowNum(); row++) {
        List<Object> cellList = new ArrayList<>();
        for (int cell = 0; cell < size; cell++) {
          cellList.add(getCelValue(sheet.getRow(row).getCell(cell)));
        }
        lists.add(cellList);
      }
      workbook.close();
      inputStream.close();
    } catch (IOException e) {
      throw new ZinfiTechException("unable open excel file", e);
    }
    return lists;
  }

  private Object getCelValue(Cell cell) {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
      case BOOLEAN:
        return cell.getBooleanCellValue();
      case FORMULA:
        return cell.getCellFormula();
      case BLANK:
        return "";
      default:
        return null;
    }
  }

  public List<TestCase> getNoCodeTest(List<List<Object>> testData) {
    List<TestCase> testCaseList = new ArrayList<>();
    List<Object> headers = testData.remove(0);
    for (List<Object> rowList : testData) {
      Map<String, Object> rowMap = new HashMap<>();
      for (TestCaseHeader testCaseHeader : TestCaseHeader.values()) {
        if (testCaseHeader.equals(TestCaseHeader.MODULE)) {
          rowMap.put(getHeader(headers, testCaseHeader),
              splitString(splitByComa, getRowValue(rowList, headers, testCaseHeader)));
        } else if (testCaseHeader.equals(TestCaseHeader.PRIORITY)) {
          rowMap.put(getHeader(headers, testCaseHeader),
              TestCaseHeader.getPriority(getRowValue(rowList, headers, testCaseHeader)));
        } else {
          rowMap.put(getHeader(headers, testCaseHeader),
              getRowValue(rowList, headers, testCaseHeader));
        }
      }
      TestCase testCase = gson.fromJson(gson.toJsonTree(rowMap), TestCase.class);
      testCaseList.add(testCase);
    }
    return testCaseList;
  }

  public List<TestStep> getNoCodeStep(List<List<Object>> stepData) {
    List<TestStep> steps = new ArrayList<>();
    List<Object> headers = stepData.remove(0);
    for (List<Object> listOfRow : stepData) {
      Map<String, Object> row = new HashMap<>();
      for (StepHeader stepHeader : StepHeader.values()) {
        if (stepHeader.equals(StepHeader.LOCATORS)) {
          row.put(getHeader(headers, StepHeader.LOCATORS),
              splitString(splitByComa, getRowValue(listOfRow, headers, StepHeader.LOCATORS)));
        } else {
          row.put(getHeader(headers, stepHeader),
              getRowValue(listOfRow, headers, stepHeader));
        }
      }
      steps.add(gson.fromJson(gson.toJsonTree(row), TestStep.class));
    }
    return steps;
  }

  public Map<String, List<Map<String, String>>> getTestData(List<List<Object>> testData) {
    Map<String, List<Map<String, String>>> mapMap = new HashMap<>();
    testData.remove(0);
    String key = null;
    for (int i = 0; i < testData.size(); i++) {
      Map<String, String> map = new HashMap<>();
      if (StringUtils.isNotEmpty(testData.get(i).get(0).toString())) {
        key = testData.get(i).get(0).toString();
      } else {
        throw new ZinfiTechException("0th index should not be empty");
      }
      for (int j = i;
          j < testData.size() && StringUtils.isEmpty(testData.get(i + 1).get(0).toString()); j++) {
        map.put(testData.get(j).get(1).toString(), testData.get(j).get(2).toString());
        i = j;
      }
      if (mapMap.containsKey(key)) {
        List<Map<String, String>> v = new ArrayList<>(mapMap.get(key));
        v.add(map);
        mapMap.put(key, v);
      } else {
        mapMap.put(key, List.of(map));
      }
    }
    return mapMap;
  }

  public Map<String, String> getDefaultTestData(List<List<Object>> data) {
    Map<String, String> testCaseData = new HashMap<>();
    List<Object> headers = data.remove(0);
    for (List<Object> row : data) {
      testCaseData.put(row.get(indexOf.apply(headers, DeFaultTestData.NAME)).toString(),
          row.get(indexOf.apply(headers, DeFaultTestData.VALUE)).toString());
    }
    return testCaseData;
  }

  public List<Map<String, String>> getLocators(List<List<Object>> data) {
    List<Map<String, String>> testCaseData = new ArrayList<>();
    List<Object> headers = data.remove(0);
    for (List<Object> row : data) {
      testCaseData.add(Map.of(row.get(indexOf.apply(headers, Locators.NAME)).toString(),
          row.get(indexOf.apply(headers, Locators.VALUE)).toString()));
    }
    return testCaseData;
  }
}
