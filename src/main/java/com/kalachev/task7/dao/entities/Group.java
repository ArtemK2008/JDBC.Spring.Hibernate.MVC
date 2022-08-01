package com.kalachev.task7.dao.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "groups")
public class Group {

  @Id
  @GeneratedValue
  @Column(name = "group_id")
  private int id;
  @Column(name = "group_name")
  private String groupName;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupName, id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Group other = (Group) obj;
    return Objects.equals(groupName, other.groupName) && id == other.id;
  }

  @Override
  public String toString() {
    return "Group [id=" + id + ", groupName=" + groupName + "]";
  }

}
