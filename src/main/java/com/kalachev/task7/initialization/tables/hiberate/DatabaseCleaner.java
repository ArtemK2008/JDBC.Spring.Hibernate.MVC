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
    clearAllTables();
  }

  public void clearAllTables() {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.createNativeQuery("DELETE FROM hstudents_hcourses")
          .executeUpdate();
      session.createNativeQuery("DELETE FROM hstudents").executeUpdate();
      session.createNativeQuery("DELETE FROM hcourses").executeUpdate();
      session.createNativeQuery("DELETE FROM hgroups").executeUpdate();
      session
          .createNativeQuery("ALTER SEQUENCE student_sequence RESTART WITH 1")
          .executeUpdate();
      session.createNativeQuery("ALTER SEQUENCE groups_sequence RESTART WITH 1")
          .executeUpdate();
      session
          .createNativeQuery("ALTER SEQUENCE courses_sequence RESTART WITH 1")
          .executeUpdate();

      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
  }
}
