package com.example.tgbot.Listener;

import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendMessages {
    private final TelegramBot telegramBot;
    private final Logger logger = LoggerFactory.getLogger(SendMessages.class);
    public SendMessages(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }




}
