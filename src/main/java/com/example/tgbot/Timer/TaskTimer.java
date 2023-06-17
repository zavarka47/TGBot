package com.example.tgbot.Timer;

import com.example.tgbot.Repository.TaskRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class TaskTimer {
    private final TaskRepository taskRepository;
    private final TelegramBot telegramBot;

    public TaskTimer(TaskRepository taskRepository, TelegramBot telegramBot) {
        this.taskRepository = taskRepository;
        this.telegramBot = telegramBot;
    }

    @Scheduled (fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void task(){
        taskRepository.findAllByDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .forEach(task -> {
                    telegramBot.execute(
                            new SendMessage(task.getChatId(), "Вы просили напомнить: " + task.getText()));
                    taskRepository.delete(task);
                });
    }
}
