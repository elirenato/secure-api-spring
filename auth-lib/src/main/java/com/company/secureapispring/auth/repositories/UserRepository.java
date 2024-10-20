package com.company.secureapispring.auth.repositories;

import com.company.secureapispring.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: Cache this method
    Optional<User> findByUsername(@Param("username") String username);
}
