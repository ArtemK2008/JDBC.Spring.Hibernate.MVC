package com.kalachev.task7.initialization.tables.hiberate;

import java.util.List;

import com.kalachev.task7.dao.entities.Group;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupsDataHibernatePopulator {

  @Autowired
  SessionFactory sessionFactory;

  public void populateGroups(List<String> groups) {
    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      for (String group : groups) {
        Group g = new Group();
        g.setGroupName(group);
        session.save(g);
      }
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
  }
}
