package ru.mzuev.taskmanagementsystem.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.mzuev.taskmanagementsystem.model.Task;
import ru.mzuev.taskmanagementsystem.service.TaskService;

@Component("taskSecurity")
public class TaskSecurity {

    private final TaskService taskService;

    public TaskSecurity(TaskService taskService) {
        this.taskService = taskService;
    }

    // true, если текущий пользователь назначен как исполнитель
    public boolean isExecutor(Long taskId) {
        Task task = taskService.getTaskById(taskId);
        if(task.getExecutor() == null) {
            return false;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        return currentUserEmail.equals(task.getExecutor().getEmail());
    }
}
