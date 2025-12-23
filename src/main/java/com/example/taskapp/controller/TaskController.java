package com.example.taskapp.controller;

import com.example.taskapp.dto.ApiResponse;
import com.example.taskapp.dto.TaskDto;
import com.example.taskapp.model.Task;
import com.example.taskapp.model.User;
import com.example.taskapp.repository.UserRepository;
import com.example.taskapp.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<ApiResponse<List<TaskDto>>> list(
            @AuthenticationPrincipal UserDetails ud) {

        User user = userRepo.findByUsername(ud.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<TaskDto> tasks = taskService.listForUser(user)
                .stream()
                .map(t -> new TaskDto(
                        t.getId(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getStatus().name(),
                        t.getPriority().name(),
                        t.getDueDate()
                ))
                .toList();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Tasks fetched", tasks)
        );
    }

    // ✅ CREATE TASK
    @PostMapping
    public ResponseEntity<ApiResponse<TaskDto>> create(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody TaskDto dto) {

        User user = userRepo.findByUsername(ud.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setPriority(Task.Priority.valueOf(dto.priority()));

        Task saved = taskService.create(task, user);

        TaskDto response = new TaskDto(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus().name(),
                saved.getPriority().name(),
                saved.getDueDate()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task created", response)
        );
    }

    // ✅ UPDATE TASK
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDto>> update(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id,
            @RequestBody TaskDto dto) {

        Task t = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        boolean isOwner = t.getOwner().getUsername().equals(ud.getUsername());
        boolean isAdmin = ud.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>(false, "Forbidden", null));
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
                updated.getDueDate()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task updated", response)
        );
    }

    // ✅ DELETE TASK
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id) {

        Task t = taskService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        boolean isOwner = t.getOwner().getUsername().equals(ud.getUsername());
        boolean isAdmin = ud.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403)
                    .body(new ApiResponse<>(false, "Forbidden", null));
        }

        taskService.delete(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Task deleted", null)
        );
    }
}
