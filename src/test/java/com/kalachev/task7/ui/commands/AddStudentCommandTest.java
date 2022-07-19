package com.kalachev.task7.ui.commands;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.kalachev.task7.service.StudentOptions;
import com.kalachev.task7.ui.commands.impl.AddStudentCommand;

class AddStudentCommandTest {
  Command command;
  final static String NEWLINE = System.lineSeparator();
  String name = "b";
  String lastname = "b";
  String group = "1";
  Scanner mockScanner;
  StudentOptions mockOptions;
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
    mockOptions = Mockito.mock(StudentOptions.class);
    mockScanner = Mockito.mock(Scanner.class);
  }

  @Test
  void testAddStudent_shouldCallAllNeedeMethods_whenGivenValidInputs() {
    // given
    Mockito.when(mockScanner.next()).thenReturn(name).thenReturn(lastname)
        .thenReturn(group);
    Mockito
        .when(mockOptions.addNewStudent(name, lastname, Integer.valueOf(group)))
        .thenReturn(true);
    Mockito.when(mockOptions.checkIfStudentAlreadyInGroup(anyInt(), anyString(),
        anyString())).thenReturn(false);
    command = new AddStudentCommand(mockScanner, mockOptions);

    // when
    command.execute();
    // then
    verify(mockOptions, times(1)).addNewStudent(name, lastname,
        Integer.valueOf(group));
    verify(mockOptions, times(1))
        .checkIfStudentAlreadyInGroup(Integer.parseInt(group), name, lastname);
  }

  @Test
  void testAddStudent_shouldPrintThatStudentAdded_whenAllWasValid()
      throws Exception {
    // given
    String expexted = "Enter student name" + NEWLINE + "Enter student last name"
        + NEWLINE + "Enter group id to add this student" + NEWLINE
        + "Student b b added to group 1";
    Mockito.when(mockScanner.next()).thenReturn(name).thenReturn(lastname)
        .thenReturn(group);
    Mockito
        .when(mockOptions.addNewStudent(name, lastname, Integer.valueOf(group)))
        .thenReturn(true);
    Mockito.when(mockOptions.checkIfStudentAlreadyInGroup(anyInt(), anyString(),
        anyString())).thenReturn(false);
    command = new AddStudentCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expexted, actual.trim());
  }

  @Test
  void testAddStudent_shouldPrintInputWasNotCorrect_whenIdWasNotAnInt()
      throws Exception {
    // given
    String expexted = "Enter student name" + NEWLINE + "Enter student last name"
        + NEWLINE + "Enter group id to add this student" + NEWLINE
        + "Your Input was not correct";
    Mockito.when(mockScanner.next()).thenReturn(name).thenReturn(lastname)
        .thenReturn("not an integer id");
    command = new AddStudentCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expexted, actual.trim());
  }

  @Test
  void testAddStudent_shouldPrintWrongGroupId_whenIdWasOutOfRange()
      throws Exception {
    // given
    String expexted = "Enter student name" + NEWLINE + "Enter student last name"
        + NEWLINE + "Enter group id to add this student" + NEWLINE
        + "Wrong groupd id";
    Mockito.when(mockScanner.next()).thenReturn(name).thenReturn(lastname)
        .thenReturn("322");
    command = new AddStudentCommand(mockScanner, mockOptions);
    // when
    String actual = tapSystemOut(() -> command.execute());
    // then
    assertEquals(expexted, actual.trim());
  }

}
