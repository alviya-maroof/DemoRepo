# Zinfitech Automation Framework Overview


* * *

Its the automation testing framework written using java and maven which is build on top of sauron library.

This framework helps tester to add the test cases without coding knowledge.

Test cases and its steps can be written in the google sheets.

We can enable or disable the test cases in the sheet. We can enable or disable required steps for the test cases.

We can have difference sheet for different environment. Meaning we can have set of test cases or the steps enabled or disable for required environment.

## 1\. Getting Started:

Before you start using the framework, you need to get the following credentials from Qapitol:

* nexus-username
* nexus-password

**Note**: Framework uses Sauron4. Sauron V4 does not have licensing for specific features and if you have obtained the credentials, you can use all the capabilities provided by the library.

## Install Software Dependencies

Install below in your system.

* Java 11 (JDK - 11)
* Maven

### Verify the installed tools

Open command prompt or terminal in your machine and give below command to verify java version.

```java
java --version
```

Verify `javac --version` using below command

```java
javac --version
```

It should show java version as 11

While giving above command, if it gives any error, check environment variable of `JAVA_HOME`.

If `JAVA_HOME` is not set as environment variable, set the location of java directory as `JAVA_HOME`

Also set the JAVA\_HOME/bin folder as the `PATH` variable

Open command prompt or terminal in your machine and give below command to verify whether maven is installed or not.

```java
mvn --version
```

It should show the maven version

If `mvn --version` is not working, set bin folder of maven directory in the `PATH` variable

##### Additional Dependencies:

