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

  public static String validateGroupId(String groupId) {
    String result = "valid";
    if (!NumberUtils.isParsable(groupId)) {
      return "group id is not an int";
    }
    int id = Integer.parseInt(groupId);
    if (id < 1 || id > 11) {
      result = "Wrong groupd id";
    }
    return result;
  }

  public static String validateStudentId(String studentId) {
    String result = "valid";
    if (!NumberUtils.isParsable(studentId)) {
      return "id is not an int";
    }
    int id = Integer.parseInt(studentId);
    if (id < 1) {
      result = "Wrong student id";
    }
    return result;
  }

}
