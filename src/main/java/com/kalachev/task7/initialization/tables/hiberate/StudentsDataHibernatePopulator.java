package com.kalachev.task7.initialization.tables.hiberate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;

import com.kalachev.task7.dao.entities.Group;
import com.kalachev.task7.dao.entities.Student;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StudentsDataHibernatePopulator {

  @Autowired
  SessionFactory sessionFactory;

  public void populateStudents(
      Map<String, List<String>> groupsWithItsStudents) {
    Map<Integer, List<String>> allStudentsByGroupId = mapToIdInstead(
        groupsWithItsStudents);

    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      for (Entry<Integer, List<String>> entry : allStudentsByGroupId
          .entrySet()) {
        for (String s : entry.getValue()) {
          Student student = new Student();
          student.setGroupId(entry.getKey());
          student.setFirstName(retrieveFirstName(s));
          student.setLastName(retrieveLastName(s));
          session.save(student);
          session.flush();
        }
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, Integer> retrieveGroups() {
    Map<String, Integer> groupNamesById = new HashMap<>();
    List<Group> groups = new ArrayList<>();
    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      Query q = session.createQuery("FROM hgroups");
      groups = q.getResultList();
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
    for (Group g : groups) {
      groupNamesById.put(g.getGroupName(), g.getId());
    }
    return groupNamesById;
  }

  private Map<Integer, List<String>> mapToIdInstead(
      Map<String, List<String>> groupsWithItsStudents) {
    Map<String, Integer> groupNamesById = retrieveGroups();
    Map<Integer, List<String>> allStudentsByGroupId = new HashMap<>();
    for (Entry<String, List<String>> entry : groupsWithItsStudents.entrySet()) {
      int groupId = groupNamesById.get(entry.getKey());
      allStudentsByGroupId.put(groupId, entry.getValue());
    }
    return allStudentsByGroupId;
  }

  private String retrieveFirstName(String student) {
    int spaceIndex = student.indexOf(' ');
    return student.substring(0, spaceIndex);
  }

  private String retrieveLastName(String student) {
    int spaceIndex = student.indexOf(' ');
    return student.substring(spaceIndex + 1);
  }

}
