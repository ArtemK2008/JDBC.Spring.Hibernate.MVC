package com.kalachev.task7.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.dao.entities.Student;

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
class StudentDaoTest {

  @Autowired
  StudentsDao studentsDao;

  @Test
  @DatabaseSetup("/dao/student/ActualStudentDataSetHibernate.xml")
  @ExpectedDatabase(assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, value = "/dao/student/ExpectedInsertStudentDataSetHibernate.xml")
  void testDaoImpl_whenInsert_thenNewStudentShouldBeAdded()
      throws SQLException, Exception {
    studentsDao.insert("artem", "artemov", 6);
  }

  @Test
  @DatabaseSetup("/dao/student/ExpectedInsertStudentDataSetHibernate.xml")
  @ExpectedDatabase(assertionMode = DatabaseAssertionMode.NON_STRICT_UNORDERED, value = "/dao/student/ActualInsertStudentDataSetHibernate.xml")
  void testDaoImpl_whenDelete_thenStudentNoLongerInTable()
      throws SQLException, Exception {
    studentsDao.delete(1);
  }

  @Test
  void testStudentNamesById_shouldMapStudentsToIds_whenStudentTableExists() {
    // given
    Map<String, String> expected = new LinkedHashMap<>();
    expected.put("1", "tom tomov");
    expected.put("2", "ivan ivanov");
    expected.put("3", "petr petrov");
    expected.put("4", "sidor sidorov");
    expected.put("5", "aleksandr aleksandrov");
    expected.put("6", "pavel pavlov");
    expected.put("7", "sveta svetova");
    expected.put("8", "liza lizova");
    expected.put("9", "marina marinova");
    expected.put("10", "lena lenova");
    // when
    Map<String, String> actual = studentsDao.studentNamesById();
    // then
    assertEquals(expected, actual);
  }

  @Test
  void testFindByCourse_shouldReturnAllStudentsOfChousenCourse_whenTableExists() {
    // given
    List<Student> expected = new ArrayList<>();
    Student marina = new Student();
    marina.setFirstName("marina");
    marina.setLastName("marinova");
    marina.setGroupId(4);
    marina.setId(9);
    Student lena = new Student();
    lena.setFirstName("lena");
    lena.setLastName("lenova");
    lena.setGroupId(5);
    lena.setId(10);
    expected.add(marina);
    expected.add(lena);
    // when
    List<Student> actual = studentsDao.findByCourse("Mandarin");
    // then
    assertEquals(expected, actual);
  }

  @Test
  void testCheckStudntIfExistsInGroup_shouldReturnTrue_whenStudentIsInGroup() {
    // when
    boolean check = studentsDao.isExistsInGroup("tom", "tomov", 1);
    // then
    assertTrue(check);
  }

  @Test
  void testCheckStudntIfExistsInGroup_shouldReturnFalse_whenStudentIsNotInGroup() {
    // when
    boolean check = studentsDao.isExistsInGroup("tom", "tomov", 2);
    // then
    assertFalse(check);
  }

  @Test
  void testcheckStudentIdIfExists_shouldReturnTrue_whenIdExists() {
    // when
    boolean check = studentsDao.isIdExists(2);
    // then
    assertTrue(check);
  }

  @Test
  void testcheckStudentIdIfExists_shouldReturnFalse_whenIdNotExists() {
    // when
    boolean check = studentsDao.isIdExists(15);
    // then
    assertFalse(check);
  }

  @Test
  void testcheckIfStudentInCourse_shouldReturnTrue_whenStudentInCourse() {
    // when
    boolean check = studentsDao.checkIfStudentInCourse(1, "Russian");
    // then
    assertTrue(check);
  }

  @Test
  void testcheckIfStudentInCourse_shouldReturnFalse_whenStudentNotInCourse() {
    // when
    boolean check = studentsDao.checkIfStudentInCourse(1, "Hindi");
    // then
    assertFalse(check);
  }
}
