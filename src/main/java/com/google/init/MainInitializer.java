package com.google.init;

import com.google.controller.UpdatesController;
import com.google.model.Users;
import com.google.services.DatabaseService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.util.List;

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
        List<Users> users = DatabaseService.getAllUsers();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SendMessage sendMessage = new SendMessage();
                int successCount = 0, failCount = 0;
                for (Users user : users) {
                    sendMessage.setChatId(String.valueOf(user.getChatId()));
                    sendMessage.setText("Assalomu alaykum botimiz yana ish faoliyatini boshladi\n Endilikda siz bemalol rasmlarni PDF xolatiga o`tkazib olishingiz mumkin");
                    if (updatesController.sendMessage(sendMessage)) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                    int count = successCount + failCount;
                    if (count % 50 == 0 || count == users.size()) {
                        sendMessage.setChatId("1324394249");
                        sendMessage.setText(count + "ta habar jo`natildi\nSuccess✅: " + successCount + "\nFail❌: " + failCount);
                        updatesController.sendMessage(sendMessage);
                        sendMessage.setChatId("968877318");
                        updatesController.sendMessage(sendMessage);
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }
}
