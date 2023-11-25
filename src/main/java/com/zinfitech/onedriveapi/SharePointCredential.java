package com.zinfitech.onedriveapi;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SharePointCredential {

  @SerializedName(value = "clientId")
  private String clientId;
  @SerializedName(value = "clientSecret")
  private String clientSecret;
  @SerializedName(value = "tenantId")
  private String tenantId;
  @SerializedName(value = "driveId")
  private String driveId;
  @SerializedName(value = "emailId")
  private String emailId;
  @SerializedName(value = "encrypted")
  private boolean encrypted;
  @SerializedName(value = "crypt")
  private Crypt crypt;
}
