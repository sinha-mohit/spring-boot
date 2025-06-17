package com.ms.my_spring_boot_project.service;

import com.ms.my_spring_boot_project.model.Todo;
import com.ms.my_spring_boot_project.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TodoService {
    private final WebClient webClient;
    private final String BASE_URL = "https://jsonplaceholder.typicode.com/todos";
    private final TodoRepository todoRepository;

    private static final Logger logger = LoggerFactory.getLogger(TodoService.class);

    public TodoService(WebClient webClient, TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
        this.webClient = webClient;
    }

    public List<Todo> getTodos() {
        return webClient.get()
                .uri(BASE_URL)
                .retrieve()
                .bodyToFlux(Todo.class)
                .collectList()
                .block();
    }

    public Todo getTodoById(int id) {
        Todo todo = todoRepository.findById(id).orElse(null);

        if(todo != null) {
            logger.info("Fetching Todo with id {} from repository", id);
            return todo;
        }
        return webClient.get()
                .uri(BASE_URL + "/{id}", id)
                .retrieve()
                .bodyToMono(Todo.class)
                .block();
    }

    public void saveTodoById(int id) {
        Todo todo = getTodoById(id);
        if (todo != null) {
            // Check if the todo already exists in the repository
            if (todoRepository.existsById(id)) {
                logger.warn("Todo with id {} already exists!! Not creating a new one.", id);
                return;
            }
            logger.info("Saving Todo with id: {}", id);
            todoRepository.save(todo);
        } else {
            logger.error("Todo not found with id: {}", id);
            throw new IllegalArgumentException("Todo not found with id: " + id);
        }
    }

    public void saveTodosInRange(int x, int y) {
        if (x < 1 || y < x) {
            throw new IllegalArgumentException("Invalid range: x must be >= 1 and y must be >= x");
        }

        for (int id = x; id <= y; id++) {
            try {
                saveTodoById(id);
            } catch (Exception e) {
                logger.error("Failed to save Todo with id: {}", id, e);
                throw new RuntimeException("Failed to save Todo with id: " + id, e);
            }
        }
    }
}
