package com.ms.my_spring_boot_project.controller;

import com.ms.my_spring_boot_project.model.Todo;
import com.ms.my_spring_boot_project.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Todo>> getTodos() {
        try {
            List<Todo> todos = todoService.getTodos();
            return ResponseEntity.ok(todos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable int id) {
        try {
            Todo todo = todoService.getTodoById(id);
            if (todo != null) {
                return ResponseEntity.ok(todo);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/todos/save")
    public ResponseEntity<String> saveTodoById(@RequestParam int id) {
        try {
            todoService.saveTodoById(id);
            return ResponseEntity.ok("Todo saved successfully with ID " + id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving todo: " + e.getMessage());
        }
    }

    @PostMapping("/todos/saveRange")
    public ResponseEntity<String> saveTodosInRange(@RequestParam int x, @RequestParam int y) {
        try {
            todoService.saveTodosInRange(x, y);
            return ResponseEntity.ok("Todos saved successfully from ID " + x + " to " + y);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving todos: " + e.getMessage());
        }
    }
}
