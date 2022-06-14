package com.kalachev.task7.dao;

import java.util.List;

import com.kalachev.task7.dao.entities.Group;

public interface GroupsDao {
  List<Group> findBySize(int size);
}