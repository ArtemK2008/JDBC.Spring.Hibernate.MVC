package com.kalachev.task7.initialization.tables.hiberate;

import com.kalachev.task7.events.DatabaseCleanEvent;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {
  @Autowired
  SessionFactory sessionFactory;

  @EventListener
  public void initializeTablesEvent(DatabaseCleanEvent event) {
    dropAllTables();
  }

  public void dropAllTables() {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.createNativeQuery(
          "DROP TABLE IF EXISTS hstudents,hcourses,hgroups,hstudents_hcourses CASCADE")
          .executeUpdate();
      session.createNativeQuery(
          "DROP TABLE IF EXISTS students,courses,groups,students_courses,studentscoursesdata CASCADE")
          .executeUpdate();

      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
  }
}
