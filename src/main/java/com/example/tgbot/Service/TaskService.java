package com.example.tgbot.Service;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Repository.TaskRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void save (Task task){
        taskRepository.save(task);
    }

    public List<Task> getAll (Long chatId) {return taskRepository.findAllByChatId(chatId);}
    @Transactional
    public void deleteByIds (Long chatId, Long taskId){
        taskRepository.deleteByChatIdAndId(chatId, taskId);
    }







}
