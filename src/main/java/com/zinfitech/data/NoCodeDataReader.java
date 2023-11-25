package com.zinfitech.data;

import com.zinfitech.pojo.TestCase;
import com.zinfitech.pojo.TestStep;
import java.util.List;
import java.util.Map;

public interface NoCodeDataReader {

  public List<TestCase> getNoCodeTest();

  public List<TestStep> getNoCodeStep();

  public Map<String, List<Map<String, String>>> getTestData();

  public Map<String, String> getDefaultTestData();

  public List<Map<String, String>> getLocators();

  public String getSheetName(String sheetName, String type);
}
