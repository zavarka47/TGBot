package com.example.tgbot.handlers;

import com.example.tgbot.Service.TaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendPhoto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class EditHandler implements TelegramHandler{
    private final TelegramBot telegramBot;
    public EditHandler(TelegramBot telegramBot, TaskService taskService) {
        this.telegramBot = telegramBot;
    }

    @Override
    public boolean appliesTo(Update update) {
        return Objects.nonNull(update.callbackQuery()) ? update.callbackQuery().data().equals("2") : false;
    }

    @Override
    public void handleUpdate(Update update) {
        Long chatId = update.callbackQuery().from().id();
        try {
            byte [] photo = Files.readAllBytes(
                   Paths.get(EditHandler.class.getResource("/mem.jpg").toURI()));
            telegramBot.execute(new SendPhoto(chatId, photo));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

