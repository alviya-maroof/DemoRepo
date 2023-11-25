package com.zinfitech.model;

import com.zinfitech.data.NoCodeDataReader;
import com.zinfitech.pojo.ZiniFunctionalInterface;
import com.zinfitech.zinfiexception.ZinfiTechException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocatorModel implements ZiniFunctionalInterface {

  private static final String LOCATOR = "locator";
  private final NoCodeDataReader dataReader;
  private static final Map<String, String> locators = new ConcurrentHashMap<>();

  public LocatorModel(NoCodeDataReader dataReader) {
    this.dataReader = dataReader;
    loadLocators();
  }

  private void loadLocators() {
    for (Map<String, String> loc : dataReader.getLocators()) {
      locators.putAll(loc);
    }
  }

  public String getLocatorFroLoadLoadLocator(String listLocators) {
    List<String> getLocatorType = splitString(splitByDot, listLocators);
    if (getLocatorType.size() == 2 && (getLocatorType.contains(LOCATOR))) {
      if (locators.containsKey(getLocatorType.get(1))) {
        return locators.get(getLocatorType.get(1));
      } else {
        throw new ZinfiTechException(new ZinfiTechException(listLocators
            + "locator name not found locators master data, update correct locator name"));
      }
    }
    throw new ZinfiTechException(new ZinfiTechException(
        listLocators + "locator key missing expecting locator format like: locator.locatorName "));
  }
}
