package com.example.tgbot.Service;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public List<Task> getAllByChatId(Long chatId) {return taskRepository.findAllByChatId(chatId);}
    //public List<Task> getAllByDay(LocalDate today) {return taskRepository.findAll();}
    @Transactional
    public void deleteByIds (Long chatId, Long taskId){
        taskRepository.deleteByChatIdAndId(chatId, taskId);
    }







}
