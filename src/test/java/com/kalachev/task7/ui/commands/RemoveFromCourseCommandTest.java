package com.kalachev.task7.ui.commands;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.kalachev.task7.ui.commands.impl.RemoveFromCourseCommand;

class RemoveFromCourseCommandTest {

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
  void testRemoveFromCourse_shouldCallAllNeedeMethods_whenValidInput() {
    // given
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNamesByID(Integer.parseInt(id)))
        .thenReturn(courses);
    Mockito
        .when(mockOptions.removeStudentFromCourse(Integer.parseInt(id), course))
        .thenReturn(true);
    Mockito.when(mockScanner.next()).thenReturn(id).thenReturn(course);
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    Mockito
        .when(mockOptions.checkIfStudentAlreadyInCourse(anyInt(), anyString()))
        .thenReturn(true);
    command = new RemoveFromCourseCommand(mockScanner, mockOptions);
    // when
    command.execute();
    // then
    verify(mockOptions, times(1)).removeStudentFromCourse(Integer.parseInt(id),
        course);
    verify(mockOptions, times(1)).findCourseNamesByID(Integer.parseInt(id));
  }

  @Test
  void testRemoveFromCourse_shouldPrintRemoved_whenValidInput()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to delete" + NEWLINE
        + "Eng" + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Enter a name of a course from the list" + NEWLINE
        + "Student with id 1 removed from Eng";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNamesByID(Integer.parseInt(id)))
        .thenReturn(courses);
    Mockito
        .when(mockOptions.removeStudentFromCourse(Integer.parseInt(id), course))
        .thenReturn(true);
    Mockito.when(mockScanner.next()).thenReturn(id).thenReturn(course);
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    Mockito
        .when(mockOptions.checkIfStudentAlreadyInCourse(anyInt(), anyString()))
        .thenReturn(true);
    command = new RemoveFromCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testRemoveFromCourse_shouldPrintWrongName_whenCourseNotInTable()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to delete" + NEWLINE
        + "Eng" + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Enter a name of a course from the list" + NEWLINE
        + "Wrong course name";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNamesByID(Integer.parseInt(id)))
        .thenReturn(courses);
    Mockito.when(mockScanner.next()).thenReturn(id)
        .thenReturn("wrong course name");
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    command = new RemoveFromCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testRemoveFromCourse_shouldPrintBadInput_whenIdIsNotInt()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to delete" + NEWLINE
        + "Your Input was not correct";
    Mockito.when(mockScanner.next()).thenReturn("this is not an int");
    command = new RemoveFromCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testRemoveFromCourse_shouldPrintWrongId_whenNegativeId()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to delete" + NEWLINE
        + "Wrong id";
    Mockito.when(mockScanner.next()).thenReturn("-3222");
    command = new RemoveFromCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testRemoveFromCourse_shouldPrintNoStudent_whenNoStudentId()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to delete" + NEWLINE
        + "There is no student with such id";
    Mockito.when(mockScanner.next()).thenReturn(id);
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt()))
        .thenReturn(false);
    command = new RemoveFromCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }

  @Test
  void testRemoveFromCourse_shouldPrintNotInCourse_whenNoStudentInCourse()
      throws Exception {
    // given
    String expected = "Enter ID of a student you want to delete" + NEWLINE
        + "Eng" + NEWLINE + "Rus" + NEWLINE + "Uk" + NEWLINE
        + "Enter a name of a course from the list" + NEWLINE
        + "no such student in this course";
    List<String> courses = Arrays.asList("Eng", "Rus", "Uk");
    Mockito.when(mockOptions.findCourseNamesByID(Integer.parseInt(id)))
        .thenReturn(courses);
    Mockito
        .when(mockOptions.removeStudentFromCourse(Integer.parseInt(id), course))
        .thenReturn(true);
    Mockito.when(mockScanner.next()).thenReturn(id).thenReturn(course);
    Mockito.when(mockOptions.checkIfStudentIdExists(anyInt())).thenReturn(true);
    Mockito
        .when(mockOptions.checkIfStudentAlreadyInCourse(anyInt(), anyString()))
        .thenReturn(false);
    command = new RemoveFromCourseCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expected, actual.trim());
  }
}
