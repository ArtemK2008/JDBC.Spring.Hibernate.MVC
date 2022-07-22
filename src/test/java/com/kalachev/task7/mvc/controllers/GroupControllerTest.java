package com.kalachev.task7.mvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
class GroupControllerTest {

  static final String ERROR_PAGE = "bad";
  static final String NOT_INT = "not an int";
  static final String NEGATIVE_INT = "-1";

  static final String FILTER_BY_SIZE_PAGE = "/filter-by-group-size";
  static final String CHOOSE_SIZE_PAGE = "choose-size-page";
  static final String PROCEED_FILTER_PAGE = "/proceed-group-filter";
  static final String FILTERED_RESULT_PAGE = "filtered-result-page";

  @Autowired
  GroupController controller;

  @MockBean
  GroupOptions mockGroupOptions;

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
  void testGroupPageGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (FILTER_BY_SIZE_PAGE);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(CHOOSE_SIZE_PAGE));
  }

  @Test
  void testGroupPageProceedGet_shouldReturnRightPage_whenRequestIsValid()
      throws Exception {
    // given
    String url = (PROCEED_FILTER_PAGE);
    // then
    mockMvc.perform(get(url)).andExpect(view().name(FILTERED_RESULT_PAGE));
  }

  @Test
  void testGroupPagePost_shouldReturnGroupsNames_whenRequestIsValid()
      throws Exception {
    // given
    String url = (FILTER_BY_SIZE_PAGE);
    List<String> expected = new ArrayList<>();
    expected.add("Group A");
    // when
    String size = "20";
    int iSize = Integer.parseInt(size);
    when(mockGroupOptions.findBySize(iSize)).thenReturn(expected);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("minGroupSize", size))
        .andExpect(view().name("redirect:" + PROCEED_FILTER_PAGE))
        .andExpect(flash().attributeExists("groups"))
        .andExpect(flash().attributeExists("minGroupSize"));
    verify(mockGroupOptions, times(1)).findBySize(iSize);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenRequestHasNegativeInput()
      throws Exception {
    // given
    String url = (FILTER_BY_SIZE_PAGE);
    // when
    String size = NEGATIVE_INT;
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("minGroupSize", size))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockGroupOptions);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenInputIsNotInt()
      throws Exception {
    // given
    String url = (FILTER_BY_SIZE_PAGE);
    // when
    String size = NOT_INT;
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("minGroupSize", size))
        .andExpect(view().name(ERROR_PAGE));
    verifyNoInteractions(mockGroupOptions);
  }

  @Test
  void testGroupPagePost_shouldReturnErrorPage_whenNoGroupsReturned()
      throws Exception {
    // given
    String url = (FILTER_BY_SIZE_PAGE);
    String size = "20";
    int iSize = Integer.parseInt(size);
    // when
    List<String> expected = new ArrayList<>();
    when(mockGroupOptions.findBySize(iSize)).thenReturn(expected);
    // then
    mockMvc
        .perform(MockMvcRequestBuilders.post(url).param("minGroupSize", size))
        .andExpect(view().name(ERROR_PAGE));
    verify(mockGroupOptions, times(1)).findBySize(iSize);
  }
}
