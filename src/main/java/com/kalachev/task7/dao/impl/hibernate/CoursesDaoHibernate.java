package com.kalachev.task7.dao.impl.hibernate;

import java.util.List;

import com.kalachev.task7.dao.CoursesDao;
import com.kalachev.task7.dao.entities.Course;
import com.kalachev.task7.dao.entities.Student;
import com.kalachev.task7.utilities.HibernateUtills;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Component
public class CoursesDaoHibernate implements CoursesDao {

  @Override
  public boolean addStudent(int studentId, String course) {
    Transaction transaction = null;

    try (Session session = HibernateUtills.getSessionFactory().openSession()) {
      transaction = session.beginTransaction();
      Student student = session.get(Student.class, studentId);
      Course courseToAdd = session.get(Course.class, course);
      student.getCourses().add(courseToAdd);
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
    }

    return true;
  }

  @Override
  public boolean removeStudent(int studentId, String course) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public List<Course> getAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Course> getById(int studentId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isExists(String course) {
    // TODO Auto-generated method stub
    return false;
  }

}
