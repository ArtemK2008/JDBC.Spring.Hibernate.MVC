package com.kalachev.task7.mvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.service.CoursesOptions;
import com.kalachev.task7.service.GroupOptions;
import com.kalachev.task7.service.StudentOptions;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class SchoolControllerTest {

  static final String ERROR_PAGE = "bad";
  static final String NOT_INT = "not an int";
  static final String NEGATIVE_INT = "-1";
  static final String ZERO = "0";

  @Autowired
  SchoolController controller;

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
    // when
    MockMvc mockMvc = standaloneSetup(controller).build();
    // then
    mockMvc.perform(get(url)).andExpect(view().name("school"));
  }

  @Test
  void testGroupPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/1");
    // then
    mockMvc.perform(get(url)).andExpect(view().name("group-size"));
  }

  @Test
  void testGroupPagePost_shouldReturnGroupsNames_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/1");
    List<String> expected = new ArrayList<>();
    expected.add("Group A");
    // when
    String size = "20";
    int iSize = Integer.parseInt(size);
    when(mockGroupOptions.findBySize(iSize)).thenReturn(expected);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name("size-handling-page"));
    verify(mockGroupOptions, times(1)).findBySize(iSize);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenRequestHasNegativeInput()
      throws Exception {
    // given
    String url = ("/1");
    // when
    String size = NEGATIVE_INT;
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockGroupOptions);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenInputIsNotInt()
      throws Exception {
    // given
    String url = ("/1");
    // when
    String size = NOT_INT;
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockGroupOptions);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenNoGroupsReturned()
      throws Exception {
    // given
    String url = ("/1");
    String size = "20";
    int iSize = Integer.parseInt(size);
    // when
    List<String> expected = new ArrayList<>();
    when(mockGroupOptions.findBySize(iSize)).thenReturn(expected);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    verify(mockGroupOptions, times(1)).findBySize(iSize);
  }

  @Test
  void testFindStudentsPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/2");
    // when
    List<String> courses = Arrays.asList("testCourse");
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name("find-by-course"))
        .andExpect(model().attributeExists("courseList"))
        .andExpect(model().attributeDoesNotExist("empty")).andExpect(
            model().attribute("courseList", hasItems(courses.toArray())));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testFindStudentsPageGet_shouldReturnEmptyAttribute_whenNoCoursesExist()
      throws Exception {
    // given
    String url = ("/2");
    // when
    List<String> courses = new ArrayList<>();
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name("find-by-course"))
        .andExpect(model().attributeExists("empty"))
        .andExpect(model().attributeExists("courseList"));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testFindStudentsPagePost_shouldReturnStudentNames_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/2");
    List<String> expected = Arrays.asList("Student A");
    // when
    String course = "Valid Course";
    when(mockStudentOptions.findByCourse(course)).thenReturn(expected);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("course", course))
        .andExpect(view().name("find-by-course-student-list"))
        .andExpect(model().attributeExists("students"))
        .andExpect(model().attributeExists("course"))
        .andExpect(model().attribute("students", hasItems(expected.toArray())));
    verify(mockStudentOptions, times(1)).findByCourse(course);
  }

  @Test
  void testFindStudentsPagePost_shouldReturnErrorPage_whenCourseHasNoStudents()
      throws Exception {
    // given
    String url = ("/2");
    String course = "Test Course";
    // when
    List<String> expected = new ArrayList<>();
    when(mockStudentOptions.findByCourse(course)).thenReturn(expected);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("course", course))
        .andExpect(view().name(ERROR_PAGE))
        .andExpect(model().attributeExists("result"));
    verify(mockStudentOptions, times(1)).findByCourse(course);
  }

  @Test
  void testAddStudentPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/3");
    // then
    mockMvc.perform(get(url)).andExpect(view().name("add-student"));
  }

  @Test
  void testAddStudentPagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    // when
    String groupId = "1";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name("redirect:/good"));
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
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // when
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(true);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
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
    // when
    String groupId = NEGATIVE_INT;
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenIdIsZero()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    // when
    String groupId = ZERO;
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenIdIsTooBig()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    // when
    String groupId = "12";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentAlreadyInGroup(convertedId, firstName,
        lastName)).thenReturn(false);
    when(mockStudentOptions.addNewStudent(firstName, lastName, convertedId))
        .thenReturn(true);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("firstName", firstName)
            .param("lastName", lastName).param("groupId", groupId))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenIdIsNotNumber()
      throws Exception {
    // given
    String url = ("/3");
    String firstName = "John";
    String lastName = "Doe";
    // when
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
    // then
    mockMvc.perform(get(url)).andExpect(view().name("delete-student"));
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/4");
    // when
    String groupId = "198";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name("redirect:/good"));
    verify(mockStudentOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockStudentOptions, times(1)).deleteStudentById(convertedId);
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdIsNotNumber()
      throws Exception {
    // given
    String url = ("/4");
    // when
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
    // when
    String groupId = NEGATIVE_INT;
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdIsZero()
      throws Exception {
    // given
    String url = ("/4");
    // when
    String groupId = ZERO;
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockStudentOptions);
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdNotExist()
      throws Exception {
    // given
    String url = ("/4");
    String groupId = "777";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(false);
    // when
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(false);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name(ERROR_PAGE));
    verify(mockStudentOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockStudentOptions, times(0)).deleteStudentById(convertedId);
  }

  @Test
  void testAddToCoursePageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/5");
    // when
    List<String> courses = Arrays.asList("testCourse");
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name("add-to-course"))
        .andExpect(model().attributeExists("courseList"))
        .andExpect(model().attributeDoesNotExist("empty")).andExpect(
            model().attribute("courseList", hasItems(courses.toArray())));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testAddToCoursePageGet_shouldReturnEmptyAttribute_whenNoCoursesExist()
      throws Exception {
    // given
    String url = ("/5");
    // when
    List<String> courses = new ArrayList<>();
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name("add-to-course"))
        .andExpect(model().attributeExists("empty"))
        .andExpect(model().attributeExists("courseList"));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testAddToCoursePagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/5");
    String course = "valid course";
    // when
    String studentId = "1";
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.checkIfStudentAlreadyInCourse(convertedId, course))
        .thenReturn(false);
    when(mockCourseOptions.addStudentToCourse(convertedId, course))
        .thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url)
        .param("studentId", studentId).param("course", course))
        .andExpect(view().name("redirect:/good"));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(1))
        .checkIfStudentAlreadyInCourse(convertedId, course);
    verify(mockCourseOptions, times(1)).addStudentToCourse(convertedId, course);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenIdNotExist()
      throws Exception {
    // given
    String url = ("/5");
    String studentId = "56565656";
    String course = "valid course";
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentAlreadyInCourse(convertedId, course))
        .thenReturn(false);
    when(mockCourseOptions.addStudentToCourse(convertedId, course))
        .thenReturn(true);
    // when
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(false);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url)
        .param("studentId", studentId).param("course", course))
        .andExpect(view().name(ERROR_PAGE));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(0))
        .checkIfStudentAlreadyInCourse(convertedId, course);
    verify(mockCourseOptions, times(0)).addStudentToCourse(convertedId, course);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenAlreadyInCourse()
      throws Exception {
    // given
    String url = ("/5");
    String studentId = "1";
    String course = "valid course";
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.addStudentToCourse(convertedId, course))
        .thenReturn(true);
    // when
    when(mockCourseOptions.checkIfStudentAlreadyInCourse(convertedId, course))
        .thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url)
        .param("studentId", studentId).param("course", course))
        .andExpect(view().name(ERROR_PAGE));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(1))
        .checkIfStudentAlreadyInCourse(convertedId, course);
    verify(mockCourseOptions, times(0)).addStudentToCourse(convertedId, course);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenIdIsNegative()
      throws Exception {
    // given
    String url = ("/5");
    String course = "valid course";
    // when
    String studentId = NEGATIVE_INT;
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.checkIfStudentAlreadyInCourse(convertedId, course))
        .thenReturn(true);
    when(mockCourseOptions.addStudentToCourse(convertedId, course))
        .thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url)
        .param("studentId", studentId).param("course", course))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockCourseOptions);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenIdIsZero()
      throws Exception {
    // given
    String url = ("/5");
    String course = "valid course";
    // when
    String studentId = ZERO;
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.checkIfStudentAlreadyInCourse(convertedId, course))
        .thenReturn(true);
    when(mockCourseOptions.addStudentToCourse(convertedId, course))
        .thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url)
        .param("studentId", studentId).param("course", course))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockCourseOptions);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenIdIsNotNumber()
      throws Exception {
    // given
    String url = ("/5");
    String course = "valid course";
    // when
    String studentId = NOT_INT;
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url)
        .param("studentId", studentId).param("course", course))
        .andExpect(view().name(ERROR_PAGE));
  }

  @Test
  void testRemoveFromCoursePageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/6");
    // then
    mockMvc.perform(get(url)).andExpect(view().name("remove-from-course"));
  }

  @Test
  void testRemoveFromCoursePagePost_shouldRedirectToNextPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/6");
    List<String> studentCourses = Arrays.asList("course1", "course2");
    // when
    String studentId = "1";
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.findCourseNamesByID(convertedId))
        .thenReturn(studentCourses);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", studentId))
        .andExpect(view().name("redirect:/complete-removing"))
        .andExpect(flash().attributeExists("courses"))
        .andExpect(flash().attributeExists("id"));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(1)).findCourseNamesByID(convertedId);
  }

  @Test
  void testRemoveFromCourseCompleteGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/complete-removing");
    // then
    mockMvc.perform(get(url))
        .andExpect(view().name("remove-from-course-choose-course"));
  }

  @Test
  void testRemoveFromCourseCompletePost_shouldFinishRemoving_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/complete-removing");
    String course = "valid course";
    // when
    String id = "1";
    int convertedId = Integer.parseInt(id);
    when(mockCourseOptions.removeStudentFromCourse(convertedId, course))
        .thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", id)
        .param("course", course)).andExpect(view().name("redirect:/good"));
    verify(mockCourseOptions, times(1)).removeStudentFromCourse(convertedId,
        course);
  }

  @Test
  void testRemoveFromCoursePagePost_shouldErrorPage_whenIdIsNotAnInt()
      throws Exception {
    // given
    String url = ("/6");
    // when
    String studentId = NOT_INT;
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", studentId))
        .andExpect(view().name(ERROR_PAGE));
  }

  @Test
  void testRemoveFromCoursePagePost_shouldErrorPage_whenIdIsNegative()
      throws Exception {
    // given
    String url = ("/6");
    List<String> studentCourses = Arrays.asList("course1", "course2");
    // when
    String studentId = NEGATIVE_INT;
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.findCourseNamesByID(convertedId))
        .thenReturn(studentCourses);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", studentId))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockCourseOptions);
  }

  @Test
  void testRemoveFromCoursePagePost_shouldErrorPage_whenIdIsZero()
      throws Exception {
    // given
    String url = ("/6");
    List<String> studentCourses = Arrays.asList("course1", "course2");
    // when
    String studentId = ZERO;
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.findCourseNamesByID(convertedId))
        .thenReturn(studentCourses);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", studentId))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockCourseOptions);
  }

  @Test
  void testRemoveFromCoursePagePost_shouldErrorPage_whenIdNotExist()
      throws Exception {
    // given
    String url = ("/6");
    List<String> studentCourses = Arrays.asList("course1", "course2");
    // when
    String studentId = "77777";
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(false);
    when(mockCourseOptions.findCourseNamesByID(convertedId))
        .thenReturn(studentCourses);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", studentId))
        .andExpect(view().name(ERROR_PAGE));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(0)).findCourseNamesByID(convertedId);
  }

  @Test
  void testRemoveFromCoursePagePost_shouldErrorPage_whenStudentHasNoCourses()
      throws Exception {
    // given
    String url = ("/6");
    List<String> studentCourses = new ArrayList<>();
    // when
    String studentId = "7";
    int convertedId = Integer.parseInt(studentId);
    when(mockCourseOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockCourseOptions.findCourseNamesByID(convertedId))
        .thenReturn(studentCourses);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", studentId))
        .andExpect(view().name(ERROR_PAGE));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(1)).findCourseNamesByID(convertedId);
  }

  @Test
  void testRemoveFromCourseCompletePost_shouldErrorPage_whenOperationFails()
      throws Exception {
    // given
    String url = ("/complete-removing");
    String id = "1";
    String course = "valid course";
    int convertedId = Integer.parseInt(id);
    // when
    when(mockCourseOptions.removeStudentFromCourse(convertedId, course))
        .thenReturn(false);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", id)
        .param("course", course)).andExpect(view().name(ERROR_PAGE));
    verify(mockCourseOptions, times(1)).removeStudentFromCourse(convertedId,
        course);
  }

}
