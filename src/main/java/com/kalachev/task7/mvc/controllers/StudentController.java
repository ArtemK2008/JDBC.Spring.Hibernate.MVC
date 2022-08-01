package com.kalachev.task7.mvc.controllers;

import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.service.StudentOptions;
import com.kalachev.task7.utilities.ValidationUtills;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StudentController {

  static final String ERROR_PAGE = "error-page";
  static final String RESULT = "result";
  static final String VALID = "valid";
  static final String NOT_VALID = "not valid";
  static final int MIN_POSSIBLE_SIZE = 1;
  static final int MAX_POSSIBLE_SIZE = 11;

  static final String GO_TO_SUCCESS_PAGE = "redirect:/success-page";
  static final String SUCCESS_PAGE = "success-page";
  static final String UNEXPECTED_ERROR = "Unexpected Error";

  @Autowired
  StudentOptions studentOptions;

  @GetMapping(value = "/add-student")
  public String addStudent(Student student) {
    return "insert-student";
  }

  @PostMapping("/add-student")
  public String handleAddStudent(@RequestParam("firstName") String firstName,
      @RequestParam("lastName") String lastName,
      @RequestParam("groupId") String groupId, Model model) {

    String result = ValidationUtills.validateInput(groupId, MIN_POSSIBLE_SIZE,
        MAX_POSSIBLE_SIZE);
    if (!result.equals(VALID)) {
      if (result.equals(NOT_VALID)) {
        result = "Wrong groupd id";
      }
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int id = Integer.parseInt(groupId);
    if (studentOptions.checkIfStudentAlreadyInGroup(id, firstName, lastName)) {
      result = "User Already exists";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (!studentOptions.addNewStudent(firstName, lastName, id)) {
      result = UNEXPECTED_ERROR;
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    return "redirect:/proceed-add-student";
  }

  @GetMapping("/proceed-add-student")
  public String finishAdding() {
    return SUCCESS_PAGE;
  }

  @GetMapping("/remove-student")
  public String deleteStudent() {
    return "delete-student";
  }

  @PostMapping("/remove-student")
  public String handleDeleteStudent(@RequestParam("studentId") String studentId,
      Model model) {
    String result = ValidationUtills.validateInput(studentId, 1,
        Integer.MAX_VALUE);
    if (!result.equals(VALID)) {
      if (result.equals(NOT_VALID)) {
        result = "Wrong student id";
      }
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int id = Integer.parseInt(studentId);
    if (!studentOptions.checkIfStudentIdExists(id)) {
      result = "no such student";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (!studentOptions.deleteStudentById(id)) {
      result = UNEXPECTED_ERROR;
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    return "redirect:/proceed-remove-student";
  }

  @GetMapping("/proceed-remove-student")
  public String finishRemoving() {
    return SUCCESS_PAGE;
  }

}
