package com.kalachev.task7.dao.impl.spring;

import java.util.List;

import com.kalachev.task7.dao.GroupsDao;
import com.kalachev.task7.dao.entities.Group;
import com.kalachev.task7.dao.impl.spring.row_mappers.GroupRowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

@Component
public class GroupDaoSpring implements GroupsDao {
  @Autowired
  JdbcOperations jdbcOperations;

  public GroupDaoSpring(JdbcOperations jdbcOperations) {
    super();
    this.jdbcOperations = jdbcOperations;
  }

  private static final String FIND_GROUP_BY_SIZE = "SELECT g.group_id,g.group_name FROM Students"
      + " as s " + "INNER JOIN Groups as g " + "ON s.group_id = g.group_id "
      + "GROUP BY g.group_id,g.group_name " + "HAVING COUNT (s.group_id) >=(?)";

  @Override
  public List<Group> findBySize(int size) {
    return jdbcOperations.query(FIND_GROUP_BY_SIZE, new GroupRowMapper(), size);
  }

}
