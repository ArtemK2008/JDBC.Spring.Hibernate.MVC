package com.kalachev.task7.dao.entities;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity(name = "hgroups")
public class Group {

  @Id
  @SequenceGenerator(name = "groups_seq", sequenceName = "groups_sequence", initialValue = 1, allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups_seq")
  @Column(name = "group_id", nullable = false)
  private int id;
  @Column(name = "group_name")
  private String groupName;

  @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
  private Set<Student> students;

  public Group() {
    super();
  }

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

  public Set<Student> getStudents() {
    return students;
  }

  public void setStudents(Set<Student> students) {
    this.students = students;
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
