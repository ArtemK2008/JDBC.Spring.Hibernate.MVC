package com.kalachev.task7.dao.impl.hibernate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.kalachev.task7.dao.CoursesDao;
import com.kalachev.task7.dao.entities.Course;
import com.kalachev.task7.dao.entities.Student;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class CoursesDaoHibernate implements CoursesDao {

  @Autowired
  SessionFactory sessionFactory;

  @Override
  public boolean addStudent(int studentId, String course) {
    Transaction transaction = null;
    boolean isAdded = false;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      Student student = session.get(Student.class, studentId);
      Query query = session
          .createQuery("from hcourses where course_name=:course");
      query.setParameter("course", course);
      Course courseToAdd = (Course) query.getSingleResult();
      student.getCourses().add(courseToAdd);
      courseToAdd.getStudents().add(student);
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

  public Student getStudent(int studentId) {
    Transaction transaction = null;
    Student student = new Student();
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      student = session.get(Student.class, studentId);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return student;
  }

  @Override
  public boolean removeStudent(int studentId, String course) {
    Transaction transaction = null;
    boolean isRemoved = false;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      Student student = session.get(Student.class, studentId);
      Query query = session
          .createQuery("from hcourses where course_name=:course");
      query.setParameter("course", course);
      Course courseToRemove = (Course) query.getSingleResult();
      student.getCourses().remove(courseToRemove);
      courseToRemove.getStudents().remove(student);
      isRemoved = true;
      transaction.commit();
    } catch (Exception e) {
      e.printStackTrace();
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return isRemoved;
  }

  @Override
  public List<Course> getAll() {
    List<Course> courses = new ArrayList<>();
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      courses = session.createQuery("SELECT c FROM hcourses c", Course.class)
          .getResultList();
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return courses;
  }

  @Override
  public List<Course> getById(int studentId) {
    List<Course> courses = new ArrayList<>();
    Transaction transaction = null;
    Student student = new Student();
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      student = session.get(Student.class, studentId);
      courses.addAll(student.getCourses());
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }
    return courses;
  }

  @Override
  public boolean isExists(String course) {
    boolean isExist = false;
    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();

      Query query = session
          .createQuery("from hcourses where course_name=:course");
      query.setParameter("course", course);
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

}
