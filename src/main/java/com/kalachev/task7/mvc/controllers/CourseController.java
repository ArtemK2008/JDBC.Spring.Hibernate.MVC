package com.kalachev.task7.mvc.controllers;

import java.util.List;

import com.kalachev.task7.service.CoursesOptions;
import com.kalachev.task7.service.StudentOptions;
import com.kalachev.task7.utilities.ValidationUtills;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CourseController {

  static final String ERROR_PAGE = "error-page";
  static final String RESULT = "result";
  static final String SUCCESS_PAGE = "success-page";
  static final String VALID = "valid";
  static final String NOT_VALID = "not valid";
  static final String UNEXPECTED_ERROR = "Unexpected Error";

  @Autowired
  CoursesOptions coursesOptions;

  @Autowired
  StudentOptions studentOptions;

  @GetMapping(value = "/find-all-course-students")
  public String findStudents(Model model) {
    List<String> courses = coursesOptions.findCourseNames();
    if (courses.isEmpty()) {
      model.addAttribute("empty", "no course found");
    }
    model.addAttribute("courseList", courses);
    return "find-by-course";
  }

  @PostMapping(value = "/find-all-course-students")
  public String handleFindStudents(@RequestParam("courseName") String course,
      Model model, RedirectAttributes redirectAttributes) {
    List<String> courseStudents = studentOptions.findByCourse(course);
    if (courseStudents.isEmpty()) {
      model.addAttribute(RESULT, "no students in this course");
      return ERROR_PAGE;
    }
    redirectAttributes.addFlashAttribute("students", courseStudents);
    redirectAttributes.addFlashAttribute("courseName", course);
    return "redirect:/proceed-find-students";
  }

  @GetMapping(value = "/proceed-find-students")
  public String listCourseStudents() {
    return "find-by-course-student-list";
  }

  @GetMapping(value = "/add-student-to-course")
  public String addToCourse(Model model) {
    List<String> courses = coursesOptions.findCourseNames();
    if (courses.isEmpty()) {
      model.addAttribute("empty", "no course found");
    }
    model.addAttribute("courseList", courses);
    return "add-to-course";
  }

  @PostMapping("/add-student-to-course")
  public String handleAddToCourse(@RequestParam("studentId") String studentId,
      @RequestParam("courseName") String course, Model model,
      RedirectAttributes redirectAttributes) {

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
    if (!coursesOptions.checkIfStudentIdExists(id)) {
      result = "There is no student with such id";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (coursesOptions.checkIfStudentAlreadyInCourse(id, course)) {
      result = "student already in course";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    if (!coursesOptions.addStudentToCourse(id, course)) {
      result = UNEXPECTED_ERROR;
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    return "redirect:/finish-add-to-course";
  }

  @GetMapping(value = "/finish-add-to-course")
  public String finishAddingToCourse() {
    return SUCCESS_PAGE;
  }

  @GetMapping(value = "/remove-student-from-course")
  public String removeFromCourse() {
    return "remove-from-course";
  }

  @PostMapping(value = "/remove-student-from-course")
  public String findRemovingStudentCourses(
      @RequestParam("studentId") String studentId,
      RedirectAttributes redirectAttributes, Model model) {

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
    if (!coursesOptions.checkIfStudentIdExists(id)) {
      result = "There is no student with such id";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    List<String> coursesOfAStudent = coursesOptions.findCourseNamesByID(id);
    if (coursesOfAStudent.isEmpty()) {
      model.addAttribute(RESULT,
          "Student with id " + studentId + " is not enrolled in any course");
      return ERROR_PAGE;
    }
    redirectAttributes.addFlashAttribute("courses", coursesOfAStudent);
    redirectAttributes.addFlashAttribute("studId", studentId);
    return "redirect:/proceed-removing";
  }

  @GetMapping("/proceed-removing")
  public String chooseCourseToRemove() {
    return "remove-from-course-choose-course";
  }

  @PostMapping("/proceed-removing")
  public String handleRemoveFromCourse(
      @RequestParam("courseName") String course,
      @RequestParam("studentId") Integer id) {
    boolean isRemoved = coursesOptions.removeStudentFromCourse(id, course);
    if (!isRemoved) {
      return ERROR_PAGE;
    }
    return "redirect:/finish-removing";
  }

  @GetMapping("/finish-removing")
  public String removeResult() {
    return SUCCESS_PAGE;
  }

  @GetMapping("/error-page")
  public String badResult() {
    return ERROR_PAGE;
  }

  @GetMapping("/success-page")
  public String goodResult() {
    return SUCCESS_PAGE;
  }

}
