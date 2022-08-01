package com.kalachev.task7.hibernate;

import com.kalachev.task7.dao.entities.Course;
import com.kalachev.task7.dao.entities.Student;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateStudy {

  public static void main(String[] args) {

    Course course = new Course();
    course.setCourseDescription("discr");
    course.setCourseName("course 1");

    Student leha = new Student();
    leha.setFirstName("leha");
    leha.setLastName("Lehov");
    leha.setGroupId(3);

    course.getStudents().add(leha);
    leha.getCourses().add(course);

    Transaction transaction = null;

    Configuration configuration = new Configuration().configure()
        .addAnnotatedClass(Student.class).addAnnotatedClass(Course.class);

    ServiceRegistry registry = new StandardServiceRegistryBuilder()
        .applySettings(configuration.getProperties()).build();

    SessionFactory factory = configuration.buildSessionFactory(registry);

    try (Session session = factory.openSession()) {
      transaction = session.beginTransaction();
      session.save(leha);
      session.save(course);
      transaction.commit();
    }

  }

}
