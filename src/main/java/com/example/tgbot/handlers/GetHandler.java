package com.example.tgbot.handlers;

import com.example.tgbot.Entity.Task;
import com.example.tgbot.SendMessages;
import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
public class GetHandler implements TelegramHandler{
    private final TelegramBot telegramBot;
    private final TaskService taskService;
    private final SendMessages sendMessages;
    private final Logger logger = LoggerFactory.getLogger(GetHandler.class);

    public GetHandler(TelegramBot telegramBot, TaskService taskService,
                      SendMessages sendMessages) {
        this.telegramBot = telegramBot;
        this.taskService = taskService;
        this.sendMessages = sendMessages;
    }
    @Override
    public boolean appliesTo(Update update) {
        return Objects.nonNull(update.callbackQuery()) ? update.callbackQuery().data().equals("3") : false;
    }

    @Override
    public void handleUpdate(Update update) {
        Long chatId = update.callbackQuery().from().id();
        List<Task> tasks = taskService.getAllByChatId(chatId);

        if (tasks.isEmpty()) {
            sendMessages.sendSimpleMessage(chatId, "Список задач пуст");
        } else {
            Map<LocalDate, List<Task>> dates = tasks.stream()
                    .collect(Collectors.groupingBy(task -> task.getDateTime().toLocalDate()))
                    .entrySet().stream()
                    .collect(Collectors.toMap(s -> s.getKey(),
                            s -> s.getValue().stream()
                                    .sorted((o1, o2) -> o1.getDateTime().isBefore(o2.getDateTime()) ? 1 : -1).collect(Collectors.toList())))
                    ;
            String answer = "";

            for (LocalDate date :dates.keySet()) {
                answer = answer +"*"+ date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "*\n";
                for (Task t: dates.get(date)){
                    answer = answer + t.getDateTime().toLocalTime() + " " + t.getText() + "\n";
                }
            }
            answer = answer.replaceAll("\\.", "\\\\.")
                    .replaceAll("\\:", "\\\\:")
                    .replaceAll("\\-", "\\\\-");
            sendMessages.sendMessageWithEmoji(chatId, answer);
        }
    }
}
