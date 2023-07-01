package com.example.tgbot.handlers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StartHandler implements TelegramHandler{
    private final TelegramBot telegramBot;

    public StartHandler(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public boolean appliesTo(Update update) {
        return Objects.nonNull(update.message()) ? update.message().text().equals("/start") : false;
    }

    @Override
    public void handleUpdate(Update update) {

        InlineKeyboardButton button1 = new InlineKeyboardButton("Создать задачу").callbackData("1");
        InlineKeyboardButton button2 = new InlineKeyboardButton("не нажимать").callbackData("2");
        InlineKeyboardButton button3 = new InlineKeyboardButton("Показать все задачи").callbackData("3");
        InlineKeyboardButton button4 = new InlineKeyboardButton("Удалить задачу").callbackData("4");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup().addRow(button1, button2).addRow(button3, button4);

        Long chatId = update.message().chat().id();

        telegramBot.execute(new SendMessage(chatId,
                    """
                            Привет !👋
                            Я помогу тебе составить расписание!
                            """).replyMarkup(keyboard));

    }


}
