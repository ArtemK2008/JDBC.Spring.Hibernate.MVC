package com.kalachev.task7.mvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.events.initializationEvent;
import com.kalachev.task7.service.CoursesOptions;
import com.kalachev.task7.service.GroupOptions;
import com.kalachev.task7.service.StudentOptions;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class SchoolControllerTest {

  static final String ERROR_PAGE = "bad";
  static final String NOT_INT = "not an int";
  static final String NEGATIVE_INT = "-1";

  @Autowired
  SchoolController controller;

  @Autowired
  ApplicationEventPublisher publisher;

  @MockBean
  GroupOptions mockGroupOptions;

  @MockBean
  CoursesOptions mockCourseOptions;

  @MockBean
  StudentOptions mockStudentOptions;

  MockMvc mockMvc;

  @BeforeEach
  void buildMock() {
    mockMvc = standaloneSetup(controller).build();
  }

  @Test
  void contextLoads() throws Exception {
    assertThat(controller).isNotNull();
  }

  @Test
  void testSchoolPage_shouldReturnRightView_whenRequestIsGet()
      throws Exception {
    // given
    String url = ("/");
    MockMvc mockMvc = standaloneSetup(controller).build();
    // then
    mockMvc.perform(get(url)).andExpect(view().name("school"));
  }

  @Test
  void testGroupPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/1");
    // when
    mockMvc.perform(get(url)).andExpect(view().name("group-size"));
  }

  @Test
  void testGroupPagePost_shouldReturnGroupsNames_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/1");
    List<String> expected = new ArrayList<>();
    expected.add("Group A");
    String size = "20";
    int iSize = Integer.parseInt(size);
    when(mockGroupOptions.findBySize(iSize)).thenReturn(expected);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name("size-handling-page"));
    // then
    verify(mockGroupOptions, times(1)).findBySize(iSize);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenRequestHasNegativeInput()
      throws Exception {
    // given
    String url = ("/1");
    String size = NEGATIVE_INT;
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockGroupOptions);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenInputIsNotInt()
      throws Exception {
    // given
    String url = ("/1");
    String size = "a";
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockGroupOptions);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenNoGroupsReturned()
      throws Exception {
    // given
    String url = ("/1");
    List<String> expected = new ArrayList<>();
    String size = "20";
    int iSize = Integer.parseInt(size);
    when(mockGroupOptions.findBySize(iSize)).thenReturn(expected);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verify(mockGroupOptions, times(1)).findBySize(iSize);
  }

  @Test
  void testFindStudentsPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/2");
    List<String> courses = Arrays.asList("testCourse");
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);

    // when
    mockMvc.perform(get(url)).andExpect(view().name("find-by-course"))
        .andExpect(model().attributeExists("courseList"))
        .andExpect(model().attributeDoesNotExist("empty")).andExpect(
            model().attribute("courseList", hasItems(courses.toArray())));
    // then
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testFindStudentsPageGet_shouldReturnEmptyAttribute_whenNoCoursesExist()
      throws Exception {
    // given
    String url = ("/2");
    List<String> courses = new ArrayList<>();
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // when
    mockMvc.perform(get(url)).andExpect(view().name("find-by-course"))
        .andExpect(model().attributeExists("empty"))
        .andExpect(model().attributeExists("courseList"));
    // then
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testFindStudentsPagePost_shouldReturnStudentNames_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/2");
    List<String> expected = Arrays.asList("Student A");
    String course = "Test Course";
    when(mockStudentOptions.findByCourse(course)).thenReturn(expected);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("course", course))
        .andExpect(view().name("find-by-course-student-list"))
        .andExpect(model().attributeExists("students"))
        .andExpect(model().attributeExists("course"))
        .andExpect(model().attribute("students", hasItems(expected.toArray())));
    // then
    verify(mockStudentOptions, times(1)).findByCourse(course);
  }

  @Test
  void testFindStudentsPagePost_shouldReturnErrorPage_whenCourseHasNoStudents()
      throws Exception {
    // given
    String url = ("/2");
    List<String> expected = new ArrayList<>();
    String course = "Test Course";
    when(mockStudentOptions.findByCourse(course)).thenReturn(expected);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("course", course))
        .andExpect(view().name(ERROR_PAGE))
        .andExpect(model().attributeExists("result"));
    // then
    verify(mockStudentOptions, times(1)).findByCourse(course);
  }

  @Test
  void testAddStudentPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/3");
    // when
    mockMvc.perform(get(url)).andExpect(view().name("add-student"));
  }

  @Test
  void testAddStudentPagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    String groupId = "1";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // when
    publisher.publishEvent(new initializationEvent(this));
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name("redirect:/good"));
    // then
    verify(mockStudentOptions, times(1))
        .checkIfStudentAlreadyInGroup(convertedId, firstName, lastName);
    verify(mockStudentOptions, times(1)).addNewStudent(firstName, lastName,
        convertedId);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenStudentExists()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    String groupId = "1";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(true);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // when
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verify(mockStudentOptions, times(1))
        .checkIfStudentAlreadyInGroup(convertedId, firstName, lastName);
    verify(mockStudentOptions, times(0)).addNewStudent(firstName, lastName,
        convertedId);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenIdIsNegative()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    String groupId = NEGATIVE_INT;
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // when
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenIdIsZero()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    String groupId = "0";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // when
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenIdIsTooBig()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    String groupId = "12";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // when
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenIdIsNotNumber()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    String groupId = NOT_INT;
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
  }

  @Test
  void testDeleteStudentPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/4");
    // when
    mockMvc.perform(get(url)).andExpect(view().name("delete-student"));
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/4");
    String groupId = "198";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(true);
    // when
    publisher.publishEvent(new initializationEvent(this));
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name("redirect:/good"));
    // then
    verify(mockStudentOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockStudentOptions, times(1)).deleteStudentById(convertedId);
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdIsNotNumber()
      throws Exception {
    // given
    String url = ("/4");
    String groupId = NOT_INT;
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name(ERROR_PAGE));
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdIsNegative()
      throws Exception {
    // given
    String url = ("/4");
    String groupId = NEGATIVE_INT;
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(true);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdIsZero()
      throws Exception {
    // given
    String url = ("/4");
    String groupId = "0";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(true);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdNotExist()
      throws Exception {
    // given
    String url = ("/4");
    String groupId = "777";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(false);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(false);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verify(mockStudentOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockStudentOptions, times(0)).deleteStudentById(convertedId);
  }

}
