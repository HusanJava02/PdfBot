package com.google.init;

import com.google.controller.UpdatesController;
import com.google.services.DatabaseService;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MainInitializer {
    @SneakyThrows
    public static void main(String[] args) {

        //1 host
        //2 db_name
        //password
//        if (args.length < 3) throw new IllegalArgumentException();

//        DatabaseService.setHost(args[0],args[1]);
//        DatabaseService.password = args[2];

        DatabaseService databaseService = new DatabaseService();
        UpdatesController updatesController = new UpdatesController();
        updatesController.clearWebhook();
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(updatesController);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
