//Folder Structure
// src/
//  └── main/
//      ├── java/
//      │    └── com/example/bank/
//      │         ├── config/
//      │         │    └── AppConfig.java
//      │         ├── model/
//      │         │    ├── Account.java
//      │         │    └── Transaction.java
//      │         ├── service/
//      │         │    └── BankService.java
//      │         └── MainApp.java
//      └── resources/
//          └── hibernate.cfg.xml

//hibernate.cfg.xml
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/bank_db</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">your_password</property>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="hibernate.show_sql">true</property>

        <mapping class="com.example.bank.model.Account"/>
        <mapping class="com.example.bank.model.Transaction"/>
    </session-factory>
</hibernate-configuration>

//AppConfig.java
package com.example.bank.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.example.bank")
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration().configure().buildSessionFactory();
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }
}

//Account.java
package com.example.bank.model;

import javax.persistence.*;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String holderName;
    private double balance;

    public Account() {}
    public Account(String holderName, double balance) {
        this.holderName = holderName;
        this.balance = balance;
    }

    // Getters & setters...
    public int getId() { return id; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Account{id=" + id + ", holderName='" + holderName + "', balance=" + balance + '}';
    }
}

//Transaction.java
package com.example.bank.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int fromAccountId;
    private int toAccountId;
    private double amount;
    private Date timestamp = new Date();

    public Transaction() {}
    public Transaction(int from, int to, double amount) {
        this.fromAccountId = from;
        this.toAccountId = to;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{from=" + fromAccountId + ", to=" + toAccountId +
               ", amount=" + amount + ", at=" + timestamp + "}";
    }
}

//BankService.java
package com.example.bank.service;

import com.example.bank.model.Account;
import com.example.bank.model.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankService {

    @Autowired
    private SessionFactory sessionFactory;

    public Account createAccount(String name, double initialBalance) {
        Session session = sessionFactory.getCurrentSession();
        Account acc = new Account(name, initialBalance);
        session.save(acc);
        return acc;
    }

    @Transactional
    public void transfer(int fromId, int toId, double amount, boolean fail) {
        Session session = sessionFactory.getCurrentSession();
        Account from = session.get(Account.class, fromId);
        Account to = session.get(Account.class, toId);

        if (from.getBalance() < amount)
            throw new RuntimeException("Insufficient balance");

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        session.update(from);
        session.update(to);

        session.save(new Transaction(fromId, toId, amount));

        if (fail) {
            throw new RuntimeException("Forced failure to test rollback");
        }
    }

    public Account getAccount(int id) {
        return sessionFactory.getCurrentSession().get(Account.class, id);
    }
}

//MainApp.java
package com.example.bank;

import com.example.bank.config.AppConfig;
import com.example.bank.model.Account;
import com.example.bank.service.BankService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;

public class MainApp {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(AppConfig.class);
        var bankService = context.getBean(BankService.class);
        var txTemplate = context.getBean(TransactionTemplate.class);

        // Wrapping account creation in transaction
        txTemplate.execute(status -> {
            bankService.createAccount("Alice", 1000);
            bankService.createAccount("Bob", 500);
            return null;
        });

        // Successful Transfer
        try {
            txTemplate.execute(status -> {
                bankService.transfer(1, 2, 200, false);
                return null;
            });
            System.out.println("Transfer successful.");
        } catch (Exception e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // Failing Transfer (to test rollback)
        try {
            txTemplate.execute(status -> {
                bankService.transfer(1, 2, 100, true);
                return null;
            });
        } catch (Exception e) {
            System.out.println("Transfer rolled back: " + e.getMessage());
        }

        context.close();
    }
}


//MAVEN DEPENDENCIES
// <dependencies>
//     <dependency>
//         <groupId>org.springframework</groupId>
//         <artifactId>spring-context</artifactId>
//         <version>5.3.31</version>
//     </dependency>
//     <dependency>
//         <groupId>org.springframework</groupId>
//         <artifactId>spring-tx</artifactId>
//         <version>5.3.31</version>
//     </dependency>
//     <dependency>
//         <groupId>org.hibernate</groupId>
//         <artifactId>hibernate-core</artifactId>
//         <version>5.6.15.Final</version>
//     </dependency>
//     <dependency>
//         <groupId>mysql</groupId>
//         <artifactId>mysql-connector-java</artifactId>
//         <version>8.0.33</version>
//     </dependency>
// </dependencies>
