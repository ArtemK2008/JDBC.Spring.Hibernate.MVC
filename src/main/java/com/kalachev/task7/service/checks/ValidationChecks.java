package com.kalachev.task7.service.checks;

import com.kalachev.task7.dao.classes.DaoChecks;
import com.kalachev.task7.exceptions.DaoException;
import com.kalachev.task7.exceptions.UiException;

public class ValidationChecks {

  static DaoChecks dao = new DaoChecks();

  private ValidationChecks() {
    super();
  }

  public static boolean checkIfCourseExists(String course) throws UiException {
    boolean isExist = false;
    try {
      if (dao.checkCourseIfExists(course)) {
        isExist = true;
      }
    } catch (DaoException e) {
      e.printStackTrace();
      throw new UiException();
    }
    return isExist;
  }

  public static boolean checkIfStudentAlreadyInGroup(int groupId,
      String firstName, String lastName) throws UiException {
    boolean isInGroup = false;
    try {
      if (dao.checkStudntIfExistsInGroup(firstName, lastName, groupId)) {
        isInGroup = true;
      }
    } catch (DaoException e) {
      e.printStackTrace();
      throw new UiException();
    }
    return isInGroup;
  }

  public static boolean checkIfStudentIdExists(int id) throws UiException {
    boolean isExist = false;
    try {
      if (dao.checkStudentIdIfExists(id)) {
        isExist = true;
      }
    } catch (DaoException e) {
      e.printStackTrace();
      throw new UiException();
    }
    return isExist;
  }

  public static boolean checkIfStudentAlreadyInCourse(int id, String course)
      throws UiException {
    boolean isExist = false;
    try {
      if (dao.checkIfStudentInCourse(id, course)) {
        isExist = true;
      }
    } catch (DaoException e) {
      e.printStackTrace();
      throw new UiException();
    }
    return isExist;
  }
}
