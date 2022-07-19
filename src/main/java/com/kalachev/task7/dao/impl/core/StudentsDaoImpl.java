package com.kalachev.task7.dao.impl.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kalachev.task7.dao.StudentsDao;
import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.utilities.ConnectionManager;
import com.kalachev.task7.utilities.JdbcUtil;

@Component
public class StudentsDaoImpl implements StudentsDao {

  private static final String FIND_BY_COURSE = "SELECT student_id, group_id,first_name,last_name "
      + "FROM studentscoursesdata " + "WHERE course_name = (?)";

  private static final String INSERT_STUDENT = "INSERT INTO Students(group_id,first_name,last_name)"
      + " VALUES (?,?,?)";

  private static final String DELETE_STUDENT = "DELETE FROM Students Where student_id = (?)";

  private static final String CHECK_STUDENT_IF_EXISTS_IN_GROUP = "SELECT * FROM Students "
      + "WHERE first_name = (?) AND last_name = (?) AND group_id = (?)";

  private static final String CHECK_STUDENT_ID_IF_EXISTS = "SELECT * FROM Students "
      + "WHERE student_id = (?)";

  private static final String CHECK_IF_STUDENT_IN_COURSE = "SELECT * "
      + "FROM studentscoursesdata " + "WHERE student_id ="
      + " (?) AND course_name = (?)";

  @Override
  public List<Student> findByCourse(String courseName) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    List<Student> students = new ArrayList<>();
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(FIND_BY_COURSE);
      statement.setString(1, courseName);
      rs = statement.executeQuery();
      while (rs.next()) {
        Student student = new Student();
        student.setId(rs.getInt("student_id"));
        student.setGroupId(rs.getInt("group_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        students.add(student);
      }
    } catch (SQLException e) {
      System.out
          .println("Error while geting Students of " + courseName + " course");
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return students;
  }

  @Override
  public boolean insert(String firstName, String lastName, int groupId) {
    boolean isInserted = false;
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(INSERT_STUDENT);
      statement.setInt(1, groupId);
      statement.setString(2, firstName);
      statement.setString(3, lastName);
      statement.executeUpdate();
      isInserted = true;
    } catch (SQLException e) {
      System.out.println("Error while inserting Student " + firstName + " "
          + lastName + " to group " + groupId);
    } finally {
      JdbcUtil.closeAll(statement, connection);
    }
    return isInserted;
  }

  @Override
  public boolean delete(int id) {
    boolean isDeleted = false;
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(DELETE_STUDENT);
      statement.setInt(1, id);
      statement.executeUpdate();
      isDeleted = true;
    } catch (SQLException e) {
      System.out.println("Error while Deleting Student with ID: " + id);
    } finally {
      JdbcUtil.closeAll(statement, connection);
    }
    return isDeleted;
  }

  @Override
  public Map<String, String> studentNamesById() {
    Map<String, String> idsOfEachStuden = new LinkedHashMap<>();
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.createStatement();
      rs = statement.executeQuery(
          "SELECT student_id,first_name,last_name  FROM Students");
      while (rs.next()) {
        idsOfEachStuden.put(rs.getString("student_id"),
            rs.getString("first_name") + " " + rs.getString("last_name"));
      }
    } catch (SQLException e) {
      System.out.println("Error while maping student names to their IDs");
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return idsOfEachStuden;
  }

  @Override
  public boolean isExistsInGroup(String firstName, String lastName,
      int groupId) {
    boolean isExists = false;
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(CHECK_STUDENT_IF_EXISTS_IN_GROUP);
      statement.setString(1, firstName);
      statement.setString(2, lastName);
      statement.setInt(3, groupId);
      rs = statement.executeQuery();
      if (rs.next()) {
        isExists = true;
      }
    } catch (SQLException e) {
      System.out.println("Error while checking Student" + firstName + " "
          + lastName + " existance in group " + groupId);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return isExists;
  }

  @Override
  public boolean isIdExists(int id) {
    boolean isExists = false;
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(CHECK_STUDENT_ID_IF_EXISTS);
      statement.setInt(1, id);
      rs = statement.executeQuery();
      if (rs.next()) {
        isExists = true;
      }
    } catch (SQLException e) {
      System.out
          .println("Error while checking existance of student with ID: " + id);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return isExists;
  }

  @Override
  public boolean checkIfStudentInCourse(int studentId, String course) {
    boolean isExists = false;
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(CHECK_IF_STUDENT_IN_COURSE);
      statement.setInt(1, studentId);
      statement.setString(2, course);
      rs = statement.executeQuery();
      if (rs.next()) {
        isExists = true;
      }
    } catch (SQLException e) {
      System.out.println("Error while checking existance of student with ID: "
          + studentId + " in coure " + course);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return isExists;
  }
}
