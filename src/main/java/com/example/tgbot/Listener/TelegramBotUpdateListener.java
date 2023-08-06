package com.example.tgbot.Listener;


import com.example.tgbot.SendMessages;
import com.example.tgbot.handlers.TelegramHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
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
    private final SendMessages sendMessages;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);

    public TelegramBotUpdateListener(TelegramBot telegramBot, List<TelegramHandler> handlers,
                                     SendMessages sendMessages) {
        this.telegramBot = telegramBot;
        this.handlers = handlers;
        this.sendMessages = sendMessages;

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
                    sendMessages.sendMessageWithEmoji(chatId, """
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
}
