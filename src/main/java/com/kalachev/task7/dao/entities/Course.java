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
import javax.persistence.ManyToMany;

@Entity(name = "hcourses")
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "course_id", nullable = false)
  private int id;
  @Column(name = "course_name")
  private String courseName;
  @Column(name = "course_description")
  private String courseDescription;

  @ManyToMany(mappedBy = "courses", fetch = FetchType.EAGER)
  private Set<Student> students = new HashSet<>();

  public Course() {
    super();
  }

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

  public Set<Student> getStudents() {
    return students;
  }

  public void setStudents(Set<Student> students) {
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
