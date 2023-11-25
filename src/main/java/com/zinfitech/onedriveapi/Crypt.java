package com.zinfitech.onedriveapi;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Crypt {

  private String algorithm;
  private String key;
}
