package com.kalachev.task7.dao;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.kalachev.task7.configuration.ConsoleAppConfig;
import com.kalachev.task7.dao.entities.Group;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ConsoleAppConfig.class)
class GroupDaoTest extends DbUnitConfig {
  @Autowired
  GroupsDao groupsDao;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    beforeData = new FlatXmlDataSetBuilder()
        .build(new FileInputStream(getClass().getClassLoader()
            .getResource("dao/group/ActualGroupDataSet.xml").getFile()));
    databaseTester.setDataSet(beforeData);
    databaseTester.onSetup();
  }

  @Test
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
