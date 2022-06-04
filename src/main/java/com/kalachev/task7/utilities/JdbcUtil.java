package com.kalachev.task7.utilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.kalachev.task7.exceptions.DaoException;

public class JdbcUtil {

  private JdbcUtil() {
    super();
  }

  public static void closeAll(Statement statement, Connection connection)
      throws DaoException {
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
        String methodName = ExceptionsUtil.getCurrentMethodName();
        String className = ExceptionsUtil.getCurrentClassName();
        throw new DaoException(methodName, className);
      }
    }
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
        String methodName = ExceptionsUtil.getCurrentMethodName();
        String className = ExceptionsUtil.getCurrentClassName();
        throw new DaoException(methodName, className);
      }
    }
  }

  public static void closeAll(ResultSet rs, Statement statement,
      Connection connection) throws DaoException {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
        String methodName = ExceptionsUtil.getCurrentMethodName();
        String className = ExceptionsUtil.getCurrentClassName();
        throw new DaoException(methodName, className);
      }
    }
    if (statement != null) {
      try {
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
        String methodName = ExceptionsUtil.getCurrentMethodName();
        String className = ExceptionsUtil.getCurrentClassName();
        throw new DaoException(methodName, className);
      }
    }
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
        String methodName = ExceptionsUtil.getCurrentMethodName();
        String className = ExceptionsUtil.getCurrentClassName();
        throw new DaoException(methodName, className);
      }
    }
  }

}