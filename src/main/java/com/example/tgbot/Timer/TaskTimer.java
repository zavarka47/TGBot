package com.example.tgbot.Timer;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Repository.TaskRepository;
import com.example.tgbot.SendMessages;
import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TaskTimer {
    private final TaskRepository taskRepository;
    private final SendMessages sendMessages;
    private final TaskService taskService;

    public TaskTimer(TaskRepository taskRepository, SendMessages sendMessages, TaskService taskService) {
        this.taskRepository = taskRepository;
        this.sendMessages = sendMessages;
        this.taskService = taskService;
    }

    @Scheduled (cron = "*/6 * 23 * * *")
    private void dailyTask(){
        Map<Long, List<Task>> tasksByChat = taskRepository
                .findAllForToday()
                .stream()
                .collect(Collectors.groupingBy(task -> task.getChatId()));

        for (Map.Entry<Long, List<Task>> map : tasksByChat.entrySet()){
            if (map.getValue().isEmpty()){
                sendMessages.sendSimpleMessage(map.getKey(), """
                        Доброе утро!
                        На сегодня задач нет
                        """);
            } else {
                String tasks = """
                        Доброе утро! Сегодня в программе
                        """;
                for (Task task:map.getValue()) {
                    tasks = tasks + task.getDateTime().toLocalTime() + " " + task.getText() + "\n";
                }
                sendMessages.sendSimpleMessage(map.getKey(), tasks);

            }
        }
    }

    @Scheduled (fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void notifyTask(){
        taskRepository.findAllByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(task -> {
                    sendMessages.sendSimpleMessage(task.getChatId(), ("Вы просил напомнить" + task.getText()));
                });
    }

/*    @Scheduled (fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void deleteTask(){
        taskRepository.findAllByDateTime(LocalDateTime.now().plusMinutes(1).truncatedTo(ChronoUnit.MINUTES))
                .forEach(task -> { taskRepository.delete(task);});
    }*/

}
