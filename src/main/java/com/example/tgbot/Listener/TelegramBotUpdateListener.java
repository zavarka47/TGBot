package com.example.tgbot.Listener;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBotUpdateListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final TaskService taskService;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);
    private final Pattern pattern = Pattern.compile(
            "(\\d{1,2}\\.\\d{1,2}\\.\\d{4} \\d{1,2}\\:\\d{1,2})\\s+([А-я\\s\\d\\p{Punct}]+)"
    );

    public TelegramBotUpdateListener(TelegramBot telegramBot,
    TaskService taskService) {
        this.telegramBot = telegramBot;
        this.taskService = taskService;
    }

    @PostConstruct
    public void init (){
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> list) {
        try {
            list.forEach(update -> {
                logger.info("Handles update: {}", update);
                Message message = update.message();
                Long chatId = message.chat().id();
                String text = message.text();
                if ("/start".equals(text)) {
                    sendMessage(chatId, """
                            Привет ![👋](tg://emoji?id=5368324170671202286)
                            Я помогу тебе составить расписание\\!
                            Введи задачу в формате \\"_01\\.01\\.2001 00\\:00 Поздравить друзей с новым годом_\\"
                            """);
                } else if (text != null){
                        Matcher matcher = pattern.matcher(text);
                        if (matcher.find()){
                            LocalDateTime dateTime = parse(matcher.group(1));
                            if (Objects.isNull(dateTime)){
                                sendMessage(chatId, """
                                    Чё\\?""
                                    ||Дата и\\/или время указаны в неверном формате||
                                    """);
                            } else {
                                String task = matcher.group(2);
                                Task notifyTask = new Task();
                                notifyTask.setChatId(chatId);
                                notifyTask.setDateTime(dateTime);
                                notifyTask.setText(task);
                                taskService.save(notifyTask);
                                sendMessage(chatId, "Задача сохранена[\uD83D\uDCDD](tg://emoji?id=5368324170671202286)");
                            }

                        } else {
                            sendMessage(chatId, """
                                    Чё\\?""
                                    ||Задача указана в неверном формате||
                                    """);
                        }

                    }



            });
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Nullable
    private LocalDateTime parse (String dateTime){
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        } catch (DateTimeException d){
            logger.error("Ошибка при записи даты из задачи: {}", d.getMessage());
            return null;

        }
    }

    private void sendMessage (Long chatId, String text){
        SendMessage sendMessage = new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()){
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }
}
