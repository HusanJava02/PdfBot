package com.google.init;

import com.google.controller.UpdatesController;
import com.google.services.DatabaseService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;

public class MainInitializer {
    public static void main(String[] args) {

        File file = new File("/home/ubuntu/assets");
        file.mkdir();

        File pdfFolder = new File("/home/ubuntu/assets/PDFS");
        pdfFolder.mkdir();

        File pdfFolderImage = new File("/home/ubuntu/assets/images");
        pdfFolderImage.mkdir();

        UpdatesController updatesController = new UpdatesController();
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(updatesController);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
