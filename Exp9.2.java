//Program Structure
// src/
//  └── main/
//      ├── java/
//      │    └── com/example/
//      │         ├── config/
//      │         │    └── HibernateUtil.java
//      │         ├── model/
//      │         │    └── Student.java
//      │         └── MainApp.java
//      └── resources/
//          └── hibernate.cfg.xml

//hibernate.cfg.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>

        <!-- DB Connection -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/student_db</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">your_password</property>

        <!-- Hibernate Settings -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="hibernate.show_sql">true</property>

        <!-- Entity Mapping -->
        <mapping class="com.example.model.Student" />

    </session-factory>
</hibernate-configuration>

//Student.java
package com.example.model;

import javax.persistence.*;

@Entity
@Table(name = "student")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private int age;

    public Student() {}

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and Setters
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', age=" + age + '}';
    }
}

//HibernateUtil.java
package com.example.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration()
                                .configure() // loads hibernate.cfg.xml
                                .buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}

//MainApp.java
package com.example;

import com.example.config.HibernateUtil;
import com.example.model.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MainApp {
    public static void main(String[] args) {
        // CREATE
        Student s1 = new Student("Aashish", 21);
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(s1);
        tx.commit();
        session.close();
        System.out.println("Saved: " + s1);

        // READ
        session = HibernateUtil.getSessionFactory().openSession();
        Student retrieved = session.get(Student.class, s1.getId());
        System.out.println("Retrieved: " + retrieved);
        session.close();

        // UPDATE
        session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
        retrieved.setAge(22);
        session.update(retrieved);
        tx.commit();
        session.close();
        System.out.println("Updated: " + retrieved);

        // DELETE
        session = HibernateUtil.getSessionFactory().openSession();
        tx = session.beginTransaction();
        session.delete(retrieved);
        tx.commit();
        session.close();
        System.out.println("Deleted: " + retrieved);
    }
}

