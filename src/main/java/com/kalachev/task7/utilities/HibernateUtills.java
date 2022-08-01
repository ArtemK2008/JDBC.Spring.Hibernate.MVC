package com.kalachev.task7.utilities;

import com.kalachev.task7.dao.entities.Course;
import com.kalachev.task7.dao.entities.Group;
import com.kalachev.task7.dao.entities.Student;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtills {

  private HibernateUtills() {
    super();
  }

  public static SessionFactory getSessionFactory() {
    Configuration configuration = new Configuration().configure()
        .addAnnotatedClass(Student.class).addAnnotatedClass(Course.class)
        .addAnnotatedClass(Group.class);

    ServiceRegistry registry = new StandardServiceRegistryBuilder()
        .applySettings(configuration.getProperties()).build();

    return configuration.buildSessionFactory(registry);
  }

}
