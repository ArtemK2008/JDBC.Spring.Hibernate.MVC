package com.kalachev.task7.mvc.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.kalachev.task7.configuration.ConsoleAppConfig;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class SchoolControllerTest {

  static final String ERROR_PAGE = "bad";
  static final String NOT_INT = "not an int";
  static final String NEGATIVE_INT = "-1";
  static final String ZERO = "0";

  @Autowired
  SchoolController controller;

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
    // then
    mockMvc.perform(get(url)).andExpect(view().name("school"));
  }

}
