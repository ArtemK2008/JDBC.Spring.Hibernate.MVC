package com.kalachev.task7.initialization.impl.hibernate;

import java.util.List;
import java.util.Map;

import com.kalachev.task7.dao.impl.hibernate.StudentsDaoHibernate;
import com.kalachev.task7.events.InitializationEvent;
import com.kalachev.task7.initialization.CoursesInitializer;
import com.kalachev.task7.initialization.GroupInitializer;
import com.kalachev.task7.initialization.Initializer;
import com.kalachev.task7.initialization.StudentInitializer;
import com.kalachev.task7.initialization.tables.hiberate.CoursesDataHibernatePopulator;
import com.kalachev.task7.initialization.tables.hiberate.DatabaseCleaner;
import com.kalachev.task7.initialization.tables.hiberate.GroupsDataHibernatePopulator;
import com.kalachev.task7.initialization.tables.hiberate.StudentsDataHibernatePopulator;
import com.kalachev.task7.initialization.tables.hiberate.StudentsToCoursesHibernatePopulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Primary
public class InitializerHibernate implements Initializer {
  List<String> groups;
  List<String> students;
  Map<String, String> courses;
  @Autowired
  StudentInitializer studentInitializerImpl;
  @Autowired
  CoursesInitializer coursesInitializerImpl;
  @Autowired
  GroupInitializer groupIntitializerImpl;
  @Autowired
  GroupsDataHibernatePopulator groupsDataHibernatePopulator;
  @Autowired
  CoursesDataHibernatePopulator coursesDataHibernatePopulator;
  @Autowired
  StudentsDataHibernatePopulator studentsDataHibernatePopulator;
  @Autowired
  StudentsDaoHibernate studentsDaoHibernate;
  @Autowired
  StudentsToCoursesHibernatePopulator manyToManyPopulator;
  @Autowired
  DatabaseCleaner cleaner;

  public InitializerHibernate(StudentInitializer studentInitializerImpl,
      CoursesInitializer coursesInitializerImpl,
      GroupInitializer groupIntitializerImpl) {
    super();
    this.studentInitializerImpl = studentInitializerImpl;
    this.coursesInitializerImpl = coursesInitializerImpl;
    this.groupIntitializerImpl = groupIntitializerImpl;
  }

  @EventListener
  @Override
  public void initializeTablesEvent(InitializationEvent event) {
    initializeTables();
  }

  public void initializeTables() {
    cleaner.clearAllTables();
    generateStudentData();
    fillGroupsTable(groups);
    fillStudentsTable(students, groups);
    fillCourseTable(courses);
    assignStudentsToCourses(courses);

  }

  private void generateStudentData() {
    groups = groupIntitializerImpl.generateGroups();
    students = studentInitializerImpl.generateStudents();
    courses = coursesInitializerImpl.generateCourses();
  }

  private void fillGroupsTable(List<String> groups) {
    groupsDataHibernatePopulator.populateGroups(groups);
  }

  private void fillCourseTable(Map<String, String> courses) {
    coursesDataHibernatePopulator.populateCourses(courses);
  }

  private void fillStudentsTable(List<String> students, List<String> groups) {
    Map<String, List<String>> studentsInEachGroup = groupIntitializerImpl
        .assignStudentsToGroups(students, groups);
    studentsDataHibernatePopulator.populateStudents(studentsInEachGroup);
  }

  private void assignStudentsToCourses(Map<String, String> courses) {
    List<String> courseList = coursesInitializerImpl
        .retrieveCoursesNames(courses);
    Map<String, String> studentIds = studentsDaoHibernate.studentNamesById();
    Map<String, List<String>> studentIdAndHisCourses = coursesInitializerImpl
        .assignStudentsIdToCourse(studentIds, courseList);
    manyToManyPopulator.createManyToManyTable(studentIdAndHisCourses);
  }

}
