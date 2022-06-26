package com.kalachev.task7.initialization.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.kalachev.task7.dao.StudentsDao;
import com.kalachev.task7.dao.impl.core.StudentsDaoImpl;
import com.kalachev.task7.events.initializationEvent;
import com.kalachev.task7.initialization.CoursesInitializer;
import com.kalachev.task7.initialization.GroupInitializer;
import com.kalachev.task7.initialization.Initializer;
import com.kalachev.task7.initialization.SchemaInitializer;
import com.kalachev.task7.initialization.StudentInitializer;
import com.kalachev.task7.initialization.tables.CoursesDataDbPopulator;
import com.kalachev.task7.initialization.tables.GroupsDataDbPopulator;
import com.kalachev.task7.initialization.tables.StudentsDataDbPopulator;
import com.kalachev.task7.initialization.tables.StudentsToCoursesDataDbPopulator;

@Component
public class InitializerImpl implements Initializer {
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
  SchemaInitializer schemaInitializerImpl;

  public InitializerImpl(StudentInitializer studentInitializerImpl,
      CoursesInitializer coursesInitializerImpl,
      GroupInitializer groupIntitializerImpl,
      SchemaInitializer schemaInitializerImpl) {
    super();
    this.studentInitializerImpl = studentInitializerImpl;
    this.coursesInitializerImpl = coursesInitializerImpl;
    this.groupIntitializerImpl = groupIntitializerImpl;
    this.schemaInitializerImpl = schemaInitializerImpl;
  }

  @EventListener
  @Override
  public void initializeTablesEvent(initializationEvent event) {
    initializeTables();
  }

  public void initializeTables() {
    initializeStartTables();
    generateStudentData();
    fillGroupsTable(groups);
    fillStudentsTable(students, groups);
    fillCourseTable(courses);
    fillTempManyToManyTable(courses);
    createStudentsCoursesTable();
  }

  private void generateStudentData() {
    groups = groupIntitializerImpl.generateGroups();
    students = studentInitializerImpl.generateStudents();
    courses = coursesInitializerImpl.generateCourses();
  }

  private void initializeStartTables() {
    SchemaInitializer schema = new SchemaInitializerImpl();
    schema.createSchema();
  }

  private void fillStudentsTable(List<String> students, List<String> groups) {
    GroupInitializer groupInitializerImpl = new GroupInitializerImpl();
    Map<String, List<String>> studentsInEachGroup = groupInitializerImpl
        .assignStudentsToGroups(students, groups);
    StudentsDataDbPopulator filler = new StudentsDataDbPopulator();
    filler.populateStudents(studentsInEachGroup);
  }

  private void fillGroupsTable(List<String> groups) {
    GroupsDataDbPopulator filler = new GroupsDataDbPopulator();
    filler.populateGroups(groups);
  }

  private void fillCourseTable(Map<String, String> courses) {
    CoursesDataDbPopulator filler = new CoursesDataDbPopulator();
    filler.populateCourses(courses);
  }

  private void fillTempManyToManyTable(Map<String, String> courses) {
    List<String> courseList = coursesInitializerImpl
        .retrieveCoursesNames(courses);
    StudentsDao studentsDao = new StudentsDaoImpl();
    Map<String, String> studentIds = studentsDao.studentNamesById();
    Map<String, List<String>> studentIdAndHisCourses = coursesInitializerImpl
        .assignStudentsIdToCourse(studentIds, courseList);
    StudentsToCoursesDataDbPopulator filler = new StudentsToCoursesDataDbPopulator();
    filler.createManyToManyTable(studentIdAndHisCourses);
  }

  private void createStudentsCoursesTable() {
    StudentsToCoursesDataDbPopulator filler = new StudentsToCoursesDataDbPopulator();
    filler.createStudentsCoursesFullTable();
  }

}
