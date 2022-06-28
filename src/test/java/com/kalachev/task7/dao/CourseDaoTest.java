package com.kalachev.task7.dao;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.dao.entities.Course;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class CourseDaoTest extends DbUnitConfig {
  @Autowired
  CoursesDao coursesDao;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();

    beforeData = new FlatXmlDataSetBuilder()
        .build(new FileInputStream(getClass().getClassLoader()
            .getResource("dao/course/ActualCourseDataSet.xml").getFile()));
    databaseTester.setDataSet(beforeData);
    databaseTester.onSetup();
  }

  @Test
  void testAddStudent_shouldAddStudentToCourse_whenStudentWasNotInThisCourse()
      throws SQLException, Exception {
    // given
    IDataSet expectedData = new FlatXmlDataSetBuilder()
        .build(new FileInputStream(getClass().getClassLoader()
            .getResource("dao/course/ExpectedInsertCourseDataSet.xml")
            .getFile()));
    // when
    coursesDao.addStudent(5, "Arabic");
    // then
    ITable expectedTable = expectedData.getTable("studentscoursesdata");
    IDataSet actualData = databaseTester.getConnection().createDataSet();
    ITable actualTable = actualData.getTable("studentscoursesdata");
    Assertion.assertEquals(expectedTable, actualTable);
  }

  @Test
  void testRemoveStudent_shouldDeleteStudentFromCourse_whenStudentWasInCourse()
      throws SQLException, Exception {
    // given
    IDataSet expectedData = new FlatXmlDataSetBuilder()
        .build(new FileInputStream(getClass().getClassLoader()
            .getResource("dao/course/ExpectedDeleteCourseDataSet.xml")
            .getFile()));
    ITable expectedTable = expectedData.getTable("studentscoursesdata");
    // when
    coursesDao.removeStudent(1, "Russian");
    IDataSet actualData = databaseTester.getConnection().createDataSet();
    ITable actualTable = actualData.getTable("studentscoursesdata");
    // then
    Assertion.assertEquals(expectedTable, actualTable);
  }

  @Test
  void testGetAll_shouldReturnAllCoursesinList_whenCalledWithValidData() {
    // given
    List<Course> expected = new ArrayList<>();
    Course course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("Russian");
    expected.add(course);
    course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("Ukrainian");
    expected.add(course);
    course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("Mandarin");
    expected.add(course);
    course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("English");
    expected.add(course);
    course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("Arabic");
    expected.add(course);
    // when
    List<Course> actual = coursesDao.getAll();
    // then
    assertEquals(expected, actual);
  }

  @Test
  void testGetById_shouldReturnAllCoursesOfChosenStudent_WhenCalledOnExistingStudent() {
    // given
    List<Course> expected = new ArrayList<>();
    Course course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("Mandarin");
    expected.add(course);
    course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("Arabic");
    expected.add(course);
    course = new Course();
    course.setCourseDescription("-");
    course.setCourseName("English");
    expected.add(course);
    // when
    List<Course> actual = coursesDao.getById(10);
    // then
    assertEquals(expected, actual);
  }

  @Test
  void testCheckCourseIfExist_shouldReturnTrue_whenCourseExists() {
    // when
    boolean check = coursesDao.isExists("Ukrainian");
    // then
    assertTrue(check);
  }

  @Test
  void testCheckCourseIfExist_shouldReturnFalse_whenCourseNotExists() {
    // when
    boolean check = coursesDao.isExists("Hindi");
    // then
    assertFalse(check);
  }
}
