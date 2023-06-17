package com.example.tgbot.Service;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void save (Task task){
        taskRepository.save(task);
    }



}
