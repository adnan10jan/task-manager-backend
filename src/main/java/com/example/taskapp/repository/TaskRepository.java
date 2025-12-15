package com.example.taskapp.repository;

import com.example.taskapp.model.Task;
import com.example.taskapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByOwner(User owner);

}
