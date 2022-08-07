package com.kalachev.task7.dao.impl.spring;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kalachev.task7.dao.StudentsDao;
import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.dao.impl.spring.row_mappers.StudentRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

@Component
public class StudentDaoSpring implements StudentsDao {
  @Autowired
  JdbcOperations jdbcOperations;

  public StudentDaoSpring(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  private static final String FIND_STUDENTS = "SELECT student_id, group_id,first_name,last_name "
      + "FROM studentscoursesdata " + "WHERE course_name = (?)";

  private static final String INSERT_STUDENT = "INSERT INTO Students(first_name,last_name,group_id)"
      + " VALUES (?,?,?)";

  private static final String DELETE_STUDENT = "DELETE FROM Students Where student_id = (?)";

  private static final String CHECK_STUDENT_IF_EXISTS_IN_GROUP = "SELECT COUNT(*) FROM Students "
      + "WHERE first_name = (?) AND last_name = (?) AND group_id = (?)";

  private static final String CHECK_STUDENT_ID_IF_EXISTS = "SELECT COUNT(*) FROM Students "
      + "WHERE student_id = (?)";

  private static final String CHECK_IF_STUDENT_IN_COURSE = "SELECT COUNT(*) "
      + "FROM studentscoursesdata " + "WHERE student_id ="
      + " (?) AND course_name = (?)";

  @Override
  public Map<String, String> studentNamesById() {
    String query = "SELECT student_id,first_name,last_name  FROM Students";
    Map<String, String> idOfEachStudent = new HashMap<>();
    jdbcOperations.query(query, (ResultSet rs) -> {
      idOfEachStudent.put(rs.getString("student_id"),
          rs.getString("first_name") + " " + rs.getString("last_name"));
    });
    return idOfEachStudent;
  }

  @Override
  public List<Student> findByCourse(String courseName) {
    return jdbcOperations.query(FIND_STUDENTS, new StudentRowMapper(),
        courseName);
  }

  @Override
  public boolean insert(String firstName, String lastName, int groupId) {
    boolean isAdded = false;
    if (jdbcOperations.update(INSERT_STUDENT, firstName, lastName,
        groupId) > 0) {
      isAdded = true;
    }
    return isAdded;
  }

  @Override
  public boolean delete(int id) {
    boolean isDeleted = false;
    if (jdbcOperations.update(DELETE_STUDENT, id) > 0) {
      isDeleted = true;
    }
    return isDeleted;
  }

  @Override
  public boolean isExistsInGroup(String firstName, String lastName,
      int groupId) {
    Integer count = jdbcOperations.queryForObject(
        CHECK_STUDENT_IF_EXISTS_IN_GROUP, Integer.class, firstName, lastName,
        groupId);
    return count != null && count > 0;
  }

  @Override
  public boolean isIdExists(int id) {
    Integer count = jdbcOperations.queryForObject(CHECK_STUDENT_ID_IF_EXISTS,
        Integer.class, id);
    return count != null && count > 0;
  }

  @Override
  public boolean checkIfStudentInCourse(int studentId, String course) {
    Integer count = jdbcOperations.queryForObject(CHECK_IF_STUDENT_IN_COURSE,
        Integer.class, studentId, course);
    return count != null && count > 0;
  }
}
