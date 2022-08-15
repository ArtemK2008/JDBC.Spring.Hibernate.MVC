package com.kalachev.task7.hibernate;

import java.util.List;

import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.dao.entities.Group;
import com.kalachev.task7.dao.impl.hibernate.GroupsDaoHibernate;
import com.kalachev.task7.initialization.impl.hibernate.InitializerHibernate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class HibernateStudy {

  public static void main(String[] args) {

    ApplicationContext context = new AnnotationConfigApplicationContext(
        ConsoleAppConfig.class);

    InitializerHibernate test = (InitializerHibernate) context
        .getBean("initializerHibernate");
    test.initializeTables();
    GroupsDaoHibernate groupsDaoHibernate = (GroupsDaoHibernate) context
        .getBean("groupsDaoHibernate");

    List<Group> groups = groupsDaoHibernate.findBySize(5);
    System.out.println();
    List<Group> groups1 = groupsDaoHibernate.findBySize(15);
    System.out.println();
    List<Group> groups2 = groupsDaoHibernate.findBySize(25);
    System.out.println();
    List<Group> groups3 = groupsDaoHibernate.findBySize(35);
    System.out.println();
    List<Group> groups4 = groupsDaoHibernate.findBySize(17);

    ((ConfigurableApplicationContext) context).close();

  }

}
