package com.zinfitech.model;

import com.qapitol.sauron.data.FieldTypeUtil;
import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.zinfiexception.ZinfiTechException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDataModel {

  private final NoCodeDataReader dataReader;
  private final Map<String, List<Map<String, String>>> testDataMap = new HashMap<>();

  private final Map<String, String> defaultTestData = new HashMap<>();

  public TestDataModel(NoCodeDataReader dataReader) {
    this.dataReader = dataReader;
    loadTestData();
    Map<String, Object> objectMap = new HashMap<>(dataReader.getDefaultTestData());
    FieldTypeUtil.replaceFieldValue(objectMap);
    defaultTestData.putAll(getMap(objectMap));
  }

  private Map<String, String> getMap(Map<String, Object> dat) {
    Map<String, String> finalmap = new HashMap<>();
    dat.forEach((key, value) -> finalmap.put(key, value.toString()));
    return finalmap;
  }

  private void loadTestData() {
    Map<String, List<Map<String, String>>> temp = dataReader.getTestData();
    temp.forEach((key, value) -> {
      List<Map<String, String>> data = new ArrayList<>();
      value.forEach(val -> {
        Map<String, Object> objectMap = new HashMap<>(val);
        FieldTypeUtil.replaceFieldValue(objectMap);
        data.add(getMap(objectMap));
      });
      testDataMap.put(key, data);
    });
  }

  public List<Map<String, String>> getTestCaseData(String testCaseId) {
    List<Map<String, String>> data = new ArrayList<>();
    if (testDataMap.getOrDefault(testCaseId, Collections.emptyList()).isEmpty()) {
      data.addAll(testDataMap.getOrDefault("default", Collections.emptyList()));
      return data;
    }
    for (Map<String, String> map : testDataMap.getOrDefault(testCaseId, Collections.emptyList())) {
      Map<String, String> defaultData = testDataMap.getOrDefault("default", Collections.emptyList())
          .get(0);
      defaultData.forEach((key, vale) -> {
        if (!map.containsKey(key)) {
          map.put(key, vale);
        }
      });
      data.add(map);
    }
    return data;
  }

  public String getDefaultTestData(String key) {
    if (!defaultTestData.containsKey(key)) {
      throw new ZinfiTechException(key + " key not found");
    }
    return defaultTestData.get(key);
  }

  public Object getDynamicTestData(Map<String, Object> dynamicDataMap, String key) {
    if (!dynamicDataMap.containsKey(key)) {
      throw new ZinfiTechException(
          new ZinfiTechException(key + " key not not found dynamic test case data"));
    }
    return dynamicDataMap.get(key);
  }

  public String getData(String testDataKey) {
    return defaultTestData.get(testDataKey);
  }
}
