package com.kalachev.task7.startup;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.ui.menu.ConsoleMenu;

public class AppRunner {

  public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(
        ConsoleAppConfig.class);
    ConsoleMenu menu = (ConsoleMenu) context.getBean("consoleMenu");
    menu.runSchoolApp();
    ((ConfigurableApplicationContext) context).close();

  }
}
