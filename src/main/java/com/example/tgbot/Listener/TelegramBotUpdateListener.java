package com.example.tgbot.Listener;


import com.example.tgbot.handlers.TelegramHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Objects;


@Component
public class TelegramBotUpdateListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final List<TelegramHandler> handlers;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);

    public TelegramBotUpdateListener(TelegramBot telegramBot, List<TelegramHandler> handlers) {
        this.telegramBot = telegramBot;
        this.handlers = handlers;

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
                Long chatId = null;
                if (Objects.nonNull(update.message())){
                    chatId = update.message().chat().id();
                }
                if ( Objects.nonNull(update.callbackQuery())){
                    chatId = update.callbackQuery().from().id();
                }

                boolean messageHandle = false;
                for (TelegramHandler handler:handlers) {
                    if (handler.appliesTo(update)){
                        handler.handleUpdate(update);
                        messageHandle = true;

                    }
                } if (!messageHandle){
                    sendMessage(chatId, """
                         Чё\\?""
                         ||Такой команды я не знаю ☹️
                        """);
                }
            });
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void sendMessage (Long chatId, String text){
        SendMessage sendMessage = new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()){
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }
}