[IntelliJ](https://www.jetbrains.com/idea/) - Recommended IDE for the Framework

[Allure](https://docs.qameta.io/allure/) - Used for generating test reports.

## 2\. Clone the project:

Get repository access from Qapitol’s Team.

Below is the repository link:

`https://bitbucket.org/qapitol/zinfitech.git`

**Note:**

Before cloning the project, make sure git is installed in your machine or not.

Use `git --version` in command prompt or terminal to check whether git is installed or not

It should show the version of the git if it was already installed.

Install [git](https://git-scm.com/downloads) if it was unavailable in your machine.

### Follow below steps to clone the project:

1. Open command prompt or the terminal in your machine.
2. Go to the directory where you want to clone the project.
3. Use below command to clone the project in your current directory

`git clone https://bitbucket.org/qapitol/zinfitech.git`

Give your bitbucket credential if it asks for credential during cloning.

1. After cloning you should able to see

`zinfitech` directory in your current path.

## 3\. To run:

Use below maven command to run the projects. Given `<nexus-username>` , `<nexus-password>` and

`<env-name>`

`mvn clean test -s settings.xml -Dnexus-username=<nexus-username> -Dnexus-password=<nexus-password> -DsuiteXmlFile=zinfitech.xml -Denv=<env-name>`

Eg:

`mvn clean test -s settings.xml -Dnexus-username=userone -Dnexus-password=12345 -DsuiteXmlFile=zinfitech.xml -Denv=ft47`

This will start executing the tests and the report will get generated

## 4\. Google Sheet:

The framework expects four sheets

1. Locator Sheet
2. Test Case Sheet
3. Test Step Sheet
4. Test Data Sheet

Test case sheet, Test step sheet and Test data sheet needs to be added as 3 sub sheets in the single google spread sheet for one environment.

We can have multiple these 3 set of sub sheets for multiple environment.

There will be only one master locator sheet irrespective of any environment

| **Sheets** | **Staging** | **Production** | **ft47** |
| --- | --- | --- | --- |
| Sheet with 3 sub sheets | Sheet for staging environment (3 sub sheets: TestCaseSheet, Steps and TestData) | Sheet for production environment (3 sub sheets: TestCaseSheet, Steps and TestData) | Sheet for ft47 environment (3 sub sheets: TestCaseSheet, Steps and TestData) |
| Locator sheet | LocatorsMasterData (sub sheet name: locators) | LocatorsMasterData (sub sheet name: locators) | LocatorsMasterData (sub sheet name: locators) |

Each sheet id and the sub sheet name will be given in the properties file of the respective environment

### Google API integration:

We need to enabled Google SDK for using google sheet in our framework.

This is one time activity which is needed while starting.

Please follow the steps in [Enable Google SDK](https://developers.google.com/drive/api/guides/enable-sdk)

Once you enabled and downloaded the credential json file using above link, copy the file into `src/test/resources` with file name `credential.json` (If any file exists with same name, replace with newly downloaded file)

At very first time, framework will open the browser and asks for login with your google account and give the access for the sheet. Please enable and give access. So that framework can access the google sheet from next run. This will ask only at very first time.

## 5\. Test Case:

All test cases needs to be added in the sub sheet `TestCaseSheet`

It has 5 columns

1. TestCaseId
2. Description
3. TestData
4. Enable / Disable
5. Tag

#### TestCaseId:

Each test case should have unique test case id. Steps in other sub sheet will be mapped using this test case id.

Eg: `AUTOMATION101`

#### Test Description:

Test description is the title or the summary of the test.

Eg: `Login with valid user credential and verify whether login is success or not`

#### TestData:

Here test data name should be given. So that the set of test data which in to other sub sheet will get mapped to given test case. This is comma separator. We can map more set of test data to a test case.

Eg: `credential, addressDetails`

#### Enable / Disable:

This is enable or disable the test case. Only the enabled test case will be running during execution. Disabled test cases will be skipped.

#### Tag:

Tag is used to group the set of test cases and we run the required group.

We can different group like `Sanity` , `Regression` or any other group.

To run specific group, give the tag name in the property file as like below. So that it will run only test cases which has the given tag name

Eg:

`tag=Sanity`

## 6\. Steps:

All steps of test cases should be added in the sub sheet `Steps`

`Steps` sheet has below columns

1. TestCaseId
2. Steps
3. Locators
4. Description
5. Output
6. Enable/Disable

#### TestCaseId:

Test case id of the test case needs to be added in this field. It should have same test case id which is added in the sub sheet `TestCaseSheet`

Using TestCaseId, the test case in `TestCaseSheet` and all steps in `Steps` will get mapped.

Eg: `AUTOMATION101`

#### Steps:

Steps is the place where you need to give the action we need to do in the page.

You can find all available actions as the methods in `com.zinfitech.pages.WebActionPage` class.

You can write your own actions or custom steps in `com.zinfitech.pages.WebActionPage` class and we can call from the step field.

Eg:

`waitAndClick`

`waitAndSetTexts`

`scrollToElement`

Refer below table for available action methods and its details

#### Locators:

In this field you need to give the inputs which is required for the action steps.

Action method can take n number of parameters. That depends on the action step.

In most of the case, the method takes locator as the first input, and the data as the next input.

To give locator as the input, give like`locator.locatorName`

The value of the given `locatorName` will be fetched from the locator sheet

To give any test data to the step, give like `dataKeyName.key`

The `dataKeyName` which we are giving here should be mapped with test case in `TestCaseSheet`

and it should also be available in `TestData` and the given `key` should be available in the Json of the data

To give the input or data directly, use double quote `"Bangalore"`

Data which is coming for the output of the step can be given with same name using `$` symbol. Eg: `$userId`

Below few examples show how the locator or the data needs to be given in locator fields

Eg:

`locator.loginInput, credential.userName`

`locator.submitButtom, address.city, address.state, "India"`

`credential.userName, "Raja"`

Refer below table for available action methods and its details

**Note:**

If the given locator or the data is unavailable, it will through the error

#### Description:

In description field we can give the details about the action step

Eg:

`Give email in the email input field`

`Give password in the password field`

`Click submit button`

#### Output:

Output field is used to get the return value in the variable for the given action step.

That return variable can be used by other further steps.

For getting the return value in a variable, you need to give any variable name starting with `$` symbol

Example: Consider an action step `waitAndGetText` which returns the variable. We can take the variable `$name`

Eg:

`$name`

`$actualValue`

#### Enable/Disable:

This is the check box used to enable or disable the step.

Only the enabled steps will be executed in the test cases.

Disabled step will be ignore for the test case

## Step actions available:

|     |     |     |     |
| --- | --- | --- | --- |
| **Steps** | **Parameters** | **Output** | **Details** |
| launch | 1st parameter - url | NA  | This is to launch the browser with given url |
| waitAndSetTexts | 1st parameter - locator<br><br>2nd parameter - text value | NA  | This to set the text value for the input element |
| waitAndGetTexts | 1st parameter - locator | Value - Give text value of the element | This is to get the text value of the element |
| waitAndClick | 1st parameter - locator | NA  | This is to click the element once it is available |
| selectOptionsByText | 1st parameter - locator<br><br>2nd parameter - text value | NA  | This is to select the text in drop down |
| selectOptionsByValue | 1st parameter - locator<br><br>2nd parameter - text value | NA  | This is to select the value in drop down |
| waitForElement | 1st parameter - locator | NA  | This is to wait for element to be present |
| assertEquals | 1st parameter - actual value<br><br>2nd parameter - expected value | NA  | This is used to verify whether both values are equal or not |
| concatString | 1st parameter - 1st string<br><br>2nd parameter - 2nd string | string | Used to concat both string and give as single string |
| deSelectOptionsByText | 1st parameter - locator<br><br>2nd parameter - text value | NA  | This is to de-select the text in drop down |
| deSelectOptionsByValue | 1st parameter - locator<br><br>2nd parameter - text value | NA  | This is to de-select the value in drop down |
| deSelectAllOption | 1st parameter - locator | NA  | This is to de-select all in drop down |
| contextClicks | 1st parameter - locator/context | NA  | Used to click the context |
| browsersGrantPermLocationViaCDP | NA  | NA  | This is to give location permission |
| browsersGrantPermMicrophoneViaCDP | NA  | NA  | This is to give microphone permission |
| browsersGrantPermNotificationsViaCDP | NA  | NA  | This is to give notification permission |
| browsersResetPermViaCDP | NA  | NA  | This is to reset all the permission |
| navigate | 1st parameter - url | NA  | Used to navigate to the URL |
| scrollIntoView | 1st parameter - locator | NA  | Used to scroll into the view |
| scrollByGivenAmount | 1st parameter - locator | NA  | Used to scroll to element using the location |
| generateRandomEmailId | NA  | string - random email id | Used to give random email id |
| scrollToElement | 1st parameter - locator | NA  | Used to scroll to a element |
| getDayMonthYear | NA  | NA  | Used to get the current date |
| verifyElementIsClickable | 1st parameter - locator | NA  | Verify element clickable or not |
| waitTillElementClickable | 1st parameter - locator | NA  | Wait till element clickable |
| waitTillElementPresent | 1st parameter - locator | NA  | Wait till element present |
| clickElementUsingJS | 1st parameter - locator | NA  | Click once element was clickable |
| waitUntilSelectOptionsPopulated | 1st parameter - locator | NA  | Wait until options was loaded in the SELECT tag |
| refreshPage | NA  | NA  | Used to refresh the page |
| elementDisabled | 1st parameter - locator | NA  | Verify whether element disabled or not |
| singleScroll | NA  | NA  | Used to scroll |
| verifyElementIsNotClickable | 1st parameter - locator | NA  | Verify whether element not clickable |
| saveCurrentWindowOrTabSession | NA  | String | Used to save session |
| waitAndClickStillElementDisplayed | 1st parameter - locator | NA  | Wait till element to be displayed. Then do click action |
| openNewBrowserTab | 1st parameter - url | NA  | Used to open new browser tab |
| generateRandomEmailId | NA  | string | Used to generate random email |
| generateRandomName | Na  | string | Used to give random name |
| getExistingOpenedWindowOrTab | 1st parameter - tab/window | NA  | Used to get existing opened window or tab and switch |
| longWaitForElement | 1st parameter - locator | NA  | Used to wait for long till the element appears |
| clickAndAlertAccept | 1st parameter - locator | NA  | Used to click the element and then accept the alert |
| alertAccept | Na  | NA  | Used to accept the alert |
| openNewBrowserWindow | 1st parameter - url | NA  | Used to open new browser window with given url |

Note:

If the locator has dynamic values, we can pass the values as the next parameters.

Based on number of dynamic values, we can pass that many number of parameters

## 7\. Test Data:

All test data needed for the test case should to be added in the sub sheet `TestData`

It has two columns

1. DataName
2. Datas

### DataName:

`DataName` is the name of set of test datas

Test data will be given the test case using the `DataName` in the column `TestData` in the sub sheet `TestCaseSheet`

For a test case, we can give multiple data names

Eg:

`userCredential`

`addressDetails`

### Datas:

`Datas` is the json which has more keys and its values of test Data

Eg:

`{"username":"Ram","password":"12345"}`

`{"address":"north streen","city":"Bangalore","State":"Karnataka"}`

### Example of the sheet:

![](https://sauron-qapitol.atlassian.net/wiki/download/attachments/125861907/Screenshot%202023-04-13%20at%2012.51.46%20PM.png?version=1&modificationDate=1681983786926&cacheVersion=1&api=v2)

## 8\. Locators:

All locators will be added in the single master locator sheet.

For mapping locator sheet with the framework, add the sheet id and sub sheet name in the properties file.

```java
google.sheet.locators.master.id=<sheet_id>
google.sheet.locators=<sub_sheet_name>
```

Eg:

In googe\_sheet-ft47.properties, add below two properties

```java
google.sheet.locators.master.id=1vCic-E1JMYs0scHFO4so1NbFQgCe-hmCIcgs72UMnmY
google.sheet.locators=locators
```

In sheet, we need to give key and values. Where key is the any custom name of the locator and value is the actual locator

Below you can find the sample:

![](https://sauron-qapitol.atlassian.net/wiki/download/attachments/125861907/Screenshot%202023-04-13%20at%202.29.26%20PM.png?version=1&modificationDate=1681983848571&cacheVersion=1&api=v2)

We can use the `locator.name` in the steps sub sheet to give the locator

Eg:

`locator.emailInputField`

`locator.submitButton`

### Types of locator

Locator should be given along with its locator type

`<locator type>=<locator>`

By default, the locator will be consider as the xpath if you not given locator type

Eg:

`id=name`

`css=#root > main > div > div > div > div > div > form > button`

`xpath=//a[contains(text(),'Opportunities')]`

### Dynamic locator

If locator is dynamic.

You can given `{0}` in place of locator where it needs to be changed. We can pass the value when calling the action step.

Eg:

|     |     |
| --- | --- |
| **name** | **value** |
| optionValue | `xpath=//a[contains(text(),'{0}')]` |

While calling the step Eg: `waitAndClick`, we can pass those dynamic value in run time Eg: `locator.Value, "Opportunities"`

If there are multiple dynamic value , we can have more `{0},{1},{2}..`

|     |     |
| --- | --- |
| **name** | **value** |
| age | `xpath=//div/[@class='{0}']/div/a{1}` |

### Multiple locator

We can give multiple locators for an element.

If one locator fails, the framework will automatically pick the next locator.

Initially it will try to identify first locator, if first fails, then it will pick the next and so on..

We can give n number of locator for same element.

|     |     |
| --- | --- |
| **name** | **value** |
| age | `xpath=//div/[@class='{0}']/div/a{1}` |

## 9\. Config or Properties files:

In `src/test/resources` you can find the properties files.

Properties file will be in the format of

`google_sheet-<env>.properties`

Eg:

`google_sheet-prod.properties`

`google_sheet-staging.properties`

`google_sheet-dev.properties`

`google_sheet-ft47.properties`

For different environment we can have different google sheet wit set of sub sheets for test cases, test steps and test data.

All sheets can be mapped with framework using properties file.

Below are the properties available in properties file

```java
tag=sanity
google.sheet.id=1oeRxdkzMfYB-1uTUpSx3Mq8eeaL5OXMeyaWaFvvpTaA
google.sheet.locators.master.id=1vCic-E1JMYs0scHFO4so1NbFQgCe-hmCIcgs72UMnmY
google.sheet.testcase=TestCaseSheet
google.sheet.steps=Steps
google.sheet.locators=locators
google.sheet.testdata=TestData
screenshot.mode=true
```

`google.sheet.id` - Here you can given the sheet id for the environment.

You can get the sheet id in the url when you open the google sheet

```java
https://docs.google.com/spreadsheets/d/<Here we can find sheet id>/edit#gid=605010047
```

`google.sheet.locators.master.id` - Here you can give the sheet of the locator sheet

`google.sheet.testcase` - Here you can give the sub sheet name of test case sheet

`google.sheet.steps` - Here you can give the sub sheet name of the step sheet

`google.sheet.locators` - Here you can give the sub sheet name of the locator sheet

`google.sheet.testdata` - Here you can give the sub sheet name of the test data sheet

We have 2 more properties

`tag` - Give the tag name which we want to run. Eg: `tag=sanity`

`screenshot.mode` - Here we can enable or disable the screenshot for each step. If it is disabled, it will take screenshot only for the error.

### Command:

When running using the command we can pass the environment name in `-Denv=name`

along with maven command. So that it will pick the property file of that environment for executing.

Eg:

`clean install -s settings.xml -Dnexus-username=<give-nexus-username> -Dnexus-password=<give-nexus-password> -DsuiteXmlFile=zinfitech.xml -Denv=ft47`

## 10\. Custom Step:

We can find all the action step methods in `com.zinfitech.pages.WebActionPage` class.

We can add the step method in `com.zinfitech.pages.WebActionPage` class if it is unavailable.

To add step method, create a method in `com.zinfitech.pages.WebActionPage` class.

Method should take `TestStep testStep` as the parameter.

Inside the method, we can call any action methods which is available in the parent class `AbstractWebPage` (Which is available in sauron library) and we need to pass `locatorUtil.getLocator(testStep)` as the parameter for the methods we are call

Below is the sample step method

```java
public void waitAndClickOn(TestStep testStep) {
    waitAndClickOn(locatorUtil.getLocator(testStep));
  }
```

## Own custom steps

To add own custom method, All above steps remains same.

Instead of calling methods which are in `AbstractWebPage` class, we will creating our own method.

We can get the driver object using `Grid.driver()` , then we can call any method related to selenium driver.

Below is the sample own custom step method

```java
public void waitAndSetValue(TestStep testStep) {
    Grid.driver().findElement(testStep.getLocators.get(0)).sendKeys(testStep.getTestData().get(0));
  }
```

## 11\. Framework Details:

## Structure of project:

`src/main/java` - Here you can find all packages and classes related to this framework changes.

`src/main/test` - Here you can find the packages and classes related to TestRunner

`src/main/resources` - Here you can find `allure.properties` and `categories.json` . You no need to modify these files.

`src/main/resources` - Here you can find all properties files, downloaded `credential.json` google authentication file, `sessions.yaml` file and `zinfitech.xml` (testng.xml) file.

`pom.xml` - An Apache Maven build configuration file which contains information about the project, configuration details, and all the dependencies required for the framework.

`settings.xml` - This file stores the username and password required while running test suites using Sauron V4 in a CI/CD pipeline.

`tokens/StoredCredential` - Authenticated google binaries will be get stored here once we login to the google sheet for the first time

Below files will be generated/changed for every run.

`sauronFiles/sauronLogs` - Here it will stores automation log file

`sauronFiles/name**.csv` - This folder stores CSV log report

`allure-results` - This folder stores the json file of allure report

`test-output` - This folder stores the testng related all reports and report related files

`target` - This folder stores all binary files of the project

**zinfitech.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite verbose="1" name="Zinfi automation" annotations="JDK" thread-count="1"
  data-provider-thread-count="1">
  <parameter name="pagesPackage" value="com.zinfitech.context"/>
  <test name="Zinfi Testcases">
    <parameter name="sessions" value="sessions.yaml"/>
    <parameter name="executionTimeout" value="15000"/>
    <classes>
      <class name="com.zinfitech.TestRunner"/>
    </classes>
  </test>
</suite>
```

From this xml file, we need to pass the parameter which is needed for sauron.

Sauron will read the parameters and its value available and run the test based on given inputs/configuration

**sessions.yaml**

```java
- name: "session_web_1"
  local: true
  platform: "chrome"
  browserHeight: 1920
  browserWidth: 1080
  browserRunHeadless: false
- name: "session_web_2"
  local: true
  platform: "firefox"
  browserHeight: 1920
  browserWidth: 1080
  browserRunHeadless: false
```

Here we need to give the inputs which is required for running the automation.

Based on this inputs, sauron will run the automation in given number of sessions, in given platform with given configurations.

## 12\. Reports:

Framework will generate below two reports after test execution

1. Allure report
2. CSV log report

## Allure Report:

Allure report will be stored as json format in `allure-results` directory in the root of the project folder.

Follow below steps to generate html allure report

1. Open command prompt or terminal
2. Go to the root of the project.
3. Use below command to generate html report

`allure serve`

1. Above command will open the allure html report in the browser.

**Note:**

If allure command is not working, install [Allure](https://docs.qameta.io/allure/) in your machine and then try `allure serve` command

## CSV log report:

CSV log report will be get generated in `sauronFiles` directory for every execution.

In CSV log report, you can find result of each test cases along with result of each step
