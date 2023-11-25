package com.zinfitech.allurereport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zinfitech.zinfilistners.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(toBuilder = true)
@Getter
@Setter
public class CsvReportModel {

  @JsonProperty("TC_ID")
  private String testCaseId;
  @JsonProperty("Test_Description")
  private String testCaseName;
  @JsonProperty("Test_Status")
  private Status testCaseStatus;
  @JsonProperty("Step_Name")
  private String stepName;
  @JsonProperty("Action_Event")
  private String event;
  @JsonProperty("tags")
  private String tag;
  @JsonProperty("Step_Status")
  private Status stepStatus;
  @JsonProperty("Test_Date")
  private String date;
  @JsonProperty("Test_Time")
  private String time;
  @JsonProperty("Screenshot")
  private String screenShot;
  @JsonProperty("Test_Data")
  private String testData;
  @JsonProperty("Error")
  private String error;
}
