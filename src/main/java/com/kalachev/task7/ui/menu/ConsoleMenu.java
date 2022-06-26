package com.kalachev.task7.ui.menu;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.kalachev.task7.events.initializationEvent;
import com.kalachev.task7.initialization.Initializer;
import com.kalachev.task7.ui.commands.Command;

@Component
public class ConsoleMenu {
  @Autowired
  private ApplicationEventPublisher publisher;
  static final String BAD_INPUT = "Your Input was not correct";
  Scanner scanner;
  @Autowired
  Initializer initializerImpl;
  Map<String, Command> commands;

  public ConsoleMenu(Scanner scanner, Initializer initializerImpl) {
    super();
    this.scanner = scanner;
    this.initializerImpl = initializerImpl;
  }

  @Resource
  public void setCommands(Map<String, Command> commands) {
    this.commands = commands;
  }

  private static String[] options = {
      "1: Find all groups with less or equals student count",
      "2: Find all students related to course with given name",
      "3: Add new student", "4: Delete student by ID",
      "5: Add a student to the course (from a list)",
      "6: Remove the student from one of his or her courses", "7: Exit" };

  public void runSchoolApp() {
    cleanConsole();
    publisher.publishEvent(new initializationEvent(this));
    String option = "1";
    while (!"7".equals(option)) {
      printMenu(options);
      try {
        option = scanner.next();
        validateOption(option);
        handleOptions(option);
      } catch (InputMismatchException e) {
        System.out.println(
            "Please enter an integer value between 1 and " + options.length);
      } catch (Exception ex) {
        System.out.println("An unexpected error happened. Please try again");
      } finally {
        scanner.nextLine();
        pressEnterMessage();
        cleanConsole();
      }
    }
  }

  private void handleOptions(String option) {
    commands.get(option).execute();
  }

  private void pressEnterMessage() {
    System.out.println("press \"ENTER\" to continue...");
    scanner.nextLine();
  }

  private void validateOption(String option) {
    if (Integer.parseInt(option) < 1 || Integer.parseInt(option) > 7) {
      throw new InputMismatchException();
    }
  }

  private static void printMenu(String[] options) {
    for (String option : options) {
      System.out.println(option);
    }
    System.out.println("Choose your option : ");
  }

  public void cleanConsole() {
    for (int i = 0; i < 100; i++) {
      System.out.println();
    }
  }
}
