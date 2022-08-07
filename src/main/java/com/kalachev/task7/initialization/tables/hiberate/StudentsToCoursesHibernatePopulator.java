package com.kalachev.task7.initialization.tables.hiberate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;

import com.kalachev.task7.dao.entities.Course;
import com.kalachev.task7.dao.entities.Student;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentsToCoursesHibernatePopulator {

  @Autowired
  SessionFactory sessionFactory;

  public void createManyToManyTable(
      Map<String, List<String>> coursesOfStudent) {
    Transaction transaction = null;
    Map<Student, List<Course>> converteMap = prepareMamyToManyData(
        coursesOfStudent);
    for (Entry<Student, List<Course>> entry : converteMap.entrySet()) {
      for (Course c : entry.getValue()) {
        Student currStudent = entry.getKey();
        currStudent.getCourses().add(c);
        c.getStudents().add(currStudent);
        try (Session session = sessionFactory.openSession()) {
          transaction = session.beginTransaction();
          session.saveOrUpdate(entry.getKey());
          session.saveOrUpdate(c);
          transaction.commit();
        } catch (Exception e) {
          e.printStackTrace();
          if (transaction != null) {
            transaction.rollback();
          }
        }
      }
    }
  }

  private Map<Student, List<Course>> prepareMamyToManyData(
      Map<String, List<String>> coursesOfStudent) {
    return convertValueMapPart(convertKeyMapPart(coursesOfStudent));
  }

  private Map<Student, List<String>> convertKeyMapPart(
      Map<String, List<String>> coursesOfStudent) {
    Transaction transaction = null;
    Map<Student, List<String>> convertedMap = new HashMap<>();

    for (Entry<String, List<String>> entry : coursesOfStudent.entrySet()) {
      int id = Integer.parseInt(entry.getKey());
      try (Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();
        Student tempStudent = session.get(Student.class, id);
        convertedMap.put(tempStudent, entry.getValue());
        transaction.commit();
      } catch (Exception e) {
        e.printStackTrace();
        if (transaction != null) {
          transaction.rollback();
        }
      }
    }
    return convertedMap;
  }

  private Map<Student, List<Course>> convertValueMapPart(
      Map<Student, List<String>> coursesOfStudent) {
    Map<Student, List<Course>> convertedMap = new HashMap<>();
    for (Entry<Student, List<String>> entry : coursesOfStudent.entrySet()) {
      List<Course> converted = convertDataType(entry.getValue());
      convertedMap.put(entry.getKey(), converted);
    }
    return convertedMap;
  }

  private List<Course> convertDataType(List<String> courses) {
    Transaction transaction = null;
    List<Course> resultList = new ArrayList<>();
    for (String course : courses) {
      try (Session session = sessionFactory.openSession()) {
        transaction = session.beginTransaction();
        Query query = session
            .createQuery("from hcourses where course_name=:course");
        query.setParameter("course", course);
        Course tempCourse = (Course) query.getSingleResult();
        resultList.add(tempCourse);
        transaction.commit();
      } catch (Exception e) {
        e.printStackTrace();
        if (transaction != null) {
          transaction.rollback();
        }
      }
    }
    return resultList;
  }

}
