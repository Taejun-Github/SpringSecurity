package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
    // select * from user where username = ? 의 쿼리가 동작한다.

    public User findByEmail(String email);
}
