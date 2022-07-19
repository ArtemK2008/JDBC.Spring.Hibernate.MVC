package com.kalachev.task7.utilities;

import org.apache.commons.lang3.math.NumberUtils;

public class ControllerUtills {

  private ControllerUtills() {
    super();
  }

  public static String validateSize(String size) {
    String result = "valid";
    if (!NumberUtils.isParsable(size)) {
      return " not an int";
    }
    if (Integer.parseInt(size) < 0) {
      result = "size cant be negative";
    }
    return result;
  }

}
