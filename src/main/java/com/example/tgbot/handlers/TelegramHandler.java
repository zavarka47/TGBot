package com.example.tgbot.handlers;

import com.pengrad.telegrambot.model.Update;
import liquibase.repackaged.net.sf.jsqlparser.statement.upsert.Upsert;

public interface TelegramHandler {
    boolean appliesTo (Update update);
    void handleUpdate (Update update);

}
