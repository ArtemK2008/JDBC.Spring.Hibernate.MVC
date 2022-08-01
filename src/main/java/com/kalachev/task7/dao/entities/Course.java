package com.kalachev.task7.dao.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "courses")
public class Course {

  @Id
  @GeneratedValue
  @Column(name = "course_id")
  private int id;
  @Column(name = "course_name")
  private String courseName;
  @Column(name = "course_description")
  private String courseDescription;
  @ManyToMany(mappedBy = "courses")
  private List<Student> students = new ArrayList<>();

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCourseName() {
    return courseName;
  }

  public void setCourseName(String courseName) {
    this.courseName = courseName;
  }

  public String getCourseDescription() {
    return courseDescription;
  }

  public void setCourseDescription(String courseDescription) {
    this.courseDescription = courseDescription;
  }

  public List<Student> getStudents() {
    return students;
  }

  public void setStudents(List<Student> students) {
    this.students = students;
  }

  @Override
  public int hashCode() {
    return Objects.hash(courseName, id);
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
    Course other = (Course) obj;
    return Objects.equals(courseName, other.courseName) && id == other.id;
  }

  @Override
  public String toString() {
    return "Course [id=" + id + ", courseName=" + courseName
        + ", courseDescription=" + courseDescription + "]";
  }

}
