package com.kalachev.task7.dao;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.dao.entities.Group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
class GroupDaoTest {

  @Autowired
  GroupsDao groupsDao;

  @Test
  @DatabaseSetup("/dao/group/ActualGroupDataSetHibernate.xml")
  void testFindBySize_shouldReturnGroupsWithAtLeastThreeStudent_whenCalledWithValidTables() {
    // given
    List<Group> expected = new LinkedList<>();
    Group group = new Group();
    group.setGroupName("aa-11");
    group.setId(1);
    expected.add(group);
    group = new Group();
    group.setGroupName("aa-22");
    group.setId(2);
    expected.add(group);
    // when
    List<Group> actual = groupsDao.findBySize(3);
    // then
    assertTrue(actual.size() == expected.size() && actual.containsAll(expected)
        && expected.containsAll(actual));
  }
}
