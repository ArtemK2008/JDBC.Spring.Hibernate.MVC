package com.kalachev.task7.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.dao.entities.Course;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
class CourseDaoTest {

  @Autowired
  CoursesDao coursesDao;

  @Test
  @DatabaseSetup("/dao/course/ActualCourseDataSetHibernate.xml")
  @ExpectedDatabase(assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, value = "/dao/course/ExpectedInsertCourseDataSetHibernate.xml")
  void testAddStudent_shouldAddStudentToCourse_whenStudentWasNotInThisCourse()
      throws SQLException, Exception {
    coursesDao.addStudent(5, "Arabic");
  }

  @Test
  @DatabaseSetup("/dao/course/ActualCourseDataSetHibernate.xml")
  @ExpectedDatabase(assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, value = "/dao/course/ExpectedDeleteCourseDataSetHibernate.xml")
  void testRemoveStudent_shouldDeleteStudentFromCourse_whenStudentWasInCourse()
      throws SQLException, Exception {
    coursesDao.removeStudent(1, "Russian");
  }

  @Test
  @DatabaseSetup("/dao/course/ActualCourseDataSetHibernate.xml")
  void testGetAll_shouldReturnAllCoursesinList_whenCalledWithValidData() {
    // given
    List<Course> expected = new ArrayList<>();
    Course course = new Course();
    course.setId(1);
    course.setCourseDescription("-");
    course.setCourseName("Russian");
    expected.add(course);
    course = new Course();
    course.setId(2);
    course.setCourseDescription("-");
    course.setCourseName("Ukrainian");
    expected.add(course);
    course = new Course();
    course.setId(3);
    course.setCourseDescription("-");
    course.setCourseName("Mandarin");
    expected.add(course);
    course = new Course();
    course.setId(4);
    course.setCourseDescription("-");
    course.setCourseName("English");
    expected.add(course);
    course = new Course();
    course.setId(5);
    course.setCourseDescription("-");
    course.setCourseName("Arabic");
    expected.add(course);
    // when
    List<Course> actual = coursesDao.getAll();
    // then
    assertEquals(expected, actual);
  }

  @Test
  @DatabaseSetup("/dao/course/ActualCourseDataSetHibernate.xml")
  void testGetById_shouldReturnAllCoursesOfChosenStudent_WhenCalledOnExistingStudent() {
    // given
    List<Course> expected = new ArrayList<>();
    Course course = new Course();
    course.setId(3);
    course.setCourseDescription("-");
    course.setCourseName("Mandarin");
    expected.add(course);
    course = new Course();
    course.setId(5);
    course.setCourseDescription("-");
    course.setCourseName("Arabic");
    expected.add(course);
    course = new Course();
    course.setId(4);
    course.setCourseDescription("-");
    course.setCourseName("English");
    expected.add(course);
    // when
    List<Course> actual = coursesDao.getById(10);
    // then
    assertTrue(actual.size() == expected.size() && actual.containsAll(expected)
        && expected.containsAll(actual));
  }

  @Test
  @DatabaseSetup("/dao/course/ActualCourseDataSetHibernate.xml")
  void testCheckCourseIfExist_shouldReturnTrue_whenCourseExists() {
    // when
    boolean check = coursesDao.isExists("Ukrainian");
    // then
    assertTrue(check);
  }

  @Test
  @DatabaseSetup("/dao/course/ActualCourseDataSetHibernate.xml")
  void testCheckCourseIfExist_shouldReturnFalse_whenCourseNotExists() {
    // when
    boolean check = coursesDao.isExists("Hindi");
    // then
    assertFalse(check);
  }
}
