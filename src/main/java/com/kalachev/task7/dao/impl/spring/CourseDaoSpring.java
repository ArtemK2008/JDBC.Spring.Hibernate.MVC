package com.kalachev.task7.dao.impl.spring;

import java.util.List;

import com.kalachev.task7.dao.CoursesDao;
import com.kalachev.task7.dao.entities.Course;
import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.dao.impl.spring.row_mappers.CourseRowMapper;
import com.kalachev.task7.dao.impl.spring.row_mappers.StudentRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

@Primary
@Component
public class CourseDaoSpring implements CoursesDao {
  @Autowired
  JdbcOperations jdbcOperations;

  public CourseDaoSpring(JdbcOperations jdbcOperations) {
    super();
    this.jdbcOperations = jdbcOperations;
  }

  private static final String FIND_COURSE_DESCRIPTION = "SELECT * "
      + "FROM Courses WHERE course_name = (?)";

  private static final String FIND_BY_ID = "SELECT * " + "FROM students "
      + "WHERE student_id = (?)";

  private static final String ADD_STUDENT_TO_COURSE = "INSERT INTO studentscoursesdata"
      + "(student_id,group_id,first_name,last_name,course_name,course_description) "
      + "VALUES (?,?,?,?,?,?)";

  private static final String DELETE_STUDENT_FROM_COURSE = "DELETE FROM studentscoursesdata "
      + "WHERE student_id = (?) AND course_name = (?)";

  private static final String FIND_COURSE_BY_STUDENT_ID = "SELECT course_name,course_description FROM StudentsCoursesData"
      + " WHERE student_id = (?)";

  private static final String CHECK_COURSE_IF_EXISTS = "SELECT COUNT(*) "
      + "FROM studentscoursesdata " + "WHERE course_name = (?)";

  @Override
  public boolean addStudent(int studentId, String course) {
    Student student = jdbcOperations.queryForObject(FIND_BY_ID,
        new StudentRowMapper(), studentId);
    if (student == null) {
      return false;
    }
    Course thisCourse = jdbcOperations.queryForObject(FIND_COURSE_DESCRIPTION,
        new CourseRowMapper(), course);
    if (thisCourse == null) {
      return false;
    }
    boolean isAdded = false;
    jdbcOperations.update(ADD_STUDENT_TO_COURSE, student.getId(),
        student.getGroupId(), student.getFirstName(), student.getLastName(),
        thisCourse.getCourseName(), thisCourse.getCourseDescription());
    isAdded = true;
    return isAdded;
  }

  @Override
  public boolean removeStudent(int studentId, String course) {
    boolean isDeleted = false;
    if (jdbcOperations.update(DELETE_STUDENT_FROM_COURSE, studentId,
        course) > 0) {
      isDeleted = true;
    }
    return isDeleted;
  }

  @Override
  public List<Course> getAll() {
    String query = "SELECT * FROM Courses";
    return jdbcOperations.query(query, new CourseRowMapper());
  }

  @Override
  public List<Course> getById(int studentId) {
    return jdbcOperations.query(FIND_COURSE_BY_STUDENT_ID,
        new CourseRowMapper(), studentId);
  }

  @Override
  public boolean isExists(String course) {
    Integer count = jdbcOperations.queryForObject(CHECK_COURSE_IF_EXISTS,
        Integer.class, course);
    return count != null && count > 0;
  }

}
