package com.example.taskapp.repository;

import com.example.taskapp.model.Task;
import com.example.taskapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByOwner(User owner, Pageable pageable);
}
