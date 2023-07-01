package com.example.tgbot.Timer;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Repository.TaskRepository;
import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TaskTimer {
    private final TaskRepository taskRepository;
    private final TelegramBot telegramBot;
    private final TaskService taskService;

    public TaskTimer(TaskRepository taskRepository, TelegramBot telegramBot, TaskService taskService) {
        this.taskRepository = taskRepository;
        this.telegramBot = telegramBot;
        this.taskService = taskService;
    }

    @Scheduled (cron = "0 15 8 * * *")
    private void dailyTask(){
        Map<Long, List<Task>> tasksByChat = taskRepository.findAll()
                .stream()
                .filter(t -> t.getDateTime().toLocalDate().equals(LocalDate.now()))
                .collect(Collectors.groupingBy(task -> task.getChatId()));
        for (Long chatId:tasksByChat.keySet()) {
            if (tasksByChat.get(chatId).isEmpty()){
                telegramBot.execute(new SendMessage(chatId, """
                        Доброе утро!
                        На сегодня задач нет
                        """));
            } else {
                String tasks = """
                        Доброе утро! Сегодня в программе
                        """;
                for (Task task:tasksByChat.get(chatId)) {
                    tasks = tasks + task.getDateTime().toLocalTime() + " " + task.getText() + "\n";
                }
                telegramBot.execute(new SendMessage(chatId, tasks));

            }
        }
    }

    @Scheduled (fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void notifyTask(){
        taskRepository.findAllByDateTime(LocalDateTime.now().plusMinutes(5).truncatedTo(ChronoUnit.MINUTES))
                .forEach(task -> {
                    telegramBot.execute(
                            new SendMessage(task.getChatId(), "Вы просил напомнить" + task.getText()));
                });
    }

    @Scheduled (fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void deleteTask(){
        taskRepository.findAllByDateTime(LocalDateTime.now().minusMinutes(2).truncatedTo(ChronoUnit.MINUTES))
                .forEach(task -> { taskRepository.delete(task);});
    }

}
