package com.example.UserManagement.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.UserManagement.model.User;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
boolean existsByEmail(String email);
    boolean existsByPassword(String password);
    Optional<User> findByEmailAndPassword(String email, String password);

    boolean existsByLocationId(UUID locationId);   // for deletion safety
}