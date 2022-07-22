package com.kalachev.task7.mvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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
import com.kalachev.task7.service.StudentOptions;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class StudentControllerTest {

  static final String ERROR_PAGE = "bad";
  static final String SUCCESS_PAGE = "good";
  static final String NOT_INT = "not an int";
  static final String NEGATIVE_INT = "-1";
  static final String ZERO = "0";

  static final String ADD_URL = "/add";
  static final String ADD_STUDENT_PAGE = "add-student";
  static final String PROCEED_ADD_STUDENT_PAGE = "/proceed-add-student";

  static final String REMOVE_URL = "/remove";
  static final String REMOVE_STUDENT_PAGE = "delete-student";
  static final String PROCEED_REMOVE_STUDENT_PAGE = "/proceed-remove-student";

  @Autowired
  StudentController controller;

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
  void testAddStudentPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (ADD_URL);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(ADD_STUDENT_PAGE));
  }

  @Test
  void testProceedAddStudentPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (PROCEED_ADD_STUDENT_PAGE);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(SUCCESS_PAGE));
  }

  @Test
  void testAddStudentPagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (ADD_URL);
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
        .andExpect(view().name("redirect:" + PROCEED_ADD_STUDENT_PAGE));
    verify(mockStudentOptions, times(1))
        .checkIfStudentAlreadyInGroup(convertedId, firstName, lastName);
    verify(mockStudentOptions, times(1)).addNewStudent(firstName, lastName,
        convertedId);
  }

  @Test
  void testAddStudentPagePost_shouldReturnErrorPage_whenStudentExists()
      throws Exception {
    // given
    String url = (ADD_URL);
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
    String url = (ADD_URL);
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
    String url = (ADD_URL);
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
    String url = (ADD_URL);
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
    String url = (ADD_URL);
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
    String url = (REMOVE_URL);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(REMOVE_STUDENT_PAGE));
  }

  @Test
  void testProceedRemoveStudentPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (PROCEED_REMOVE_STUDENT_PAGE);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(SUCCESS_PAGE));
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnSuccesfullPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (REMOVE_URL);
    // when
    String groupId = "198";
    int convertedId = Integer.parseInt(groupId);
    when(mockStudentOptions.checkIfStudentIdExists(convertedId))
        .thenReturn(true);
    when(mockStudentOptions.deleteStudentById(convertedId)).thenReturn(true);
    // then
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("id", groupId))
        .andExpect(view().name("redirect:" + PROCEED_REMOVE_STUDENT_PAGE));
    verify(mockStudentOptions, times(1)).checkIfStudentIdExists(convertedId);
    verify(mockStudentOptions, times(1)).deleteStudentById(convertedId);
  }

  @Test
  void testDeleteStudentPagePost_shouldReturnErrorPage_whenIdIsNotNumber()
      throws Exception {
    // given
    String url = (REMOVE_URL);
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
    String url = (REMOVE_URL);
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
    String url = (REMOVE_URL);
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
    String url = (REMOVE_URL);
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

}
