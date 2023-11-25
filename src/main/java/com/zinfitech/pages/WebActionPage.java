package com.zinfitech.pages;

import com.qapitol.sauron.core.Grid;
import com.zinfitech.context.ZinfiAbstractPage;
import com.zinfitech.pojo.NoCodeStep;

import com.zinfitech.zinfiexception.ZinfiTechException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

public class WebActionPage extends ZinfiAbstractPage {

  public void launch(NoCodeStep step) {
    Grid.driver().manage().window().maximize();
    Grid.driver().get(((Object[]) getLocator(step))[0].toString());
  }

  public void appLaunch() {
    Grid.driver();
  }

  public void waitAndSetTexts(NoCodeStep step) {
    waitAndSetText(getLocator(step));
  }

  public void waitAndGetTexts(NoCodeStep step) {
    String getText = waitAndGetText(getLocator(step));
    if (StringUtils.isNotEmpty(step.getOutput())) {
      step.getDynamicData().put(step.getOutput(), getText);
    }
  }

  public void verifyElementIsClickable(NoCodeStep step) {
    Assert.assertTrue(waitAndCheckIsElementClickable(getLocator(step)));
  }

  public void waitForElementVisibility(NoCodeStep step) {
    waitAndCheckIsElementPresent(getLocator(step));
  }

  public void assertEquals(NoCodeStep step) {
    Assert.assertEquals(getActualAndExpected(step.getLocators().get(0), step),
        getActualAndExpected(step.getLocators().get(1), step));
  }

  public void concatString(NoCodeStep step) {
    String var1 = getActualAndExpected(step.getLocators().get(0), step).toString();
    String var2 = getActualAndExpected(step.getLocators().get(1), step).toString();
    if (StringUtils.isNotEmpty(step.getOutput())) {
      step.getDynamicData().put(step.getOutput(), var1 + " " + var2);
    }
  }

  public void waitAndClick(NoCodeStep step) {
    waitAndClickOn(getLocator(step));
  }

  public void clickAndAlertAccept(NoCodeStep step) {
    this.waitAndClick(step);
    this.alertAccept();
  }

  public void waitTillElementPresent(NoCodeStep step) {
    waitAndCheckIsElementPresent(getLocator(step));
  }

  public void waitTillElementClickable(NoCodeStep step) {
    waitAndCheckIsElementClickable(getLocator(step));
  }

  public void selectOptionsByText(NoCodeStep step) {
    selectOptionByText(getLocator(step));
  }

  public void selectOptionsByValue(NoCodeStep step) {
    selectOptionByValue(getLocator(step));
  }

  public void deSelectOptionsByText(NoCodeStep step) {
    deSelectOptionByText(getLocator(step));
  }

  public void deSelectOptionsByValue(NoCodeStep step) {
    deSelectOptionByValue(getLocator(step));
  }

  public void deSelectAllOption(NoCodeStep step) {
    deSelectAllOptions(getLocator(step));
  }

  public void contextClicks(NoCodeStep step) {
    contextClick(getLocator(step));
  }

  public void browsersGrantPermLocationViaCDP() {
    browserGrantPermLocationViaCDP();
  }

  public void browsersGrantPermMicrophoneViaCDP() {
    browserGrantPermMicrophoneViaCDP();
  }

  public void browsersGrantPermNotificationsViaCDP() {
    browserGrantPermNotificationsViaCDP();
  }

  public void browsersResetPermViaCDP() {
    browserResetPermViaCDP();
  }

  public void saveCurrentWindowOrTabSession(NoCodeStep step) {
    String tabs = Grid.driver().getWindowHandle();
    if (StringUtils.isNotEmpty(step.getOutput())) {
      step.getDynamicData().put(step.getOutput(), tabs);
    }
  }

  public void switchToWindowOrTab() {
    switchToNextTabOrWindow();
  }

  public void getExistingOpenedWindowOrTab(NoCodeStep step) {
    String windowID = getActualAndExpected(step.getLocators().get(0), step).toString();
    Grid.driver().switchTo().window(windowID);
  }

  public void openNewBrowserTab(NoCodeStep step) {
    String url = getActualAndExpected(step.getLocators().get(0), step).toString();
    openTabs(url);
  }

  public void openNewBrowserWindow(NoCodeStep step) {
    String url = getActualAndExpected(step.getLocators().get(0), step).toString();
    openNewWindow(url);
  }

  public void navigate(NoCodeStep step) {
    navigate((BrowsingContext) getActualAndExpected(step.getLocators().get(0), step),
        getActualAndExpected(step.getLocators().get(1), step).toString());
  }

  public void scrollIntoView(NoCodeStep step) {
    Grid.driver().executeScript("arguments[0].scrollIntoView();", locateElement(getLocator(step)));
  }

  public void singleScroll() {
    Grid.driver().executeScript("window.scrollBy(0,1000)");
  }

  public void scrollByGivenAmount(NoCodeStep step) {
    WebElement footer = locateElement(getLocator(step));
    int deltaY = footer.getRect().y;
    new Actions(Grid.driver()).scrollByAmount(0, deltaY).perform();
  }


  public void scrollToElement(NoCodeStep step) {
    waitAndCheckIsElementPresent(getLocator(step));
    Grid.driver().executeScript(
        "arguments[0].scrollIntoView({behavior: \"auto\", block: \"center\", inline: \"nearest\"});",
        locateElement(getLocator(step)));
  }

