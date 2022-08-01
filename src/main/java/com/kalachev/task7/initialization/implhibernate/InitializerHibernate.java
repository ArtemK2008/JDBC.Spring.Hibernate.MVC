package com.kalachev.task7.initialization.implhibernate;

import java.util.List;
import java.util.Map;

import com.kalachev.task7.initialization.CoursesInitializer;
import com.kalachev.task7.initialization.GroupInitializer;
import com.kalachev.task7.initialization.StudentInitializer;

import org.springframework.beans.factory.annotation.Autowired;

public class InitializerHibernate {
  List<String> groups;
  List<String> students;
  Map<String, String> courses;
  @Autowired
  StudentInitializer studentInitializerImpl;
  @Autowired
  CoursesInitializer coursesInitializerImpl;
  @Autowired
  GroupInitializer groupIntitializerImpl;

  public InitializerHibernate(StudentInitializer studentInitializerImpl,
      CoursesInitializer coursesInitializerImpl,
      GroupInitializer groupIntitializerImpl) {
    super();
    this.studentInitializerImpl = studentInitializerImpl;
    this.coursesInitializerImpl = coursesInitializerImpl;
    this.groupIntitializerImpl = groupIntitializerImpl;
  }

  private void generateStudentData() {
    groups = groupIntitializerImpl.generateGroups();
    students = studentInitializerImpl.generateStudents();
    courses = coursesInitializerImpl.generateCourses();
  }

}
