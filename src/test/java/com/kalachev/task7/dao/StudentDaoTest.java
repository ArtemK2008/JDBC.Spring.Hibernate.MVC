package com.kalachev.task7.dao;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.kalachev.task7.dao.entities.Student;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class StudentDaoTest extends DbUnitConfig {
  @Autowired
  StudentsDao studentsDao;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    beforeData = new FlatXmlDataSetBuilder()
        .build(new FileInputStream(getClass().getClassLoader()
            .getResource("dao/student/ActualStudentDataSet.xml").getFile()));
    databaseTester.setDataSet(beforeData);
    databaseTester.onSetup();
  }

  @Test
  void testDataSetEmptySchema_shouldHaveEqualTables_thenDatasetCreated()
      throws Exception {
    // given
    IDataSet expectedDataSet = getDataSet();
    ITable expectedTable = expectedDataSet.getTable("students");
    // when
    IDataSet databaseDataSet = getConnection().createDataSet();
    ITable actualTable = databaseDataSet.getTable("students");
    // then
    Assertion.assertEquals(expectedTable, actualTable);
  }

  @Test
  void testDaoImpl_whenInsert_thenNewStudentShouldBeAdded()
      throws SQLException, Exception {
    // given
    IDataSet expectedData = new FlatXmlDataSetBuilder()
        .build(new FileInputStream(getClass().getClassLoader()
            .getResource("dao/student/ExpectedInsertStudentDataSet.xml")
            .getFile()));
    ITable expectedTable = expectedData.getTable("students");
    // when
    studentsDao.insert("artem", "artemov", 6);
    IDataSet actualData = databaseTester.getConnection().createDataSet();
    ITable actualTable = actualData.getTable("students");
    String[] excludedColumns = { "student_id" };
    // then
    Assertion.assertEqualsIgnoreCols(expectedTable, actualTable,
        excludedColumns);
  }

  @Test
  void testDaoImpl_whenDelete_thenStudentNoLongerInTable()
      throws SQLException, Exception {
    // given
    IDataSet expectedData = new FlatXmlDataSetBuilder()
        .build(new FileInputStream(getClass().getClassLoader()
            .getResource("dao/student/ExpectedDeleteStudentDataSet.xml")
            .getFile()));
    ITable expectedTable = expectedData.getTable("students");
    // when
    studentsDao.delete(10);
    IDataSet actualData = databaseTester.getConnection().createDataSet();
    ITable actualTable = actualData.getTable("students");
    // then
    Assertion.assertEquals(expectedTable, actualTable);
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
    marina.setGroupdId(4);
    marina.setId(9);
    Student lena = new Student();
    lena.setFirstName("lena");
    lena.setLastName("lenova");
    lena.setGroupdId(5);
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
