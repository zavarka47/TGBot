package com.example.tgbot.handlers;

import com.example.tgbot.SendMessages;
import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import com.pengrad.telegrambot.model.request.Keyboard;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;



import static org.mockito.Mockito.verify;



@ExtendWith(MockitoExtension.class)
public class StartHandlerTests {
    @Mock
    private TelegramBot telegramBot;
    @Mock
    SendMessages sendMessages;
    @InjectMocks
    private StartHandler startHandler;
    private static Update update;

    @BeforeAll
    public static void initializationResource () throws URISyntaxException, IOException {
        String json = Files.readString(
                Path.of(StartHandlerTests.class.getClassLoader().getResource("message.json").toURI()));
        update = BotUtils.fromJson(json.replace("%text%", "/start"), Update.class);
    }

    @Test
    public void appliesToTest() {
        Assertions.assertEquals(update.message().text(), "/start");
    }


    @Test
    public void handleUpdateTest() {

        startHandler.handleUpdate(update);

        ArgumentCaptor<Long> chatIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Keyboard> keyboard = ArgumentCaptor.forClass(Keyboard.class);

        verify(sendMessages).sendMessageWithKeyboard(chatIdCaptor.capture(), textCaptor.capture(), keyboard.capture());

        Long chatId = chatIdCaptor.getValue();
        String text = textCaptor.getValue();

        Assertions.assertEquals(chatId, update.message().chat().id());
        Assertions.assertTrue(text.contains("Привет"));

    }
}
