//configuration of files
// src/
//  └── main/
//      ├── java/
//      │    └── com/example/
//      │         ├── config/
//      │         │    └── AppConfig.java
//      │         ├── model/
//      │         │    ├── Course.java
//      │         │    └── Student.java
//      │         └── MainApp.java

//course.java
package com.example.model;

public class Course {
    private String courseName;
    private int duration; // in weeks

    public Course(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseName='" + courseName + '\'' +
                ", duration=" + duration + " weeks" +
                '}';
    }
}

//Student.java
package com.example.model;

public class Student {
    private String name;
    private Course course;

    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", course=" + course +
                '}';
    }
}

//AppConfig.java
package com.example.config;

import com.example.model.Course;
import com.example.model.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Course course() {
        return new Course("Spring Framework", 6);
    }

    @Bean
    public Student student() {
        return new Student("Aashish Pant", course());
    }
}


//MainApp.java
package com.example;

import com.example.config.AppConfig;
import com.example.model.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Student student = context.getBean(Student.class);
        System.out.println(student);
    }
}


//Output
//Student{name='Aashish Pant', course=Course{courseName='Spring Framework', duration=6 weeks}}

