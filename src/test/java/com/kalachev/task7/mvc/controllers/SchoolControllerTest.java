package com.kalachev.task7.mvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
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
import com.kalachev.task7.service.GroupOptions;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class SchoolControllerTest {

  static final String ERROR_PAGE = "bad";
  static final String NOT_INT = "not an int";

  @Autowired
  SchoolController controller;

  @MockBean
  GroupOptions mockOptions;
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
  void testGroupPagePost_shouldReturnStudentNames_whenRequestIsValid()
      throws Exception {
    // given
    String url = ("/1");
    List<String> expected = new ArrayList<>();
    expected.add("Student A");
    String size = "20";
    int iSize = Integer.parseInt(size);
    when(mockOptions.findBySize(iSize)).thenReturn(expected);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name("size-handling-page"));
    // then
    verify(mockOptions, times(1)).findBySize(iSize);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenRequestHasNegativeInput()
      throws Exception {
    // given
    String url = ("/1");
    String size = "-1";
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verifyNoInteractions(mockOptions);
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
    verifyNoInteractions(mockOptions);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenNoGroupsReturned()
      throws Exception {
    // given
    String url = ("/1");
    List<String> expected = new ArrayList<>();
    String size = "20";
    int iSize = Integer.parseInt(size);
    when(mockOptions.findBySize(iSize)).thenReturn(expected);
    // when
    mockMvc.perform(MockMvcRequestBuilders.post(url).param("size", size))
        .andExpect(view().name(ERROR_PAGE));
    // then
    verify(mockOptions, times(1)).findBySize(iSize);
  }
}
