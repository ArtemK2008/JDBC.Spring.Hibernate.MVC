package com.kalachev.task7.utilities;

import org.apache.commons.lang3.math.NumberUtils;

public class ValidationUtills {

  static final String VALID = "valid";
  static final String NOT_VALID = "not valid";

  private ValidationUtills() {
    super();
  }

  public static String validateInput(String input, int min, int max) {
    String result = VALID;
    if (!NumberUtils.isParsable(input)) {
      return "id is not an int";
    }
    int number = Integer.parseInt(input);
    if ((number < min) || (number > max)) {
      result = NOT_VALID;
    }
    return result;
  }

}
