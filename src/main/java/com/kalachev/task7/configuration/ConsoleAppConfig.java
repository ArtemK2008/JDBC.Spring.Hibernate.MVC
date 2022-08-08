
package com.kalachev.task7.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.kalachev.task7.ComponentScanInterface;
import com.kalachev.task7.service.CoursesOptions;
import com.kalachev.task7.service.GroupOptions;
import com.kalachev.task7.service.StudentOptions;
import com.kalachev.task7.ui.commands.Command;
import com.kalachev.task7.ui.commands.impl.AddStudentCommand;
import com.kalachev.task7.ui.commands.impl.AddToCourseCommand;
import com.kalachev.task7.ui.commands.impl.DeleteByIdCommand;
import com.kalachev.task7.ui.commands.impl.ExitCommand;
import com.kalachev.task7.ui.commands.impl.FindStudentsByCourseCommand;
import com.kalachev.task7.ui.commands.impl.GroupSizeCommand;
import com.kalachev.task7.ui.commands.impl.RemoveFromCourseCommand;

@Configuration
@PropertySource("classpath:DbProperties")
@ComponentScan(basePackageClasses = {
    ComponentScanInterface.class }, excludeFilters = {
        @Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class) })
public class ConsoleAppConfig {

  @Bean
  public Map<String, Command> commands(StudentOptions studentOptions,
      GroupOptions groupOptions, CoursesOptions coursesOptions,
      Scanner scanner) {
    Map<String, Command> commands = new LinkedHashMap<>();
    commands.put("1", new GroupSizeCommand(scanner, groupOptions));
    commands.put("2", new FindStudentsByCourseCommand(scanner, coursesOptions,
        studentOptions));
    commands.put("3", new AddStudentCommand(scanner, studentOptions));
    commands.put("4", new DeleteByIdCommand(scanner, studentOptions));
    commands.put("5", new AddToCourseCommand(scanner, coursesOptions));
    commands.put("6", new RemoveFromCourseCommand(scanner, coursesOptions));
    commands.put("7", new ExitCommand(scanner));
    return commands;
  }

  @Autowired
  Environment env;

  @Bean
  public BasicDataSource dataSource() {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName(env.getProperty("JDBC_DRIVER"));
    ds.setUrl(env.getProperty("URL"));
    ds.setUsername(env.getProperty("NAME"));
    ds.setPassword(env.getProperty("PASSWORD"));
    ds.setInitialSize(5);
    ds.setMaxTotal(10);
    return ds;
  }

  @Bean
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(dataSource());
  }

  @Bean
  public Scanner scanner() {
    return new Scanner(System.in);
  }
}
