package com.kalachev.task7.mvc.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.service.CoursesOptions;
import com.kalachev.task7.service.GroupOptions;
import com.kalachev.task7.service.StudentOptions;

@Controller
@RequestMapping("/")
public class StudentController {

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
  public String cmd1() {
    return "group-size";
  }

  @PostMapping("/1")
  public String cmd1Post(@RequestParam("size") int size, Model model) {
    List<String> groupsFiltered = gOptions.findBySize(size);
    model.addAttribute("groups", groupsFiltered);
    model.addAttribute("size", size);
    return "size-handling-page";
  }

  @GetMapping(value = "/2")
  public String cmd2(Model model) {
    List<String> courses = cOptions.findCourseNames();
    model.addAttribute("courseList", courses);
    return "find-by-course";
  }

  @PostMapping(value = "/2")
  public String cmd2Post(@RequestParam("course") String course,
      RedirectAttributes redirectAttributes) {
    List<String> courseStudents = sOptions.findByCourse(course);

    redirectAttributes.addFlashAttribute("students", courseStudents);
    redirectAttributes.addFlashAttribute("course", course);
    return "redirect:/next2";
  }

  @GetMapping("/next2")
  public String cmd2n(Model model) {
    return "find-by-course-student-list";
  }

  @GetMapping(value = "/3")
  public String addStudent(Student student) {
    return "add-student";
  }

  @PostMapping("/3")
  public String handleAddStudent(@Valid Student student,
      BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      return "add-student";
    }

    if (sOptions.addNewStudent(student.getFirstName(), student.getLastName(),
        student.getGroupId())) {
      return "redirect:/good";
    }
    return "redirect:/bad";
  }

  @GetMapping(value = "/4")
  public String deleteStudent(Student student) {
    return "delete-student";
  }

  /*
   * @PostMapping("/4") public String handleDeleteStudent(@RequestParam("id")
   * int id) { if (sOptions.deleteStudentById(id)) { return "redirect:/good"; }
   * return "redirect:/bad"; }
   */

  @PostMapping("/4")
  public String handleDeleteStudent(@Valid Student student,
      BindingResult bindingResult) {

    /*
     * if (bindingResult.hasErrors()) { return "delete-student"; }
     */

    if (sOptions.deleteStudentById(student.getId())) {
      return "redirect:/good";
    }
    return "redirect:/bad";
  }

  @GetMapping(value = "/5")
  public String addToCoursePage() {
    return "add-to-course";
  }

  @PostMapping("/5")
  public String addToCour1sePost(@RequestParam("studentId") int studentId,
      @RequestParam("course") String course) {

    boolean isAdded = cOptions.addStudentToCourse(studentId, course);
    if (isAdded) {
      return "redirect:/good";
    }
    return "redirect:/bad";
  }

  @GetMapping(value = "/6")
  public String cmd6(Model model) {
    return "remove-from-course";
  }

  @PostMapping(value = "/6")
  public String cmd6Post(@RequestParam("studentId") int studentId,
      RedirectAttributes redirectAttributes) {
    List<String> coursesOfAStudent = cOptions.findCourseNamesByID(studentId);
    redirectAttributes.addFlashAttribute("courses", coursesOfAStudent);
    redirectAttributes.addFlashAttribute("id", studentId);
    return "redirect:/next6";
  }

  @GetMapping("/next6")
  public String cmd6g() {
    return "remove-from-course-choose-course";
  }

  @PostMapping("/next6")
  public String cmd6n(@RequestParam("course") String course,
      @RequestParam("id") Integer id) {
    System.out.println(id);
    boolean isRemoved = cOptions.removeStudentFromCourse(id, course);
    if (isRemoved) {
      return "redirect:/good";
    }
    return "redirect:/bad";
  }

  @GetMapping("bad")
  public String badResult() {
    return "bad";
  }

  @GetMapping("good")
  public String goodResult() {
    return "good";
  }
}
