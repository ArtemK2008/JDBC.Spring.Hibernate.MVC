package com.kalachev.task7.utilities;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@PropertySource("classpath:DbProperties")
@Service
public class ConnectionManager {

  static String dbUrl;
  static String dbUsername;
  static String dbPassword;

  @Value("${URL}")
  public void setDbUrl(String url) {
    ConnectionManager.dbUrl = url;
  }

  @Value("${NAME}")
  public void setDbUsername(String name) {
    ConnectionManager.dbUsername = name;
  }

  @Value("${PASSWORD}")
  public void setDbPassword(String password) {
    ConnectionManager.dbPassword = password;
  }

  private ConnectionManager() {
    super();
  }

  public static Connection openDbConnection() {
    Connection connection = null;
    try {
      DriverManager.registerDriver(new org.postgresql.Driver());
      connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    } catch (Exception e) {
      System.out.println("Error while opening Connection");
    }
    return connection;
  }

}