package com.example.tgbot.Listener;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBotUpdateListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final TaskService taskService;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);


    private final Pattern patternTime = Pattern.compile(
            "(\\d{1,2}\\.\\d{1,2}\\.\\d{4} \\d{1,2}:\\d{1,2})\\s+([А-я\\s\\d\\p{Punct}]+)"
    );
    private final Pattern patternIds = Pattern.compile(
            "(\\d+)(!!)(\\d+)"
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
                if (Objects.nonNull(update.message())) {
                    Long chatId = update.message().chat().id();
                    String text = update.message().text();

                    if ("/start".equals(text)) {
                        sendMessage(chatId,
                                """
                                        Привет ![👋](tg://emoji?id=5368324170671202286)
                                        Я помогу тебе составить расписание\\!
                                        """, sendKeyboard());
                    }


                    if (text != null && !text.equals("/start")) {
                        Matcher matcher = patternTime.matcher(text);
                        if (matcher.find()) {
                            LocalDateTime dateTime = parse(matcher.group(1));

                            if (Objects.isNull(dateTime)) {
                                sendMessage(chatId, """
                                         Чё\\?""
                                         ||Дата и\\/или время указаны в неверном формате||
                                        """, new InlineKeyboardMarkup());
                            } else if (dateTime.isBefore(LocalDateTime.now())){
                                sendMessage(chatId, """
                                         Если у тебя нем машины времени так не получится
                                        """, new InlineKeyboardMarkup());
                            } else {
                                Task task = new Task();
                                task.setChatId(chatId);
                                task.setDateTime(dateTime);
                                task.setText(matcher.group(2));
                                taskService.save(task);
                                sendMessage(chatId, """
                                                Задача сохранена[\\uD83D\\uDCDD](tg://emoji?id=5368324170671202286)"
                                                """,
                                        new InlineKeyboardMarkup());
                            }
                        } else {
                            sendMessage(chatId, """
                                    Чё\\\\?""
                                    ||Задача указана в неверном формате||
                                    """, new InlineKeyboardMarkup());
                        }
                    }
                }


                if (update.callbackQuery()!=null){
                    String data = update.callbackQuery().data();
                    Long chatId = update.callbackQuery().from().id();
                    Matcher matcher = patternIds.matcher(data);
                    if (matcher.find()){
                        taskService.deleteByIds(Long.valueOf(matcher.group(1)), Long.valueOf(matcher.group(3)));
                        sendMessage(chatId, "Задача удалена", new InlineKeyboardMarkup());
                    } else {
                    switch (data) {
                        case "1":
                            sendMessage(chatId, """
                                    Введите задачу в формате:
                                    _01\\.01\\.2001 00\\:00 Поздравить друзей с новым годом\\!_
                                    """, new InlineKeyboardMarkup());
                            break;
                        case "2":
                            break;
                        case "3":
                            List<Task> tasks = taskService.getAll(chatId);
                            if (tasks.isEmpty()) {
                                sendMessage(chatId, """
                                        На сегодня задач нет
                                        """, new InlineKeyboardMarkup());
                            } else {
                                String answer = "";
                                for (int i = 0; i < tasks.size(); i++) {
                                    answer = answer + tasks.get(i).toString() + "\n";
                                }
                                answer.replaceAll(".", " ").replaceAll(":", "\\:");
                                sendMessage(chatId, answer, new InlineKeyboardMarkup());
                            }
                            break;
                        case "4":
                            List<Task> tasks1 = taskService.getAll(chatId);
                            if (tasks1.isEmpty()) {
                                sendMessage(chatId, """
                                        На сегодня задач нет
                                        """, new InlineKeyboardMarkup());
                            } else {
                                InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
                                for (Task task : tasks1) {
                                    InlineKeyboardButton button =
                                            new InlineKeyboardButton(task.toString()).
                                                    callbackData(String.valueOf(
                                                            chatId + "!!" +
                                                                    task.getId()));
                                    keyboardMarkup.addRow(button);
                                }
                                sendMessage(chatId, """
                                        Выберите задачу, которую хотите удалить
                                        """, keyboardMarkup);
                            }
                    }
                    }
                }
            });
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void sendMessage (Long chatId, String text, InlineKeyboardMarkup keyboardMarkup){
        SendMessage sendMessage = new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2).replyMarkup(keyboardMarkup);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()){
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }


 public InlineKeyboardMarkup sendKeyboard(){
        InlineKeyboardButton button1 = new InlineKeyboardButton("create").callbackData("1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("update").callbackData("2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("read").callbackData("3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("delete").callbackData("4");
        return new InlineKeyboardMarkup().addRow(button1,button2, button3, button4);

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
}
