package com.zinfitech.config;

import com.qapitol.sauron.common.capabilities.DefaultCapabilitiesBuilder;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

public class CustomCapabilities extends DefaultCapabilitiesBuilder {

  @Override
  public DesiredCapabilities getCapabilities(DesiredCapabilities desiredCapabilities) {
    if (desiredCapabilities.getBrowserName().equals("chrome")) {
      Map<String, Object> prefs = new HashMap<>();
      prefs.put("autofill.profile_enabled", false);
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--remote-allow-origins=*");
      options.setExperimentalOption("prefs", prefs);
      options.setPageLoadStrategy(PageLoadStrategy.EAGER);
      options.addArguments("--test-type");
      options.addArguments("--ignore-certificate-errors");
      options.addArguments("--no-sandbox"); // Bypass OS security model
      options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
      options.addArguments("disable-infobars"); // disabling infobars
      options.addArguments("--disable-extensions"); // disabling extensions
      options = options.merge(desiredCapabilities);
      desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
      desiredCapabilities.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
    } else if (desiredCapabilities.getBrowserName().equals("MicrosoftEdge")) {
      EdgeOptions options = new EdgeOptions();
      Map<String, Object> prefs = new HashMap<>();
      prefs.put("autofill.profile_enabled", false);
      options.addArguments("--remote-allow-origins=*");
      options.setExperimentalOption("prefs", prefs);
      options.setPageLoadStrategy(PageLoadStrategy.EAGER);
      desiredCapabilities.setCapability(EdgeOptions.CAPABILITY, options);
    } else if (desiredCapabilities.getBrowserName().equals("firefox")) {
      FirefoxOptions options = new FirefoxOptions();
      Map<String, Object> prefs = new HashMap<>();
      prefs.put("autofill.profile_enabled", false);
      options.addArguments("--disable-blink-features=AutomationControlled");
      desiredCapabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, options);
    }
    return desiredCapabilities;
  }
}
