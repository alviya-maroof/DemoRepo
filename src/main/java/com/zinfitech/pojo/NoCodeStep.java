package com.zinfitech.pojo;

import com.google.gson.annotations.SerializedName;
import com.zinfitech.zinfilistners.TestResult;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class NoCodeStep {

  @SerializedName(value = "STEPS")
  private String actionName;
  @SerializedName(value = "LOCATORS/INPUTS")
  private List<String> locators;
  @SerializedName(value = "OUTPUT")
  private String output;
  @SerializedName(value = "TESTCASEID")
  private String testCaseId;
  @SerializedName(value = "ENABLE/DISABLE")
  private boolean isEnabled;
  @SerializedName(value = "DESCRIPTION")
  private String name;
  private Map<String, Object> dynamicData;
  private TestResult result;
  private int invocationCount;
  private Map<String,String>stepData;
}
