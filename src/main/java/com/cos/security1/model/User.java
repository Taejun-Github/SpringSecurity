package com.cos.security1.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    @Column(name = "USER_NAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE")
    private String role;

    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    private Timestamp createDate;
}
