package com.kalachev.task7.dao.impl.hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kalachev.task7.dao.GroupsDao;
import com.kalachev.task7.dao.entities.Group;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class GroupsDaoHibernate implements GroupsDao {

  @Autowired
  SessionFactory sessionFactory;

  @Override
  public List<Group> findBySize(int size) {
    Transaction transaction = null;
    List<Group> groups = new ArrayList<>();
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      groups = session.createQuery("SELECT g FROM hgroups g", Group.class)
          .getResultList();
      groups = groups.stream().filter(g -> g.getStudents().size() >= size)
          .collect(Collectors.toList());
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return groups;
  }

}