  public void implicitWait() {
    Grid.driver().manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
  }

  public void elementEnabled(NoCodeStep step) {
    Assert.assertTrue(waitAndGetElements(getLocator(step)).get(0).isEnabled(),
        "Element wasn't Enabled");
  }

  public void elementDisabled(NoCodeStep step) {
    Assert.assertFalse(waitAndGetElements(getLocator(step)).get(0).isEnabled(),
        "Element wasn't Disabled");
  }

  public void refreshPage() {
    pageRefresh();
  }

  public void acceptAlert() {
    super.alertAccept();
  }

  public void verifyElementIsNotClickable(NoCodeStep step) {
    Assert.assertFalse(waitAndCheckIsElementClickable(getLocator(step)));
  }

  public void verifyElementIsPresent(NoCodeStep step) {
    Assert.assertTrue(waitAndCheckIsElementPresent(getLocator(step)));
  }

  public void getMessageFormattedString(NoCodeStep step) {
    if (StringUtils.isNotEmpty(step.getOutput())) {
      step.getDynamicData().put(step.getOutput(), getFormattedString(step));
    }
  }

  public void clickElementUsingJS(NoCodeStep step) {
    clickUsingJS(locateElement(getLocator(step)));
  }

  public void waitUntilSelectOptionsPopulated(NoCodeStep step) {
    int count = 1;
    while (count <= 10) {
      if (getAllOptions(getLocator(step)).size() > 1) {
        break;
      }
      sleep(1000);
      ++count;
    }
  }

  public void getLocationOfTheFile(NoCodeStep step) throws ZipException {
    if (StringUtils.isNotEmpty(step.getOutput())) {
      step.getDynamicData().put(step.getOutput(), getAttachmentLocation(step));
    }
  }

  private void openTabs(String url) throws ZinfiTechException {
    List<String> oldTabs = new ArrayList(Grid.driver().getWindowHandles());
    Grid.driver().executeScript("window.open()", new Object[0]);
    List<String> tabs = new ArrayList(Grid.driver().getWindowHandles());
    boolean foundNewTab = false;
    Iterator var5 = tabs.iterator();

    while (var5.hasNext()) {
      String tabId = (String) var5.next();
      if (!oldTabs.contains(tabId)) {
        Grid.driver().switchTo().window(tabId);
        Grid.driver().get(url);
        foundNewTab = true;
        break;
      }
    }
    if (!foundNewTab) {
      throw new ZinfiTechException("No new Tab found");
    }
  }

  public void sleepTillPage() {
    sleep(3000);
  }

  public void textBoxClear(NoCodeStep step) {
    clearTextBox(getLocator(step));
  }

  public void navigateBack(NoCodeStep step) {
    sleep(5000);
    JavascriptExecutor js = (JavascriptExecutor) Grid.driver();
    js.executeScript("window.history.go(-1)");
  }

  public void clickElementAcceptAlert(NoCodeStep step){
    try{
      waitAndClickOn(getLocator(step));
    } catch (UnhandledAlertException f) {
      f.printStackTrace();
    }
    finally {
      Alert alert = Grid.driver().switchTo().alert();
      alert.accept();
    }
  }

  public void saveWebElement(NoCodeStep step) {
    WebElement element = locateElement(getLocator(step));
    step.getDynamicData().put(step.getOutput(), element);
  }


  public void dragAndDrop(NoCodeStep step) {
    Object[] wb = getLocatorAsObject(step);
    WebElement source = (WebElement) wb[0];
    WebElement destination = (WebElement) wb[1];
    Actions actions = new Actions(Grid.driver());
    actions.dragAndDrop(source, destination).build().perform();
  }

  public void clearTextAndPassingInput(NoCodeStep step) {
    try {
      Thread.sleep(6000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    Object[] wb = getLocatorAsObject(step);
    WebElement codeMirror = (WebElement) wb[0];
    String htmlContext = (String) wb[1];
    Actions actions = new Actions(Grid.driver());
    actions.moveToElement(codeMirror).click();
    actions.keyDown(Keys.CONTROL);
    actions.sendKeys("a");
    actions.keyUp(Keys.CONTROL);
    actions.perform();
    actions.moveToElement(codeMirror).sendKeys(Keys.BACK_SPACE).perform();
    actions.click(codeMirror).sendKeys(Keys.CLEAR).build().perform();
    actions.sendKeys(htmlContext).perform();
  }

  /**
   * Switching to the Main window frame
   * @param step
   */
  public void frameNewWindow(NoCodeStep step) {
    Object[] wb = getLocatorAsObject(step);
    WebElement destination = (WebElement) wb[0];
    Grid.driver().switchTo().frame(destination);
  }
  /**
   * Switching to the default content
   */
  public void frameDefaultContent() {
    Grid.driver().switchTo().defaultContent();
  }

  public void clickAlert(NoCodeStep step){
    try{
      clickUsingJS(locateElement(getLocator(step)));
    } catch (UnhandledAlertException f) {
      f.printStackTrace();
    }
    finally {
      Alert alert = Grid.driver().switchTo().alert();
      alert.accept();
    }
  }

}
