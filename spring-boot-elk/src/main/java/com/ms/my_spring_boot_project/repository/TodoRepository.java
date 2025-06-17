package com.ms.my_spring_boot_project.repository;

import com.ms.my_spring_boot_project.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Integer> {
    // JpaRepository provides all the necessary methods for CRUD operations
    // Additional custom methods can be defined here if needed
    // For example, you can add methods like:
    // List<Todo> findByUserId(int userId);
    // List<Todo> findByCompleted(boolean completed);
    // But for now, we will use the default methods provided by JpaRepository
    // No additional methods are needed for basic CRUD operations
    // JpaRepository already provides methods like save(), findById(), findAll(), deleteById(), etc.
    // This interface will allow us to perform CRUD operations on Todo entities
    // without needing to implement any methods ourselves.

}
