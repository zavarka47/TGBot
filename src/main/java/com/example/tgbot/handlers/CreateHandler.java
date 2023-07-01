package com.example.tgbot.handlers;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Loggers;
import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CreateHandler implements TelegramHandler {
    private final TelegramBot telegramBot;
    private final TaskService taskService;
    private final Pattern patternTime = Pattern.compile(
            "(\\d{1,2}\\.\\d{1,2}\\.\\d{4} \\d{1,2}:\\d{1,2})\\s+([А-я\\s\\d\\p{Punct}]+)"
    );

    public CreateHandler(TelegramBot telegramBot, TaskService taskService) {
        this.telegramBot = telegramBot;
        this.taskService = taskService;
    }

    @Override
    public boolean appliesTo(Update update) {
        if (Objects.nonNull(update.message())){
            return patternTime.matcher(update.message().text()).find();
        }
        if ( Objects.nonNull(update.callbackQuery())){
            return update.callbackQuery().data().equals("1");
        }
        return  false;
    }

    @Override
    public void handleUpdate(Update update) {
        if ( Objects.nonNull(update.callbackQuery())){
            Long chatId = update.callbackQuery().from().id();
            sendMessage(chatId, """
                                    Введите задачу в формате:
                                    _01\\.01\\.2001 00\\:00 Поздравить друзей с новым годом\\!_
                                    """);
        }

        if (Objects.nonNull(update.message())){
            Long chatId = update.message().chat().id();
            Matcher matcher = patternTime.matcher(update.message().text());
            if (matcher.find()) {
                LocalDateTime dateTime = parse(matcher.group(1));

                if (Objects.isNull(dateTime)) {
                    sendMessage(chatId, """
                         Чё\\?""
                         ||Дата и\\/или время указаны в неверном формате||
                        """);
                } else if (dateTime.isBefore(LocalDateTime.now())) {
                    sendMessage(chatId, """
                         Если у тебя нет машины времени, то так не получится
                        """);
                } else {
                    Task task = new Task();
                    task.setChatId(chatId);
                    task.setDateTime(dateTime.truncatedTo(ChronoUnit.MINUTES));
                    task.setText(matcher.group(2));
                    taskService.save(task);
                    sendMessage(chatId, """
                        Задача сохранена✍️
                        """);
                }
            }
        }

    }


    @Nullable
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (DateTimeException d) {
            Loggers.getLogger(CreateHandler.class).error("Ошибка при записи даты из задачи: {}", d.getMessage());
            return null;

        }
    }

    public void sendMessage(Long chatId, String text) {
        SendResponse sendResponse = telegramBot.execute(
                new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2));
        if (!sendResponse.isOk()){
            Loggers.getLogger(CreateHandler.class).error("Error during sending message: {}", sendResponse.description());
        }
    }
}
