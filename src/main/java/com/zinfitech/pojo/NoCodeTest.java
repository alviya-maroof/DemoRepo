package com.zinfitech.pojo;

import com.google.gson.annotations.SerializedName;
import com.zinfitech.headers.TestCaseHeader.Priority;
import com.zinfitech.zinfilistners.TestResult;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class NoCodeTest {

  @SerializedName(value = "TESTCASEID")
  private String testCaseId;
  @SerializedName(value = "ENABLE/DISABLE")
  private boolean isEnabled;
  @SerializedName(value = "DESCRIPTION")
  private String name;
  @SerializedName("TAG")
  private List<String> tags;
  @SerializedName("MODULE NAME")
  private String moduleName;
  @SerializedName("PRIORITY")
  private Priority priority;
  private TestResult testResult;
  private List<NoCodeStep> steps;
  private Map<String, Object> dynamicData;
  int invocationCount;
}
