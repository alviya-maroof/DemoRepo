package com.zinfitech.pojo;

import com.google.gson.annotations.SerializedName;
import com.zinfitech.headers.TestCaseHeader.Priority;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCase implements Cloneable {

  @SerializedName(value = "ENABLE/DISABLE")
  private boolean isEnabled;
  @SerializedName(value = "TESTCASEID")
  private String testCaseId;
  @SerializedName(value = "TestStep")
  private List<TestStep> testStep;
  @SerializedName(value = "DESCRIPTION")
  private String testCaseName;
  @SerializedName("TAG")
  private List<String> tag;
  @SerializedName("MODULE NAME")
  private String moduleName;
  @SerializedName("PRIORITY")
  private Priority priority;
}
