package com.example.tgbot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendMessages {
    private final TelegramBot telegramBot;
    private Logger logger = LoggerFactory.getLogger(SendMessages.class);

    public SendMessages(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendSimpleMessage (Long chatId, String text){
        SendResponse sendResponse = telegramBot.execute(new SendMessage(chatId, text));
        if (!sendResponse.isOk()){
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

    public void sendMessageWithEmoji (Long chatId, String text){
        SendResponse sendResponse = telegramBot.execute(new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2));
        if (!sendResponse.isOk()){
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

    public void sendMessageWithKeyboard (Long chatId, String text, Keyboard keyboard){
        SendResponse sendResponse = telegramBot.execute(
                new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2).replyMarkup(keyboard));
        if (!sendResponse.isOk()){
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }
}
