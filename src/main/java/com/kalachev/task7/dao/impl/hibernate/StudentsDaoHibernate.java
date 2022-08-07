package com.kalachev.task7.dao.impl.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.kalachev.task7.dao.StudentsDao;
import com.kalachev.task7.dao.entities.Course;
import com.kalachev.task7.dao.entities.Student;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class StudentsDaoHibernate implements StudentsDao {

  @Autowired
  SessionFactory sessionFactory;

  @Override
  public Map<String, String> studentNamesById() {
    Map<String, String> studentById = new HashMap<>();
    List<Student> students = new ArrayList<>();

    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      students = session.createQuery("SELECT a FROM hstudents a", Student.class)
          .getResultList();
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    for (Student s : students) {
      String id = String.valueOf(s.getId());
      String name = s.getFirstName() + " " + s.getLastName();
      studentById.put(id, name);
    }
    return studentById;
  }

  @Override
  public List<Student> findByCourse(String courseName) {
    List<Student> students = new ArrayList<>();
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      Query query = session
          .createQuery("from hcourses where course_name=:course");
      query.setParameter("course", courseName);
      Course course = (Course) query.getSingleResult();
      students.addAll(course.getStudents());
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return students;
  }

  @Override
  public boolean insert(String firstName, String lastName, int groupId) {
    boolean isAdded = false;
    Transaction transaction = null;
    Student student = new Student();
    student.setFirstName(firstName);
    student.setLastName(lastName);
    student.setGroupId(groupId);
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      session.save(student);
      isAdded = true;
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return isAdded;
  }

  @Override
  public boolean delete(int id) {
    boolean isDeleted = false;
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      Student student = session.get(Student.class, id);
      session.delete(student);
      isDeleted = true;
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return isDeleted;
  }

  @Override
  public boolean isExistsInGroup(String firstName, String lastName,
      int groupId) {
    boolean isExist = false;
    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();

      Query query = session
          .createQuery("from hstudents where first_name=:firstname "
              + "and last_name=:lastname and group_id=:groupid");
      query.setParameter("firstname", firstName);
      query.setParameter("lastname", lastName);
      query.setParameter("groupid", groupId);
      if (!query.getResultList().isEmpty()) {
        isExist = true;
      }
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return isExist;
  }

  @Override
  public boolean isIdExists(int id) {
    boolean isExist = false;
    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();

      Query query = session.createQuery("from hstudents where student_id=:id");
      query.setParameter("id", id);
      if (!query.getResultList().isEmpty()) {
        isExist = true;
      }
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return isExist;
  }

  @Override
  public boolean checkIfStudentInCourse(int studentId, String course) {
    boolean isExist = false;
    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      Student student = session.get(Student.class, studentId);
      Query query = session
          .createQuery("from hcourses where course_name=:course");
      query.setParameter("course", course);

      if (!query.getResultList().isEmpty()) {
        Course currCourse = (Course) query.getSingleResult();
        if (student.getCourses().contains(currCourse)) {
          isExist = true;
        }
      }
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return isExist;
  }

}
