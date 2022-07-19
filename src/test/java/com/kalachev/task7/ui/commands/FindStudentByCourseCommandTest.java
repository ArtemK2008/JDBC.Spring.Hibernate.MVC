package com.kalachev.task7.ui.commands;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.kalachev.task7.service.StudentOptions;
import com.kalachev.task7.ui.commands.impl.FindStudentsByCourseCommand;

class FindStudentByCourseCommandTest {

  Command command;
  final static String NEWLINE = System.lineSeparator();
  Scanner mockScanner;
  CoursesOptions mockCourseOptions;
  StudentOptions mockStudentOptions;
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
    mockCourseOptions = Mockito.mock(CoursesOptions.class);
    mockStudentOptions = Mockito.mock(StudentOptions.class);
  }

  @Test
  void testFindNames_shouldCallAllNeedeMethods_whenValidInput() {
    // given
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    List<String> students = Arrays.asList("a", "b", "c");
    Mockito.when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(course);
    Mockito.when(mockStudentOptions.findByCourse(course)).thenReturn(students);
    command = new FindStudentsByCourseCommand(mockScanner, mockCourseOptions,
        mockStudentOptions);
    // when
    command.execute();
    // then
    verify(mockCourseOptions, times(1)).findCourseNames();
    verify(mockStudentOptions, times(1)).findByCourse(course);
  }

  @Test
  void testFindNames_shouldPrintAllCourses_whenValidInput() throws Exception {
    // given
    String expected = "Eng" + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Choose a course to see its students" + NEWLINE + "a" + NEWLINE + "b"
        + NEWLINE + "c";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    List<String> students = Arrays.asList("a", "b", "c");
    Mockito.when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(course);
    Mockito.when(mockStudentOptions.findByCourse(course)).thenReturn(students);
    command = new FindStudentsByCourseCommand(mockScanner, mockCourseOptions,
        mockStudentOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testFindNames_shouldPrintThatWasNotCorrect_whenInputIsNotACourseFromList()
      throws Exception {
    // given
    String expected = "Eng" + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Choose a course to see its students" + NEWLINE + "no such course";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn("not a course");
    command = new FindStudentsByCourseCommand(mockScanner, mockCourseOptions,
        mockStudentOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testFindNames_shouldPrintNoCourseFound_whenInputCourseTableCorrupted()
      throws Exception {
    // given
    String expected = "No Courses Found";
    List<String> emptyList = new ArrayList<>();
    List<String> students = Arrays.asList("a", "b", "c");
    Mockito.when(mockCourseOptions.findCourseNames()).thenReturn(emptyList);
    Mockito.when(mockScanner.next()).thenReturn(course);
    Mockito.when(mockStudentOptions.findByCourse(course)).thenReturn(students);
    command = new FindStudentsByCourseCommand(mockScanner, mockCourseOptions,
        mockStudentOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testFindNames_shouldPrintNoStudents_whenEmptyCourse() throws Exception {
    // given
    String expected = "Eng" + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Choose a course to see its students" + NEWLINE
        + "No students in this course";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    List<String> students = new ArrayList<>();
    Mockito.when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(course);
    Mockito.when(mockStudentOptions.findByCourse(course)).thenReturn(students);
    command = new FindStudentsByCourseCommand(mockScanner, mockCourseOptions,
        mockStudentOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }
}
