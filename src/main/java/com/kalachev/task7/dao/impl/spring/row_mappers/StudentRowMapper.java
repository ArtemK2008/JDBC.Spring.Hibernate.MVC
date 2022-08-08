package com.kalachev.task7.dao.impl.spring.row_mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kalachev.task7.dao.entities.Student;

public class StudentRowMapper implements RowMapper<Student> {

  @Override
  public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
    Student student = new Student();
    student.setId(rs.getInt("student_id"));
    student.setFirstName(rs.getString("first_name"));
    student.setLastName(rs.getString("last_name"));
    student.setGroupId(rs.getInt("group_id"));
    return student;
  }
}
