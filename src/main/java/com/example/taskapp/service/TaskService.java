package com.example.taskapp.service;

import com.example.taskapp.model.Task;
import com.example.taskapp.model.User;
import com.example.taskapp.repository.TaskRepository;
import com.example.taskapp.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    public TaskService(TaskRepository taskRepo, UserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    public Page<Task> listForUser(User owner, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return taskRepo.findByOwner(owner, p);
    }

    public Task create(Task task, User owner) {
        task.setOwner(owner);
        return taskRepo.save(task);
    }

    public Optional<Task> findById(Long id) { return taskRepo.findById(id); }

    public Task update(Task t) { return taskRepo.save(t); }

    public void delete(Long id) { taskRepo.deleteById(id); }
}
