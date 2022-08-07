package com.kalachev.task7;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.events.InitializationEvent;
import com.kalachev.task7.initialization.Initializer;
import com.kalachev.task7.ui.menu.ConsoleMenu;
import com.kalachev.task7.utilities.ConnectionManager;
import com.kalachev.task7.utilities.JdbcUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class ConsoleMenuIT {
  @SpyBean
  ConsoleMenu spyMenu;
  @MockBean
  Scanner mockScanner;
  @SpyBean
  Initializer spyInitializer;
  @Autowired
  ApplicationEventPublisher publisher;
  private final PrintStream standardOut = System.out;
  ByteArrayOutputStream outputStreamCaptor;
  final static String NEWLINE = System.lineSeparator();
  final static String EXIT = "7";
  private static final String MAX_SIZE = "20";
  private static final String HUGE_INT = "3333";
  private static final String NEGAGTIVE_INT = "-33";
  private static final String NOT_AN_INTEGER = "its a string";
  private static final String COURSE = "Ukrainian";
  private static final String FIRSTNAME = "DummyName";
  private static final String LASTNAME = "DummyLastName";
  private static final String GROUP_ID = "1";
  private static final String STUDENT_TO_DELETE_ID = "77";
  private static final String STUDENT_ID = "77";
  private static final String ALREADY_IN_COURSE = "student already in course";
  private static final String BAD_INPUT = "Your Input was not correct";

  @BeforeEach
  public void setUp() {
    outputStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));
    doNothing().when(spyMenu).cleanConsole();
  }

  @AfterEach
  public void tearDown() {
    System.setOut(standardOut);
  }

  @Test
  void testConsoleMenu_shouldPrintGroupsWithCondition_whenUserAsksForGrouNames()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("1").thenReturn(MAX_SIZE)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    List<String> expectedGroups = retrieveGroupNamesWithCondition(MAX_SIZE);
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    String[] removedLinesBeforeGroups = removeLinesBefore(outputLines, 9);
    String[] onlyGroupNames = removeLinesAfter(removedLinesBeforeGroups, 12);
    List<String> actualGroups = Arrays.asList(onlyGroupNames);
    // then
    assertEquals(0, statusCode);
    assertTrue(outputLines.length > 21);
    assertEquals(expectedGroups.size(), actualGroups.size());
    assertTrue(actualGroups.size() == expectedGroups.size()
        && actualGroups.containsAll(expectedGroups)
        && expectedGroups.containsAll(actualGroups));
  }

  @Test
  void testConsoleMenu_shouldPrintPreparedErrorMessages_whenUserGroupIsTooBig()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("1").thenReturn(HUGE_INT)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals("no such groups", outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintPreparedErrorMessages_whenSizeIsNegative()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("1").thenReturn(NEGAGTIVE_INT)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals("Max size cant be negative", outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintPreparedErrorMessages_whenSizeIsNotInteger()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("1").thenReturn(NOT_AN_INTEGER)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals(BAD_INPUT, outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintAllStudentOfCourse_whenUserAsksForGroupStudentNames()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("2").thenReturn(COURSE)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    List<String> expectedStudents = retrieveRealStudentNames(COURSE);
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    String[] removedLinesBeforeStudents = removeLinesBefore(outputLines, 19);
    String[] onlyStudentNames = removeLinesAfter(removedLinesBeforeStudents,
        12);
    List<String> actualStudent = Arrays.asList(onlyStudentNames);
    // then
    assertEquals(0, statusCode);
    assertTrue(onlyStudentNames.length > 0);
    assertTrue(actualStudent.size() == expectedStudents.size()
        && actualStudent.containsAll(expectedStudents)
        && expectedStudents.containsAll(actualStudent));
  }

  @Test
  void testConsoleMenu_shouldPrintPreparedErrorMessage_whenWrongCourseName()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("2")
        .thenReturn(COURSE + " is wrong").thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals("no such course", outputLines[19]);
  }

  @Test
  void testConsoleMenu_shouldAddStudentToDatabase_whenUserAsksToInsert()
      throws Exception {
    // given
    String expectedMessage = "Student " + FIRSTNAME + " " + LASTNAME
        + " added to group " + GROUP_ID;
    Mockito.when(mockScanner.next()).thenReturn("3").thenReturn(FIRSTNAME)
        .thenReturn(LASTNAME).thenReturn(GROUP_ID).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int countBefore = countStudentsInDatabase();
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    String actualMessage = outputLines[11];
    int countAfter = countStudentsInDatabase();
    // then
    assertEquals(200, countBefore);
    assertEquals(0, statusCode);
    assertEquals(expectedMessage, actualMessage);
    assertEquals(201, countAfter);
    assertTrue(checkIfStudentExistsInDatabase(FIRSTNAME, LASTNAME));
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenGroupIdIsNotInt()
      throws Exception {
    // given
    publisher.publishEvent(new InitializationEvent(this));
    Mockito.when(mockScanner.next()).thenReturn("3").thenReturn(FIRSTNAME)
        .thenReturn(LASTNAME).thenReturn(NOT_AN_INTEGER).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int countBefore = countStudentsInDatabase();
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    int countAfter = countStudentsInDatabase();
    // then
    assertEquals(200, countBefore);
    assertEquals(0, statusCode);
    assertEquals(BAD_INPUT, outputLines[11]);
    assertEquals(200, countAfter);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenGroupIdIsOutOfRange()
      throws Exception {
    // given
    publisher.publishEvent(new InitializationEvent(this));
    Mockito.when(mockScanner.next()).thenReturn("3").thenReturn(FIRSTNAME)
        .thenReturn(LASTNAME).thenReturn(HUGE_INT).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int countBefore = countStudentsInDatabase();
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    int countAfter = countStudentsInDatabase();
    // then
    assertEquals(200, countBefore);
    assertEquals(0, statusCode);
    assertEquals("Wrong groupd id", outputLines[11]);
    assertEquals(200, countAfter);
  }

  @Test
  void testConsoleMenu_shouldDeleteStudentFromDatabase_whenUserAsksToDelete()
      throws Exception {
    // given
    publisher.publishEvent(new InitializationEvent(this));
    String expectedMessage = "student with id " + STUDENT_TO_DELETE_ID
        + " deleted";
    Mockito.when(mockScanner.next()).thenReturn("4")
        .thenReturn(STUDENT_TO_DELETE_ID).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int countBefore = countStudentsInDatabase();
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    String actualMessage = outputLines[9];
    int countAfter = countStudentsInDatabase();
    // then
    assertEquals(200, countBefore);
    assertEquals(0, statusCode);
    assertEquals(expectedMessage, actualMessage);
    assertEquals(199, countAfter);
    assertFalse(checkIfStudentExistsInDatabase(STUDENT_TO_DELETE_ID));
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenIdIsNotInt()
      throws Exception {
    // given
    publisher.publishEvent(new InitializationEvent(this));
    Mockito.when(mockScanner.next()).thenReturn("4").thenReturn(NOT_AN_INTEGER)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int countBefore = countStudentsInDatabase();
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    int countAfter = countStudentsInDatabase();
    // then
    assertEquals(200, countBefore);
    assertEquals(0, statusCode);
    assertEquals(BAD_INPUT, outputLines[9]);
    assertEquals(200, countAfter);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenIdNotExists()
      throws Exception {
    // given
    publisher.publishEvent(new InitializationEvent(this));
    Mockito.when(mockScanner.next()).thenReturn("4").thenReturn(HUGE_INT)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int countBefore = countStudentsInDatabase();
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    int countAfter = countStudentsInDatabase();
    // then
    assertEquals(200, countBefore);
    assertEquals(0, statusCode);
    assertEquals("no such student", outputLines[9]);
    assertEquals(200, countAfter);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenNegaiveId()
      throws Exception {
    // given
    publisher.publishEvent(new InitializationEvent(this));
    Mockito.when(mockScanner.next()).thenReturn("4").thenReturn(NEGAGTIVE_INT)
        .thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int countBefore = countStudentsInDatabase();
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    int countAfter = countStudentsInDatabase();
    // then
    assertEquals(200, countBefore);
    assertEquals(0, statusCode);
    assertEquals("Wrong student id", outputLines[9]);
    assertEquals(200, countAfter);
  }

  @Test
  void testConsoleMenu_shouldAddStudentToCourse_whenUserAsksToAddToCourse()
      throws Exception {
    // given
    String expectedMessage = "Student with id " + STUDENT_ID
        + " added to course " + COURSE;
    Mockito.when(mockScanner.next()).thenReturn("5").thenReturn(STUDENT_ID)
        .thenReturn(COURSE).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    doNothing().when(spyInitializer).initializeTablesEvent(any());
    // when
    int coursesNumberBefore = countStudentCourses(STUDENT_ID);
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    String actualMessage = outputLines[20];
    // then
    assertNotEquals(0, coursesNumberBefore);
    assertTrue(coursesNumberBefore < 4);
    assertEquals(0, statusCode);
    if (actualMessage.equals(ALREADY_IN_COURSE)) {
      expectedMessage = ALREADY_IN_COURSE;
      int coursesNumberAfter = countStudentCourses(STUDENT_ID);
      assertEquals(coursesNumberAfter, coursesNumberBefore);
    } else {
      int coursesNumberAfter = countStudentCourses(STUDENT_ID);
      assertEquals(coursesNumberAfter, coursesNumberBefore + 1);
    }
    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenCourseNameWasWrong()
      throws Exception {
    // given
    publisher.publishEvent(new InitializationEvent(this));
    Mockito.when(mockScanner.next()).thenReturn("5").thenReturn(STUDENT_ID)
        .thenReturn(COURSE + "is worng").thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    doNothing().when(spyInitializer).initializeTablesEvent(any());
    // when
    int coursesNumberBefore = countStudentCourses(STUDENT_ID);
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    int coursesNumberAfter = countStudentCourses(STUDENT_ID);
    // then
    assertNotEquals(0, coursesNumberBefore);
    assertTrue(coursesNumberBefore < 4);
    assertEquals(0, statusCode);
    assertEquals("Wrong course name", outputLines[20]);
    assertEquals(coursesNumberAfter, coursesNumberBefore);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenIdNotInt() throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("5").thenReturn(NOT_AN_INTEGER)
        .thenReturn(COURSE + "is worng").thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals(BAD_INPUT, outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenIdIsNegative()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("5").thenReturn(NEGAGTIVE_INT)
        .thenReturn(COURSE).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals("id cant be negative", outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenIdNotExist()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("5").thenReturn(HUGE_INT)
        .thenReturn(COURSE).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals("There is no student with such id", outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenIdAlreadyInCourse()
      throws Exception {
    // given
    String existingCourse = findStudentCourse(STUDENT_ID);
    Mockito.when(mockScanner.next()).thenReturn("5").thenReturn(STUDENT_ID)
        .thenReturn(existingCourse).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    doNothing().when(spyInitializer).initializeTablesEvent(any());
    // when
    int coursesNumberBefore = countStudentCourses(STUDENT_ID);
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    int coursesNumberAfter = countStudentCourses(STUDENT_ID);
    // then
    assertNotEquals(0, coursesNumberBefore);
    assertTrue(coursesNumberBefore < 4);
    assertEquals(0, statusCode);
    assertEquals("student already in course", outputLines[20]);
    assertEquals(coursesNumberAfter, coursesNumberBefore);
  }

  @Test
  void testConsoleMenu_shouldRemoveStudentFromCourse_whenUserAsksToDelete()
      throws Exception {
    // given
    String course = findStudentCourse(STUDENT_ID);
    String expectedMessage = "Student with id " + STUDENT_ID + " removed from "
        + course;
    Mockito.when(mockScanner.next()).thenReturn("6").thenReturn(STUDENT_ID)
        .thenReturn(course).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    doNothing().when(spyInitializer).initializeTablesEvent(any());
    // when
    int coursesNumberBefore = countStudentCourses(STUDENT_ID);
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    String actualMessage = outputLines[10 + coursesNumberBefore];
    int coursesNumberAfter = countStudentCourses(STUDENT_ID);
    // then
    assertNotEquals(0, coursesNumberBefore);
    assertTrue(coursesNumberBefore < 4);
    assertEquals(0, statusCode);
    assertEquals(expectedMessage, actualMessage);
    assertEquals(coursesNumberAfter, coursesNumberBefore - 1);
  }

  @Test
  void testConsoleMenu_shouldPrintErrorMessage_whenCourseNotExist()
      throws Exception {
    // given
    String expectedMessage = "Wrong course name";
    Mockito.when(mockScanner.next()).thenReturn("6").thenReturn(STUDENT_ID)
        .thenReturn(COURSE + "is wrong").thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    doNothing().when(spyInitializer).initializeTablesEvent(any());
    // when
    int coursesNumberBefore = countStudentCourses(STUDENT_ID);
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    String actualMessage = outputLines[10 + coursesNumberBefore];
    int coursesNumberAfter = countStudentCourses(STUDENT_ID);
    // then
    assertNotEquals(0, coursesNumberBefore);
    assertTrue(coursesNumberBefore < 4);
    assertEquals(0, statusCode);
    assertEquals(expectedMessage, actualMessage);
    assertEquals(coursesNumberAfter, coursesNumberBefore);
  }

  @Test
  void testConsoleMenu_shouldPrintDeleteErrorMessage_whenIdNotInt()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("6").thenReturn(NOT_AN_INTEGER)
        .thenReturn(COURSE).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals(BAD_INPUT, outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintDeleteErrorMessage_whenIdIsNegative()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("6").thenReturn(NEGAGTIVE_INT)
        .thenReturn(COURSE).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals("Wrong id", outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldPrintDeleteErrorMessage_whenIdNotExist()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn("6").thenReturn(HUGE_INT)
        .thenReturn(COURSE).thenReturn(EXIT);
    Mockito.when(mockScanner.nextLine()).thenReturn("enter");
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    String output = outputStreamCaptor.toString().trim();
    String[] outputLines = output.split(NEWLINE);
    // then
    assertEquals(0, statusCode);
    assertEquals("There is no student with such id", outputLines[9]);
  }

  @Test
  void testConsoleMenu_shouldTermitateApp_whenUserAsksForExit()
      throws Exception {
    // given
    Mockito.when(mockScanner.next()).thenReturn(EXIT);
    // when
    int statusCode = catchSystemExit(() -> {
      spyMenu.runSchoolApp();
    });
    // then
    assertEquals(0, statusCode);
  }

  private String[] removeLinesBefore(String[] data, int amount) {
    String[] removedLinesBefore = new String[data.length - amount];
    for (int i = amount; i < data.length; i++) {
      removedLinesBefore[i - amount] = data[i];
    }
    return removedLinesBefore;
  }

  private String[] removeLinesAfter(String[] data, int amount) {
    String[] removedLinesAfter = Arrays.copyOf(data, data.length - amount);
    return removedLinesAfter;
  }

  private List<String> retrieveRealStudentNames(String course) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    List<String> students = new ArrayList<>();
    final String FIND_STUDENTS = "SELECT first_name,last_name "
        + "FROM studentscoursesdata " + "WHERE course_name = (?)";
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(FIND_STUDENTS);
      statement.setString(1, course);
      rs = statement.executeQuery();
      while (rs.next()) {
        students
            .add(rs.getString("first_name") + " " + rs.getString("last_name"));
      }
    } catch (SQLException e) {
      System.out.println("Error while getting Student names from " + course);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return students;
  }

  private List<String> retrieveGroupNamesWithCondition(String count) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    List<String> groups = new LinkedList<>();
    final String FIND_GROUP_BY_SIZE = "SELECT g.group_name FROM Students"
        + " as s " + "INNER JOIN Groups as g " + "ON s.group_id = g.group_id "
        + "GROUP BY g.group_id,group_name "
        + "HAVING COUNT (s.group_id) >= (?)";
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(FIND_GROUP_BY_SIZE);
      statement.setInt(1, Integer.parseInt(count));
      rs = statement.executeQuery();
      while (rs.next()) {
        groups.add(rs.getString("group_name"));
      }
    } catch (SQLException e) {
      System.out.println("Error while getting Group names with less then "
          + count + " students");
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return groups;
  }

  private int countStudentsInDatabase() {
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    int count = 0;
    final String COUNT_STUDENTS = "SELECT COUNT(*) FROM STUDENTS";
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.createStatement();
      rs = statement.executeQuery(COUNT_STUDENTS);
      if (rs.next()) {
        count = rs.getInt(1);
      }
    } catch (SQLException e) {
      System.out.println("Error while counting Students");
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return count;
  }

  private boolean checkIfStudentExistsInDatabase(String name, String lastname) {
    boolean isExist = false;
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    int count = 0;
    final String COUNT_STUDENTS = "SELECT COUNT(*) FROM STUDENTS WHERE first_name = (?) AND last_name = (?)";
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(COUNT_STUDENTS);
      statement.setString(1, name);
      statement.setString(2, lastname);
      rs = statement.executeQuery();
      if (rs.next()) {
        count = rs.getInt(1);
      }
      if (count != 0) {
        isExist = true;
      }
    } catch (SQLException e) {
      System.out.println(
          "Error while checking existance of Student " + name + " " + lastname);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return isExist;
  }

  private boolean checkIfStudentExistsInDatabase(String studentId) {
    boolean isExist = false;
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    int count = 0;
    final String COUNT_STUDENTS = "SELECT COUNT(*) FROM STUDENTS WHERE student_id = (?)";
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(COUNT_STUDENTS);
      statement.setInt(1, Integer.parseInt(studentId));
      rs = statement.executeQuery();
      if (rs.next()) {
        count = rs.getInt(1);
      }
      if (count != 0) {
        isExist = true;
      }
    } catch (SQLException e) {
      System.out.println(
          "Error while checking existance of Student with ID: " + studentId);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return isExist;
  }

  private int countStudentCourses(String studentId) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    int count = 0;
    final String COUNT_STUDENTS = "SELECT COUNT(*) FROM studentscoursesdata WHERE student_id = (?)";
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(COUNT_STUDENTS);
      statement.setInt(1, Integer.parseInt(studentId));
      rs = statement.executeQuery();
      if (rs.next()) {
        count = rs.getInt(1);
      }
    } catch (SQLException e) {
      System.out.println(
          "Error while counting courses for Student with ID: " + studentId);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return count;
  }

  private String findStudentCourse(String studentId) {
    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet rs = null;
    String course = "";
    final String FIND_COURSE = "SELECT course_name FROM studentscoursesdata WHERE student_id = (?)";
    try {
      connection = ConnectionManager.openDbConnection();
      statement = connection.prepareStatement(FIND_COURSE);
      statement.setInt(1, Integer.parseInt(studentId));
      rs = statement.executeQuery();
      if (rs.next()) {
        course = rs.getString(1);
      }
    } catch (SQLException e) {
      System.out.println(
          "Error while finding any course of Student with id " + studentId);
    } finally {
      JdbcUtil.closeAll(rs, statement, connection);
    }
    return course;
  }
}
