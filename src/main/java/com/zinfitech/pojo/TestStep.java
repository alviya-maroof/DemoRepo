package com.zinfitech.pojo;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TestStep implements Cloneable {

  @SerializedName(value = "ENABLE/DISABLE")
  private boolean isEnabled;
  @SerializedName(value = "STEPS")
  private String action;
  @SerializedName(value = "TESTCASEID")
  private String testCaseId;
  @SerializedName(value = "LOCATORS/INPUTS")
  private List<String> locators;
  @SerializedName(value = "OUTPUT")
  private String output;
  private List<String> testData;
  @SerializedName(value = "DESCRIPTION")
  private String stepName;
}
