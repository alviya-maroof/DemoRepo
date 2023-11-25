package com.zinfitech.context;

import com.qapitol.sauron.web.AbstractWebPage;
import com.zinfitech.data.ZinfiTechDataFactory;
import com.zinfitech.model.LocatorModel;
import com.zinfitech.model.NoCodeTestModel;
import com.zinfitech.model.TestDataModel;
import com.zinfitech.pojo.NoCodeStep;
import com.zinfitech.pojo.ZiniFunctionalInterface;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.ZipException;

public class ZinfiAbstractPage extends AbstractWebPage implements ZiniFunctionalInterface {

  private static final String LOCATOR = "locator";

  private final TestDataModel testDataModel = NoCodeTestModel.getModelService(
      ZinfiTechDataFactory.getDataReader()).getTestDataModel();

  private final LocatorModel locatorModel = NoCodeTestModel.getModelService(
      ZinfiTechDataFactory.getDataReader()).getLocatorModel();

  @Override
  public String getLocator(Supplier<String> siteLocale,
      Supplier<String> targetClient, String... key) {
    String locator = key[0];
    if (key.length > 1) {
      MessageFormat format = new MessageFormat(locator);
      locator = format.format(Arrays.copyOfRange(key, 1, key.length));
    }
    return formatIgnoreCase(locator);
  }


  @Override
  protected void loadLocators() {
    // NO CODE IMPLEMENTED
  }

  public List<Object> getLocatorAsList(NoCodeStep testStep) {
    List<Object> arguments = new ArrayList<>();
    for (String loc : testStep.getLocators()) {
      if (startStringWith(dollarBiPredicate, loc, 0)) {
        arguments.add(testDataModel.getDynamicTestData(testStep.getDynamicData(), loc));
      } else if (startStringWith(doubleQuoteBiPredicate, loc, 0)) {
        arguments.add(loc);
      } else if (splitString(splitByDot, loc).size() >= 2 && !splitString(splitByDot, loc).contains(
          LOCATOR)) {
        if (testStep.getStepData().containsKey(splitString(splitByDot, loc).get(1))) {
          arguments.add(testStep.getStepData().get(splitString(splitByDot, loc).get(1)));
        } else {
          arguments.add(testDataModel.getDefaultTestData(splitString(splitByDot, loc).get(1)));
        }
      } else {
        arguments.add(locatorModel.getLocatorFroLoadLoadLocator(loc));
      }
    }
    return arguments;
  }

  @SuppressWarnings("unchecked")
  public <T> T getLocator(NoCodeStep testStep) {
    return (T) getLocatorAsList(testStep).toArray(new String[0]);
  }

  @SuppressWarnings("unchecked")
  public <T> T getLocatorAsObject(NoCodeStep testStep) {
    return (T) getLocatorAsList(testStep).toArray(new Object[0]);
  }


  public Object getActualAndExpected(String arg, NoCodeStep testStep) {
    if (startStringWith(dollarBiPredicate, arg, 0)) {
      return testDataModel.getDynamicTestData(testStep.getDynamicData(), arg);
    } else if (startStringWith(doubleQuoteBiPredicate, arg, 0)) {
      return arg;
    } else if (splitString(splitByDot, arg).size() >= 2 && !splitString(splitByDot, arg).contains(
        LOCATOR)) {
      return "";
    }
    return "";
  }

  public String getFormattedString(NoCodeStep testStep) {
    List<String> args = new ArrayList<>();
    String format = getActualAndExpected(testStep.getLocators().get(0), testStep).toString();
    for (int i = 1; i < testStep.getLocators().size(); i++) {
      args.add(getActualAndExpected(testStep.getLocators().get(i), testStep).toString());
    }
    return MessageFormat.format(format, args.toArray());
  }

  public String getAttachmentLocation(NoCodeStep testStep) throws ZipException {
    Path attachLocation = Paths.get("", "files",
        (String)getLocatorAsList(testStep).get(0));
    if (!attachLocation.toFile().exists()) {
      throw new ZipException(attachLocation.toFile().getAbsolutePath()
          + " file not found at given attachments directory");
    }
    return attachLocation.toFile().getAbsolutePath();
  }
}
