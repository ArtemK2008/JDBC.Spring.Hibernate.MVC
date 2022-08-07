package com.kalachev.task7.dao.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity(name = "hstudents")
public class Student {
  @Id
  @SequenceGenerator(name = "student_seq", sequenceName = "student_sequence", initialValue = 1, allocationSize = 5)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_seq")
  @Column(name = "student_id")
  private int id;
  @Column(name = "group_id")
  private int groupId;
  @Column(name = "first_name")
  private String firstName;
  @Column(name = "last_name")
  private String lastName;
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "hstudents_hcourses", joinColumns = {
      @JoinColumn(name = "student_id") }, inverseJoinColumns = {
          @JoinColumn(name = "course_id") })
  private Set<Course> courses = new HashSet<>();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "group_id", nullable = false, updatable = false, insertable = false)
  private Group group;

  public Student() {
    super();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Set<Course> getCourses() {
    return courses;
  }

  public void setCourses(Set<Course> courses) {
    this.courses = courses;
  }

  public Group getGroup() {
    return group;
  }

  public void setGroup(Group group) {
    this.group = group;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
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
    Student other = (Student) obj;
    return id == other.id;
  }

  @Override
  public String toString() {
    return "Student [id=" + id + ", groupdId=" + groupId + ", firstName="
        + firstName + ", lastName=" + lastName + "]";
  }

}
