package com.example.taskapp.controller;

import com.example.taskapp.dto.TaskDto;
import com.example.taskapp.model.Task;
import com.example.taskapp.model.User;
import com.example.taskapp.repository.UserRepository;
import com.example.taskapp.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepo;

    public TaskController(TaskService taskService, UserRepository userRepo) {
        this.taskService = taskService;
        this.userRepo = userRepo;
    }

    // ✅ GET TASKS
    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal UserDetails ud) {
        User user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(taskService.listForUser(user));
    }


    // ✅ CREATE TASK
    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam String username,
            @RequestBody TaskDto tDto
    ) {
        User u = userRepo.findByUsername(username).orElseThrow();

        Task t = new Task();
        t.setTitle(tDto.title());
        t.setDescription(tDto.description());
        t.setPriority(Task.Priority.valueOf(tDto.priority()));
        t.setDueDate(tDto.dueDate());

        Task saved = taskService.create(t, u);

        TaskDto response = new TaskDto(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus().name(),
                saved.getPriority().name(),
                saved.getDueDate()
        );

        return ResponseEntity.ok(response);
    }


    // ✅ UPDATE TASK
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody TaskDto dto
    ) {
        Task t = taskService.findById(id).orElseThrow();

        t.setTitle(dto.title());
        t.setDescription(dto.description());
        t.setStatus(Task.Status.valueOf(dto.status()));
        t.setPriority(Task.Priority.valueOf(dto.priority()));
        t.setDueDate(dto.dueDate());

        Task saved = taskService.update(t);

        TaskDto response = new TaskDto(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus().name(),
                saved.getPriority().name(),
                saved.getDueDate()
        );

        return ResponseEntity.ok(response);
    }


    // ✅ DELETE TASK
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
