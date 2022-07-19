package com.kalachev.task7.ui.commands;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.kalachev.task7.initialization.CoursesInitializer;
import com.kalachev.task7.initialization.GroupInitializer;
import com.kalachev.task7.initialization.SchemaInitializer;
import com.kalachev.task7.initialization.StudentInitializer;
import com.kalachev.task7.initialization.impl.CoursesInitializerImpl;
import com.kalachev.task7.initialization.impl.GroupInitializerImpl;
import com.kalachev.task7.initialization.impl.InitializerImpl;
import com.kalachev.task7.initialization.impl.SchemaInitializerImpl;
import com.kalachev.task7.initialization.impl.StudentInitializerImpl;
import com.kalachev.task7.service.CoursesOptions;
import com.kalachev.task7.ui.commands.impl.AddToCourseCommand;

class AddToCourseCommandTest {

  Command command;
  final static String NEWLINE = System.lineSeparator();
  Scanner mockScanner;
  CoursesOptions mockOptions;
  String id = "1";
  String course = "Eng";
  static StudentInitializer studentInitializer = new StudentInitializerImpl();
  static GroupInitializer groupInitializer = new GroupInitializerImpl();
  static CoursesInitializer coursesInitializer = new CoursesInitializerImpl();
  static SchemaInitializer schemaInitializer = new SchemaInitializerImpl();
  static InitializerImpl initializer = new InitializerImpl(
      studentInitializer, coursesInitializer, groupInitializer,
      schemaInitializer);

  @BeforeAll
  static void startUp() {
    initializer.initializeTables();
  }

  @BeforeEach
  void setUp() {
    mockScanner = Mockito.mock(Scanner.class);
    mockOptions = Mockito.mock(CoursesOptions.class);
  }

  @Test
  void testAddToCourse_shouldCallAllNeedeMethods_whenValidInput() {
    // given
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(id).thenReturn(course);
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    Mockito
        .when(mockOptions.checkIfStudentAlreadyInCourse(anyInt(), anyString()))
        .thenReturn(false);
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    command.execute();
    // then
    verify(mockOptions, times(1)).addStudentToCourse(Integer.parseInt(id),
        course);
  }

  @Test
  void testAddToCourse_shouldPrintAdded_whenValidInput() throws Exception {
    // given
    String expected = "Enter ID of a student you want to add" + NEWLINE + "Eng"
        + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Enter a name of a course from the list" + NEWLINE
        + "Student with id 1 added to course Eng";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(id).thenReturn(course);
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    Mockito
        .when(mockOptions.checkIfStudentAlreadyInCourse(anyInt(), anyString()))
        .thenReturn(false);
    Mockito.when(mockOptions.addStudentToCourse(Integer.parseInt(id), course))
        .thenReturn(true);
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testAddToCourse_shouldprintWrongName_whenNameWasNotInCourses()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to add" + NEWLINE + "Eng"
        + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Enter a name of a course from the list" + NEWLINE
        + "Wrong course name";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(id).thenReturn("wrong course");
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testAddToCourse_shouldprintWrongInput_whenIdWasNotInt()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to add" + NEWLINE
        + "Your Input was not correct";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn("not an int id");
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testAddToCourse_shouldPrintWrongId_whenNegativeId() throws Exception {
    // given
    String expected = "Enter ID of a student you want to add" + NEWLINE
        + "id cant be negative";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn("-322");
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testAddToCourse_shouldPrintStudentNotExist_whenIdIsNotInTable()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to add" + NEWLINE
        + "There is no student with such id";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn("5555");
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt()))
        .thenReturn(false);
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testAddToCourse_shouldPrintAlreadyInCourse_whenStudentInCourse()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to add" + NEWLINE + "Eng"
        + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Enter a name of a course from the list" + NEWLINE
        + "student already in course";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(id).thenReturn(course);
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    Mockito
        .when(mockOptions.checkIfStudentAlreadyInCourse(anyInt(), anyString()))
        .thenReturn(true);
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testAddToCourse_shouldPrintNoCourses_whenCoursesCorrupted()
      throws Exception {
    // given
    String expected = "No Courses Found";
    List<String> emptyList = new ArrayList<>();
    Mockito.when(mockOptions.findCourseNames()).thenReturn(emptyList);
    command = new AddToCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }
}
