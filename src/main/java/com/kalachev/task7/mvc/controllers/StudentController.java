package com.kalachev.task7.mvc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.service.StudentOptions;
import com.kalachev.task7.utilities.ControllerUtills;

@Controller
public class StudentController {

  static final String ERROR_PAGE = "bad";
  static final String RESULT = "result";
  static final String VALID = "valid";

  static final String GO_TO_SUCCESS_PAGE = "redirect:/good";
  static final String SUCCESS_PAGE = "good";
  static final String UNEXPECTED_ERROR = "Unexpected Error";

  @Autowired
  StudentOptions studentOptions;

  @GetMapping(value = "/add")
  public String addStudent(Student student) {
    return "add-student";
  }

  @PostMapping("/add")
  public String handleAddStudent(@RequestParam("firstName") String firstName,
      @RequestParam("lastName") String lastName,
      @RequestParam("groupId") String groupId, Model model) {

    String result = ControllerUtills.validateGroupId(groupId);
    if (!result.equals(VALID)) {
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

  @GetMapping("/remove")
  public String deleteStudent(Student student) {
    return "delete-student";
  }

  @PostMapping("/remove")
  public String handleDeleteStudent(@RequestParam("id") String id,
      Model model) {
    String result = ControllerUtills.validateStudentId(id);
    if (!result.equals(VALID)) {
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int studentId = Integer.parseInt(id);
    if (!studentOptions.checkIfStudentIdExists(studentId)) {
      result = "no such student";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (!studentOptions.deleteStudentById(studentId)) {
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
