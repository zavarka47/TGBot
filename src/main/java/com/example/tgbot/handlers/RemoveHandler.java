package com.example.tgbot.handlers;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.Loggers;
import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class RemoveHandler implements TelegramHandler{
    private final TelegramBot telegramBot;
    private final TaskService taskService;
    private final Pattern patternIds = Pattern.compile("(\\d+)(!!)(\\d+)");

    public RemoveHandler(TelegramBot telegramBot, TaskService taskService) {
        this.telegramBot = telegramBot;
        this.taskService = taskService;
    }

    @Override
    public boolean appliesTo(Update update) {
        return Objects.nonNull(update.callbackQuery()) && (update.callbackQuery().data().equals("4") || patternIds.matcher(update.callbackQuery().data()).find());
    }

    @Override
    public void handleUpdate(Update update) {
        Long chatId = update.callbackQuery().from().id();
        String data = update.callbackQuery().data();
        Matcher matcher = patternIds.matcher(data);

        if (matcher.find()){
            taskService.deleteByIds(Long.valueOf(matcher.group(1)), Long.valueOf(matcher.group(3)));
            sendMessage(chatId, "Задача удалена", new InlineKeyboardMarkup());
        }


        if (data.equals("4")) {
            List<Task> tasks1 = taskService.getAllByChatId(chatId)
                    .stream().
                    sorted((o1, o2) -> o1.getDateTime().isBefore(o2.getDateTime()) ? 1 : -1)
                    .collect(Collectors.toList());
            if (tasks1.isEmpty()) {
                sendMessage(chatId, "На сегодня задач нет", new InlineKeyboardMarkup());
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
                sendMessage(chatId, "Выберите задачу, которую хотите удалить", keyboardMarkup);

            }
        }
    }


        public void sendMessage(Long chatId, String text, InlineKeyboardMarkup keyboard) {
            SendResponse sendResponse = telegramBot.execute(
                    new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2).replyMarkup(keyboard));
            if (!sendResponse.isOk()){
                Loggers.getLogger(CreateHandler.class).error("Error during sending message: {}", sendResponse.description());
            }
        }
}
