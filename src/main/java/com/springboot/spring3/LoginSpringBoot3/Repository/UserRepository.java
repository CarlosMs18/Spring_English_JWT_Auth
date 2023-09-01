package com.springboot.spring3.LoginSpringBoot3.Repository;

import com.springboot.spring3.LoginSpringBoot3.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
