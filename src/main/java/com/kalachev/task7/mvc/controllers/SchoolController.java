package com.kalachev.task7.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.service.CoursesOptions;
import com.kalachev.task7.service.GroupOptions;
import com.kalachev.task7.service.StudentOptions;
import com.kalachev.task7.utilities.ControllerUtills;

@Controller
@RequestMapping("/")
public class SchoolController {

  static final String ERROR_PAGE = "bad";
  static final String GO_TO_SUCCESS_PAGE = "redirect:/good";
  static final String SUCCESS_PAGE = "good";
  static final String RESULT = "result";
  static final String VALID = "valid";
  @Autowired
  CoursesOptions cOptions;

  @Autowired
  GroupOptions gOptions;

  @Autowired
  StudentOptions sOptions;

  @GetMapping
  public String school() {
    return "school";
  }

  @GetMapping(value = "/1")
  public String groupCommand() {
    return "group-size";
  }

  @PostMapping("/1")
  public String handleGroupCommand(@RequestParam("size") String size,
      Model model) {
    String result = ControllerUtills.validateSize(size);
    if (!result.equals(VALID)) {
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int validSize = Integer.parseInt(size);
    List<String> groups = gOptions.findBySize(validSize);
    if (groups.isEmpty()) {
      result = "no groups found";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    model.addAttribute("groups", groups);
    model.addAttribute("size", size);
    return "size-handling-page";
  }

  @GetMapping(value = "/2")
  public String findStudents(Model model) {
    List<String> courses = cOptions.findCourseNames();
    if (courses.isEmpty()) {
      model.addAttribute("empty", "no course found");
    }
    model.addAttribute("courseList", courses);
    return "find-by-course";
  }

  @PostMapping(value = "/2")
  public String handleFindStudents(@RequestParam("course") String course,
      Model model) {
    List<String> courseStudents = sOptions.findByCourse(course);
    if (courseStudents.isEmpty()) {
      model.addAttribute(RESULT, "no students in this course");
      return ERROR_PAGE;
    }
    model.addAttribute("students", courseStudents);
    model.addAttribute("course", course);
    return "find-by-course-student-list";
  }

  @GetMapping(value = "/3")
  public String addStudent(Student student) {
    return "add-student";
  }

  @PostMapping("/3")
  public String handleAddStudent(@RequestParam("firstName") String firstName,
      @RequestParam("lastName") String lastName,
      @RequestParam("groupId") String groupId, Model model) {

    String result = ControllerUtills.validateGroupId(groupId);
    if (!result.equals(VALID)) {
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int id = Integer.parseInt(groupId);
    if (sOptions.checkIfStudentAlreadyInGroup(id, firstName, lastName)) {
      result = "User Already exists";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (!sOptions.addNewStudent(firstName, lastName, id)) {
      result = "Unexpected Error";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    return GO_TO_SUCCESS_PAGE;
  }

  @GetMapping(value = "/4")
  public String deleteStudent(Student student) {
    return "delete-student";
  }

  @PostMapping("/4")
  public String handleDeleteStudent(@RequestParam("id") String id,
      Model model) {
    String result = ControllerUtills.validateStudentId(id);
    if (!result.equals(VALID)) {
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int studentId = Integer.parseInt(id);
    if (!sOptions.checkIfStudentIdExists(studentId)) {
      result = "no such student";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (!sOptions.deleteStudentById(studentId)) {
      result = "Unexpected Error";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    return GO_TO_SUCCESS_PAGE;
  }

  @GetMapping(value = "/5")
  public String addToCourse(Model model) {
    List<String> courses = cOptions.findCourseNames();
    if (courses.isEmpty()) {
      model.addAttribute("empty", "no course found");
    }
    model.addAttribute("courseList", courses);
    return "add-to-course";
  }

  @PostMapping("/5")
  public String handleAddToCourse(@RequestParam("studentId") String studentId,
      @RequestParam("course") String course, Model model) {

    String result = ControllerUtills.validateStudentId(studentId);
    if (!result.equals(VALID)) {
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int id = Integer.parseInt(studentId);
    if (!cOptions.checkIfStudentIdExists(id)) {
      result = "There is no student with such id";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (cOptions.checkIfStudentAlreadyInCourse(id, course)) {
      result = "student already in course";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (!cOptions.addStudentToCourse(id, course)) {
      result = "Unexpected Error";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    return GO_TO_SUCCESS_PAGE;
  }

  @GetMapping(value = "/6")
  public String removeFromCourse(Model model) {
    return "remove-from-course";
  }

  @PostMapping(value = "/6")
  public String handleRemoveFromCourse(
      @RequestParam("studentId") String studentId,
      RedirectAttributes redirectAttributes, Model model) {

    String result = ControllerUtills.validateStudentId(studentId);
    if (!result.equals(VALID)) {
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int id = Integer.parseInt(studentId);
    if (!cOptions.checkIfStudentIdExists(id)) {
      result = "There is no student with such id";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    List<String> coursesOfAStudent = cOptions.findCourseNamesByID(id);
    if (coursesOfAStudent.isEmpty()) {
      model.addAttribute(RESULT,
          "Student with id " + studentId + " is not enrolled in any course");
      return ERROR_PAGE;
    }
    redirectAttributes.addFlashAttribute("courses", coursesOfAStudent);
    redirectAttributes.addFlashAttribute("id", studentId);
    return "redirect:/complete-removing";
  }

  @GetMapping("/complete-removing")
  public String cmd6g() {
    return "remove-from-course-choose-course";
  }

  @PostMapping("/complete-removing")
  public String cmd6n(@RequestParam("course") String course,
      @RequestParam("id") Integer id) {
    boolean isRemoved = cOptions.removeStudentFromCourse(id, course);
    if (isRemoved) {
      return GO_TO_SUCCESS_PAGE;
    }
    return ERROR_PAGE;
  }

  @GetMapping("bad")
  public String badResult() {
    return ERROR_PAGE;
  }

  @GetMapping("good")
  public String goodResult() {
    return "good";
  }
}
