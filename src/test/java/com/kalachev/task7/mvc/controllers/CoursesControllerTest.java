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
import com.kalachev.task7.service.StudentOptions;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class CoursesControllerTest {

  static final String ERROR_PAGE = "bad";
  static final String NOT_INT = "not an int";
  static final String NEGATIVE_INT = "-1";
  static final String ZERO = "0";
  static final String SUCCES_PAGE = "good";

  static final String FIND_COURSE_STUDENTS_URL = "/find-all-course-students";
  static final String FIND_BY_COURSE_PAGE = "find-by-course";
  static final String PROCEED_FIND_BY_COURSE = "/proceed-find-students";
  static final String FIND_BY_COURSE_RESULT_PAGE = "find-by-course-student-list";

  static final String ADD_TO_COURSE_URL = "/add-student-to-course";
  static final String ADD_TO_COURSE_PAGE = "add-to-course";
  static final String FINISH_ADDING_TO_COURSE_PAGE = "/finish-add-to-course";

  static final String REMOVE_FROM_COURSE_URL = "/remove-student-from-course";
  static final String REMOVE_FROM_COURSE_PAGE = "remove-from-course";
  static final String PROCEED_REMOVE_FROM_COURSE = "/proceed-removing";
  static final String FINISH_REMOVE_FROM_COURSE = "/finish-removing";
  static final String REMOVE_CHOOSE_COURSE = "remove-from-course-choose-course";

  @MockBean
  CoursesOptions mockCourseOptions;

  @MockBean
  StudentOptions mockStudentOptions;

  @Autowired
  CourseController controller;

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
  void testFindStudentsPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (FIND_COURSE_STUDENTS_URL);
    // when
    List<String> courses = Arrays.asList("testCourse");
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(FIND_BY_COURSE_PAGE))
        .andExpect(model().attributeExists("courseList"))
        .andExpect(model().attributeDoesNotExist("empty")).andExpect(
            model().attribute("courseList", hasItems(courses.toArray())));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testFindStudentsPageGet_shouldReturnEmptyAttribute_whenNoCoursesExist()
      throws Exception {
    // given
    String url = (FIND_COURSE_STUDENTS_URL);
    // when
    List<String> courses = new ArrayList<>();
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(FIND_BY_COURSE_PAGE))
        .andExpect(model().attributeExists("empty"))
        .andExpect(model().attributeExists("courseList"));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testProceedFindStudentsPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (PROCEED_FIND_BY_COURSE);
    // then
    mockMvc.perform(get(url))
        .andExpect(view().name(FIND_BY_COURSE_RESULT_PAGE));
  }

  @Test
  void testFindStudentsPagePost_shouldReturnStudentNames_whenRequestIsValid()
      throws Exception {
    // given
    String url = (FIND_COURSE_STUDENTS_URL);
    List<String> expected = Arrays.asList("Student A");
    // when
    String course = "Valid Course";
    when(mockStudentOptions.findByCourse(course)).thenReturn(expected);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("courseName", course))
        .andExpect(view().name("redirect:" + PROCEED_FIND_BY_COURSE))
        .andExpect(flash().attributeExists("students"))
        .andExpect(flash().attributeExists("courseName"))
        .andExpect(flash().attribute("students", hasItems(expected.toArray())));
    verify(mockStudentOptions, times(1)).findByCourse(course);
  }

  @Test
  void testFindStudentsPagePost_shouldReturnErrorPage_whenCourseHasNoStudents()
      throws Exception {
    // given
    String url = (FIND_COURSE_STUDENTS_URL);
    String course = "Test Course";
    // when
    List<String> expected = new ArrayList<>();
    when(mockStudentOptions.findByCourse(course)).thenReturn(expected);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("courseName", course))
        .andExpect(view().name(ERROR_PAGE))
        .andExpect(model().attributeExists("result"));
    verify(mockStudentOptions, times(1)).findByCourse(course);
  }

  @Test
  void testAddToCoursePageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (ADD_TO_COURSE_URL);
    // when
    List<String> courses = Arrays.asList("testCourse");
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(ADD_TO_COURSE_PAGE))
        .andExpect(model().attributeExists("courseList"))
        .andExpect(model().attributeDoesNotExist("empty")).andExpect(
            model().attribute("courseList", hasItems(courses.toArray())));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testAddToCoursePageGet_shouldReturnEmptyAttribute_whenNoCoursesExist()
      throws Exception {
    // given
    String url = (ADD_TO_COURSE_URL);
    // when
    List<String> courses = new ArrayList<>();
    when(mockCourseOptions.findCourseNames()).thenReturn(courses);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(ADD_TO_COURSE_PAGE))
        .andExpect(model().attributeExists("empty"))
        .andExpect(model().attributeExists("courseList"));
    verify(mockCourseOptions, times(1)).findCourseNames();
  }

  @Test
  void testFinishAddToCoursePageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (FINISH_ADDING_TO_COURSE_PAGE);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(SUCCES_PAGE));
  }

  @Test
  void testAddToCoursePagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (ADD_TO_COURSE_URL);
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
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", studentId)
            .param("courseName", course))
        .andExpect(view().name("redirect:" + FINISH_ADDING_TO_COURSE_PAGE));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(1))
        .checkIfStudentAlreadyInCourse(convertedId, course);
    verify(mockCourseOptions, times(1)).addStudentToCourse(convertedId, course);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenIdNotExist()
      throws Exception {
    // given
    String url = (ADD_TO_COURSE_URL);
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
        .param("studentId", studentId).param("courseName", course))
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
    String url = (ADD_TO_COURSE_URL);
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
        .param("studentId", studentId).param("courseName", course))
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
    String url = (ADD_TO_COURSE_URL);
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
        .param("studentId", studentId).param("courseName", course))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockCourseOptions);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenIdIsZero()
      throws Exception {
    // given
    String url = (ADD_TO_COURSE_URL);
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
        .param("studentId", studentId).param("courseName", course))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockCourseOptions);
  }

  @Test
  void testAddToCoursePagePost_shouldReturnErrorPage_whenIdIsNotNumber()
      throws Exception {
    // given
    String url = (ADD_TO_COURSE_URL);
    String course = "valid course";
    // when
    String studentId = NOT_INT;
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url)
        .param("studentId", studentId).param("courseName", course))
        .andExpect(view().name(ERROR_PAGE));
  }

  @Test
  void testRemoveFromCoursePageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (REMOVE_FROM_COURSE_URL);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(REMOVE_FROM_COURSE_PAGE));
  }

  @Test
  void testRemoveFromCourseProceedGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (PROCEED_REMOVE_FROM_COURSE);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(REMOVE_CHOOSE_COURSE));
  }

  @Test
  void testRemoveFromCourseFinishGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (FINISH_REMOVE_FROM_COURSE);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(SUCCES_PAGE));
  }

  @Test
  void testRemoveFromCoursePagePost_shouldRedirectToNextPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (REMOVE_FROM_COURSE_URL);
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
        .andExpect(view().name("redirect:" + PROCEED_REMOVE_FROM_COURSE))
        .andExpect(flash().attributeExists("courses"))
        .andExpect(flash().attributeExists("studId"));
    verify(mockCourseOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockCourseOptions, times(1)).findCourseNamesByID(convertedId);
  }

  @Test
  void testRemoveFromCourseCompletePost_shouldFinishRemoving_whenRequestIsValid()
      throws Exception {
    // given
    String url = (PROCEED_REMOVE_FROM_COURSE);
    String course = "valid course";
    // when
    String id = "1";
    int convertedId = Integer.parseInt(id);
    when(mockCourseOptions.removeStudentFromCourse(convertedId, course))
        .thenReturn(true);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("studentId", id)
            .param("courseName", course))
        .andExpect(view().name("redirect:" + FINISH_REMOVE_FROM_COURSE));
    verify(mockCourseOptions, times(1)).removeStudentFromCourse(convertedId,
        course);
  }

  @Test
  void testRemoveFromCoursePagePost_shouldErrorPage_whenIdIsNotAnInt()
      throws Exception {
    // given
    String url = (REMOVE_FROM_COURSE_URL);
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
    String url = (REMOVE_FROM_COURSE_URL);
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
    String url = (REMOVE_FROM_COURSE_URL);
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
    String url = (REMOVE_FROM_COURSE_URL);
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
    String url = (REMOVE_FROM_COURSE_URL);
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
    String url = (PROCEED_REMOVE_FROM_COURSE);
    String id = "1";
    String course = "valid course";
    int convertedId = Integer.parseInt(id);
    // when
    when(mockCourseOptions.removeStudentFromCourse(convertedId, course))
        .thenReturn(false);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("studentId", id)
        .param("courseName", course)).andExpect(view().name(ERROR_PAGE));
    verify(mockCourseOptions, times(1)).removeStudentFromCourse(convertedId,
        course);
  }

}
