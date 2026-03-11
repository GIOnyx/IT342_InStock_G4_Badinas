package edu.cit.badinas.instock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.badinas.instock.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
