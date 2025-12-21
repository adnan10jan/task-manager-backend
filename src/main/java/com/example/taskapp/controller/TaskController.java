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
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            @RequestBody TaskDto dto
    ) {
        User user = userRepo
                .findByUsername(userDetails.getUsername())
                .orElseThrow();

        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPriority(Task.Priority.valueOf(dto.priority()));

        Task saved = taskService.create(task, user);
        return ResponseEntity.ok(saved);
    }



    // ✅ UPDATE TASK
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @RequestBody TaskDto dto) {

        // 🔎 TEMP DEBUG LOGS (ADD HERE)
        System.out.println("Logged user: " + ud.getUsername());
        ud.getAuthorities().forEach(a ->
                System.out.println("ROLE => " + a.getAuthority())
        );

        Task t = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isOwner = t.getOwner().getUsername().equals(ud.getUsername());
        boolean isAdmin = ud.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        t.setTitle(dto.title());
        t.setDescription(dto.description());
        t.setStatus(Task.Status.valueOf(dto.status()));
        t.setPriority(Task.Priority.valueOf(dto.priority()));

        Task updated = taskService.update(t);

        TaskDto response = new TaskDto(
                updated.getId(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getStatus().name(),
                updated.getPriority().name(),
                updated.getDueDate(),
                updated.getOwner().getUsername()
        );

        return ResponseEntity.ok(
                new TaskDto(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getStatus().name(),
                        t.getPriority().name(),
                        t.getDueDate(),
                        t.getOwner().getUsername()
                )
        );

    }


    // ✅ DELETE TASK
    @DeleteMapping("/{id}")

    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id) {

        System.out.println("==== DELETE DEBUG ====");
        System.out.println("User: " + ud.getUsername());
        ud.getAuthorities().forEach(a ->
                System.out.println("AUTHORITY => " + a.getAuthority())
        );

        Task t = taskService.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        boolean isOwner = t.getOwner().getUsername().equals(ud.getUsername());
        boolean isAdmin = ud.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("ADMIN"));

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
