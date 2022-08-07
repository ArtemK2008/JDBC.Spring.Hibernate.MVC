package com.kalachev.task7.initialization.tables.hiberate;

import java.util.Map;
import java.util.Map.Entry;

import com.kalachev.task7.dao.entities.Course;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoursesDataHibernatePopulator {

  @Autowired
  SessionFactory sessionFactory;

  public void populateCourses(Map<String, String> courses) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      for (Entry<String, String> entry : courses.entrySet()) {
        Course course = new Course();
        course.setCourseName(entry.getKey());
        course.setCourseDescription(entry.getValue());
        session.save(course);
      }

      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
  }
}
