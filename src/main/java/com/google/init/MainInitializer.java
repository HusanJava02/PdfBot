package com.google.init;
import com.google.controller.UpdatesController;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class MainInitializer {
    public static void main(String[] args) {

        ApiContextInitializer.init();

        UpdatesController updatesController = new UpdatesController();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(updatesController);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
