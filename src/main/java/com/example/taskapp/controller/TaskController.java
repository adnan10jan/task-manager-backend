package com.example.taskapp.controller;

import com.example.taskapp.dto.TaskDto;
import com.example.taskapp.model.Task;
import com.example.taskapp.model.User;
import com.example.taskapp.repository.UserRepository;
import com.example.taskapp.service.TaskService;
import com.example.taskapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.net.URI;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserRepository userRepo;

    public TaskController(TaskService taskService, UserRepository userRepo) {
        this.taskService = taskService;
        this.userRepo = userRepo;
    }

    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal UserDetails ud,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        User user = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        Page<Task> p = taskService.listForUser(user, page, size);
        var dtoPage = p.map(t -> new TaskDto(t.getId(), t.getTitle(), t.getDescription(), t.getStatus().name(), t.getPriority().name(), t.getDueDate()));
        return ResponseEntity.ok(dtoPage.getContent());
    }

    @PostMapping
    public ResponseEntity<?> create(@AuthenticationPrincipal UserDetails ud, @RequestBody TaskDto tDto) {
        User u = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        Task t = new Task();
        t.setTitle(tDto.title());
        t.setDescription(tDto.description());
        t.setPriority(Task.Priority.valueOf(tDto.priority()));
        Task saved = taskService.create(t, u);
        return ResponseEntity.created(URI.create("/api/tasks/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id, @RequestBody TaskDto dto) {
        Task t = taskService.findById(id).orElseThrow();
        // Only owner or admin should update (check)
        if (!t.getOwner().getUsername().equals(ud.getUsername()) && ud.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        t.setTitle(dto.title());
        t.setDescription(dto.description());
        t.setStatus(Task.Status.valueOf(dto.status()));
        t.setPriority(Task.Priority.valueOf(dto.priority()));
        taskService.update(t);
        return ResponseEntity.ok(t);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        Task t = taskService.findById(id).orElseThrow();
        if (!t.getOwner().getUsername().equals(ud.getUsername()) && ud.getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).build();
        }
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
