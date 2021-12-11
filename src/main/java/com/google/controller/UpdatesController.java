package com.google.controller;

import com.google.Frames.FramesController;
import com.google.enums.Language;
import com.google.generates.PDFGenerator;
import com.google.model.Users;
import com.google.services.DatabaseService;
import com.google.templates.BotState;
import com.itextpdf.text.DocumentException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.*;

public class UpdatesController extends TelegramLongPollingBot {
    static final String apiUrl = "https://api.telegram.org/bot1762336264:AAGz9-95sFr2BtygeUuPtwjb4GeSlxBBjKk/getFile?file_id=";

    Map<Long, List<List<PhotoSize>>> photosMap = new HashMap<>();
    Map<Long, List<Document>> documentMap = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                updateThread(update);

            }
        });
        thread.start();
        System.out.println(thread.getId() + " ishladi ");

    }

    public void updateThread(Update update) {
        try {
            if (update.hasMessage()) {
                String botState1 = DatabaseService.getBotState(update);
                Message message = update.getMessage();
                System.out.println("botState1" + botState1 + " " + message.getChatId());

                Users userWithChatId = DatabaseService.getUserWithChatId(update);
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start")) {
                        DatabaseService.saveLanguage(update, Language.UZBEK);
                        SendMessage stiker = MessageController.stikerBot(update);
                        if (DatabaseService.exists(message.getChatId())) {
                            SendChatAction sendChatAction = new SendChatAction();
                            sendChatAction.setAction(ActionType.TYPING);
                            sendChatAction.setChatId(message.getChatId().toString());
                            Language userLanguage = DatabaseService.getUserLanguage(update);
                            SendMessage greeting = MessageController.getGreetingWithoutUpdate(update, userLanguage);
                            execute(greeting);

                        } else {
                            SendChatAction sendChatAction = new SendChatAction();
                            sendChatAction.setAction(ActionType.TYPING);
                            sendChatAction.setChatId(message.getChatId().toString());
                            execute(sendChatAction);
                            execute(stiker);
                            execute(MessageController.getStart(update));
                            SendMessage sendMessage = MessageController.newUser(update);
                            execute(sendMessage);
                        }
                    } else if (text.equals("/lang")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId().toString());
                        SendMessage choose = MessageController.chooseLang(update);
                        execute(choose);
                    } else if (text.equals("/profile")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId().toString());
                        sendMessage.setText("Savol taklif mulohazalar uchun \nMurojat : @JavaSpringDev");
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText("Profile \uD83E\uDDD1\u200D\uD83D\uDCBB");
                        inlineKeyboardButton.setUrl("https://t.me/JavaSpringDev");
                        List<List<InlineKeyboardButton>> profileButton = new ArrayList<>(Collections.singletonList(Collections.singletonList(inlineKeyboardButton)));
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(profileButton);
                        sendMessage.setReplyMarkup(markup);
                        execute(sendMessage);
                    } else if (text.equals("/help")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("https://telegra.ph/PDF-maker-bot--PDF-file-qollanmasi-12-05");
                        sendMessage.setChatId(message.getChatId().toString());
                        execute(sendMessage);
                    } else if (text.equals("/users")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(update.getMessage().getChatId().toString());
                        sendMessage.setText("Foydalanuvchilar : " + DatabaseService.getUsersCount());
                        execute(sendMessage);
                    } else {
                        String botState = userWithChatId.getBotState();
                        System.out.println(update.getMessage().getChatId() + " " + botState);
                        if (botState != null) {
                            System.out.println(update.getMessage().getChatId() + " " + botState);
                            if (botState.equals(BotState.GETPHOTO)) {
                                System.out.println(update.getMessage().getChatId() + " " + botState);
                                if (text.equals("PDF Generation \uD83D\uDCD5") || text.equals("PDF yaratish \uD83D\uDCD5")
                                        || text.equals("ПДФ генерация \uD83D\uDCD5")) {
                                    SendChatAction sendChatAction = new SendChatAction();
                                    sendChatAction.setAction(ActionType.TYPING);
                                    sendChatAction.setChatId(message.getChatId().toString());
                                    SendMessage sendMessage = MessageController.askPhoto(update);
                                    sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
                                    Message executed = execute(sendMessage);
                                } else if (text.equals("Generate \uD83D\uDDC2")) {
                                    send(update,userWithChatId.getLanguageUser());
                                    execute(MessageController.sendAnimationStiker(update));
                                }
                            } else {
                                SendChatAction sendChatAction = new SendChatAction();
                                sendChatAction.setAction(ActionType.TYPING);
                                sendChatAction.setChatId(message.getChatId().toString());
                                SendMessage sendMessage = new SendMessage(message.getChatId().toString(), userWithChatId.getLanguageUser().name().equals(Language.UZBEK.name()) ? "Iltimos /start buyrug'ini qayta jo'nating" :
                                        userWithChatId.getLanguageUser().name().equals(Language.ENGLISH.name()) ? "Please send /start to use this bot" : "Отправьте команду перезапуска /start");

                                execute(sendMessage);
                            }

                        } else {
                            SendChatAction sendChatAction = new SendChatAction();
                            sendChatAction.setAction(ActionType.TYPING);
                            sendChatAction.setChatId(message.getChatId().toString());
                            SendMessage sendMessage = new SendMessage(message.getChatId().toString(), userWithChatId.getLanguageUser().name().equals(Language.UZBEK.name()) ? "Iltimos /start buyrug'ini qayta jo'nating" :
                                    userWithChatId.getLanguageUser().name().equals(Language.ENGLISH.name()) ? "Please send /start to use this bot" : "Отправьте команду перезапуска /start");
                            DatabaseService.setBotState(update, BotState.START);
                            execute(sendMessage);
                        }
                    }
                } else if (message.hasPhoto()) {
                    List<PhotoSize> photos = message.getPhoto();
                    Users userFromDb = DatabaseService.getUserWithChatId(update);
                    if (photosMap.get(message.getChatId()) == null) {
                        photosMap.put(message.getChatId(), new ArrayList<>());
                    }
                    List<List<PhotoSize>> lists = photosMap.get(message.getChatId());
                    lists.add(photos);
                    photosMap.put(message.getChatId(), lists);
                    if (userFromDb.getBotState().equals(BotState.GETPHOTO)) {
                        SendChatAction sendChatAction = new SendChatAction();
                        sendChatAction.setAction(ActionType.TYPING);
                        sendChatAction.setChatId(message.getChatId().toString());

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(MessageController.getChatId(update).getChatId().toString());
                        sendMessage.setReplyToMessageId(message.getMessageId());
                        sendMessage.setText(userWithChatId.getLanguageUser().name().equals(Language.UZBEK.name()) ? "Rasm qabul qilindi , Generate orqali pdf ni qabul qilib olishingiz mumkin" :
                                userWithChatId.getLanguageUser().name().equals(Language.ENGLISH.name()) ? "Photo haas been saved , You can receive pdf file by pressing Generate button " : "Изображение сделано, вы можете скачать pdf, используя кнопку генерировать.\n");
//                        sendMessage.setReplyMarkup(FramesController.photoBottomButton(userWithChatId.getLanguageUser(), photos.get(photos.size() - 1).getFileUniqueId()));
                        sendMessage.setReplyMarkup(FramesController.makeGenerateKeyboardButton());
                        execute(sendMessage);
                    }
                } else if (update.getMessage().hasDocument()) {
                    Document document = update.getMessage().getDocument();
                    Language userLanguage = DatabaseService.getUserLanguage(update);
                    Integer fileSize = document.getFileSize();
                    GetFile getFile = new GetFile(document.getFileId());
                    File file = execute(getFile);
                    if (file.getFilePath().endsWith(".jpg") || file.getFilePath().endsWith(".jpeg")
                            || file.getFilePath().endsWith(".png") || file.getFilePath().endsWith(".tif")) {
                        if (fileSize < 10_024_000) {
                            if (documentMap.get(update.getMessage().getChatId()) == null) {
                                documentMap.put(update.getMessage().getChatId(), new ArrayList<>());
                            }
                            List<Document> documents = documentMap.get(update.getMessage().getChatId());
                            documents.add(document);
                            documentMap.put(update.getMessage().getChatId(), documents);
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(MessageController.getChatId(update).getChatId().toString());
                            sendMessage.setReplyToMessageId(message.getMessageId());
                            sendMessage.setText(userWithChatId.getLanguageUser().name().equals(Language.UZBEK.name()) ? "Rasm qabul qilindi , Generate orqali pdf ni qabul qilib olishingiz mumkin" :
                                    userWithChatId.getLanguageUser().name().equals(Language.ENGLISH.name()) ? "Photo haas been saved , You can receive pdf file by pressing Generate button " : "Изображение сделано, вы можете скачать pdf, используя кнопку генерировать.\n");
//                            sendMessage.setReplyMarkup(FramesController.photoBottomButton(userWithChatId.getLanguageUser(), document.getFileUniqueId()));
                            sendMessage.setReplyMarkup(FramesController.makeGenerateKeyboardButton());
                            execute(sendMessage);

                        } else {
                            SendMessage sendMessage = MessageController.maxFileSizeOutOfMessage(update, userLanguage);
                            execute(sendMessage);
                        }
                    } else execute(MessageController.notAllowedFileFormat(update, userLanguage));


                }
            } else if (update.hasCallbackQuery()) {

                String botState = DatabaseService.getBotState(update);
                Language userLanguage = DatabaseService.getUserLanguage(update);
                System.out.println("botState1" + botState + " " + update.getCallbackQuery().getMessage().getChatId());
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                if (data.equals(Language.ENGLISH.name())) {

                    EditMessageText start = MessageController.getGreeting(update, Language.ENGLISH);
                    DatabaseService.saveLanguage(update, Language.ENGLISH);
                    execute(start);
                    Message execute = execute(MessageController.getOptionsKeyboard(update, Language.ENGLISH));
                    DatabaseService.setBotState(update, BotState.GETPHOTO);
                } else if (data.equals(Language.RUS.name())) {

                    EditMessageText start = MessageController.getGreeting(update, Language.RUS);
                    DatabaseService.saveLanguage(update, Language.RUS);
                    execute(start);
                    execute(MessageController.getOptionsKeyboard(update, Language.RUS));
                    DatabaseService.setBotState(update, BotState.GETPHOTO);

                } else if (data.equals(Language.UZBEK.name())) {
                    EditMessageText start = MessageController.getGreeting(update, Language.UZBEK);
                    DatabaseService.saveLanguage(update, Language.UZBEK);
                    execute(start);
                    execute(MessageController.getOptionsKeyboard(update, Language.UZBEK));
                    DatabaseService.setBotState(update, BotState.GETPHOTO);


                }
//                else if (data.startsWith("generate")) {
//                    execute(MessageController.editSendedFile(update, userLanguage));
//                    String botStateNew = DatabaseService.getBotState(update);
//                    if (botStateNew.equals(BotState.GETPHOTO)) {
//                        try {
//                            List<String> getFilePathsUrls = new ArrayList<>();
//                            Long chatId = update.getCallbackQuery().getMessage().getChatId();
//                            List<List<PhotoSize>> lists = photosMap.get(chatId);
//                            List<Document> documents = documentMap.get(chatId);
//                            if (documents != null) {
//                                for (Document document : documents) {
//                                    GetFile getFile = new GetFile(document.getFileId());
//                                    File executedFile = execute(getFile);
//                                    getFilePathsUrls.add(executedFile.getFilePath());
//                                }
//                                Thread.sleep(2000);
//                                SendChatAction sendChatAction = new SendChatAction();
//                                sendChatAction.setAction(ActionType.UPLOADDOCUMENT);
//                                sendChatAction.setChatId(chatId.toString());
//                                execute(sendChatAction);
//                                PDFGenerator pdfGenerator = new PDFGenerator();
//                                pdfGenerator.generatePDF(getFilePathsUrls, update.getCallbackQuery().getMessage().getChatId());
//
//                            }
//                            if (lists != null) {
//                                for (List<PhotoSize> list : lists) {
//                                    System.out.println(apiUrl + list.get(list.size()-1).getFileId());
//                                    GetFile getFile = new GetFile();
//                                    System.out.println(list);
//                                    getFile.setFileId(list.get(list.size()-1).getFileId());
//                                    File executed = execute(getFile);
//                                    String filePath = executed.getFilePath();
//                                    getFilePathsUrls.add(filePath);
//                                }
//                                Thread.sleep(2000);
//                                SendChatAction sendChatAction = new SendChatAction();
//                                sendChatAction.setAction(ActionType.UPLOADDOCUMENT);
//                                sendChatAction.setChatId(chatId.toString());
//                                execute(sendChatAction);
//                                PDFGenerator pdfGenerator = new PDFGenerator();
//                                pdfGenerator.generatePDF(getFilePathsUrls, update.getCallbackQuery().getMessage().getChatId());
//
//                            }
//
//                            SendDocument sendDocument = new SendDocument();
//                            sendDocument.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
//                            sendDocument.setCaption(userLanguage.name().equals(Language.UZBEK.name()) ? "PDF file tayyor ! Botimizni do'slaringizga ham ulashing @pdfdocsbot" : userLanguage.name().equals(Language.ENGLISH.name()) ?
//                                    "PDF is ready ! If you like the bot, Please share to your friends @pdfdocsbot" : "PDF готов, если вам нравится наш бот, поделитесь им с друзьями @pdfdocsbot");
//                            InputFile inputFile = new InputFile();
//                            inputFile.setMedia(new java.io.File("src/main/java/com/google/resources/PDFS/" + chatId + ".pdf"));
//                            sendDocument.setDocument(inputFile);
//                            sendDocument.setReplyToMessageId(update.getCallbackQuery().getMessage().getMessageId());
//                            execute(sendDocument);
//                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//                            InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
//                            keyboardButton.setText("Share");
//                            keyboardButton.setSwitchInlineQueryCurrentChat("Shared message");
//                            List<InlineKeyboardButton> buttons = new ArrayList<>();
//                            List<List<InlineKeyboardButton>> col = new ArrayList<>();
//                            col.add(buttons);
//                            inlineKeyboardMarkup.setKeyboard(col);
//                            sendDocument.setReplyMarkup(inlineKeyboardMarkup);
//                            java.io.File file = new java.io.File("src/main/java/com/google/resources/PDFS/" + chatId + ".pdf");
//                            if (file.delete()) {
//                                System.out.println("deleted");
//                            }
//
//                            if (lists != null) {
//                                deletePhotos(chatId);
//                            }
//                            if (documents != null) {
//                                deletePhotos(chatId);
//                            }
//                            photosMap.remove(update.getCallbackQuery().getMessage().getChatId());
//                            documentMap.remove(update.getCallbackQuery().getMessage().getChatId());
//                        } catch (IOException | DocumentException | InterruptedException e) {
//                            e.printStackTrace();
//                            photosMap.remove(update.getCallbackQuery().getMessage().getChatId());
//                            documentMap.remove(update.getCallbackQuery().getMessage().getChatId());
//                            String s = e.toString();
//                            try {
//                                execute(new SendMessage("968877318",s));
//                            } catch (TelegramApiException telegramApiException) {
//                                telegramApiException.printStackTrace();
//                            }
//
//                        }
//                    } else {
//                        SendMessage sendMessage = MessageController.askPhoto(update);
//                        DatabaseService.setBotState(update, BotState.GETPHOTO);
//                        execute(sendMessage);
//                    }
//                }
//
                else if (data.startsWith("delete")) {
                    List<List<PhotoSize>> lists = photosMap.get(update.getCallbackQuery().getMessage().getChatId());
                    if (lists != null) {
                        lists.removeIf(photoSizes -> data.endsWith(photoSizes.get(photoSizes.size() - 1).getFileUniqueId()));
                        execute(editMessageTextRemove(update));
                    }
                    List<Document> documents = documentMap.get(update.getCallbackQuery().getMessage().getChatId());
                    if (documents != null) {
                        documents.removeIf(document -> data.endsWith(document.getFileUniqueId()));
                        execute(editMessageTextRemove(update));
                    }
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            String s = e.toString();
            try {
                execute(new SendMessage("968877318", s));
            } catch (TelegramApiException telegramApiException) {
                telegramApiException.printStackTrace();
            }
            photosMap.remove(MessageController.getChatId(update).getChatId());
            documentMap.remove(MessageController.getChatId(update).getChatId());
        }
    }

    public void deletePhotos(Long chatId) {
        java.io.File file = new java.io.File("src/main/java/com/google/resources/images/");
        String[] list = file.list();
        if (list != null) {
            for (String s : list) {
                if (s.startsWith(chatId.toString())) {
                    java.io.File deletingFile = new java.io.File("src/main/java/com/google/resources/images/" + s);
                    if (deletingFile.delete()) {
                        System.out.println("Deleted+" + s);
                    }
                }
            }
        }

    }


    public EditMessageText editMessageTextRemove(Update update) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        editMessageText.setText("❌❌❌");
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        return editMessageText;
    }


    @Override
    public String getBotUsername() {
        return "@pdfdocsbot";
    }

    @Override
    public String getBotToken() {
        return "5039461659:AAGCxFgEjUkzm4ForurJxOFNBiS4qxkJTGI";
    }

    public SendChatAction getChatAction(Long chatId) {
        SendChatAction sendChatAction = new SendChatAction();
        sendChatAction.setAction(ActionType.UPLOADDOCUMENT);
        sendChatAction.setChatId(chatId.toString());
        return sendChatAction;
    }

    public void send(Update update, Language userLanguage) throws TelegramApiException {
//        execute(MessageController.editSendedFile(update, userLanguage));
        String botStateNew = DatabaseService.getBotState(update);
        if (botStateNew.equals(BotState.GETPHOTO)) {
            try {
                List<String> getFilePathsUrls = new ArrayList<>();
                Long chatId = update.getMessage().getChatId();
                List<List<PhotoSize>> lists = photosMap.get(chatId);
                List<Document> documents = documentMap.get(chatId);
                if (documents != null) {
                    for (Document document : documents) {
                        GetFile getFile = new GetFile(document.getFileId());
                        File executedFile = execute(getFile);
                        getFilePathsUrls.add(executedFile.getFilePath());
                    }
                    Thread.sleep(2000);
                    SendChatAction sendChatAction = new SendChatAction();
                    sendChatAction.setAction(ActionType.UPLOADDOCUMENT);
                    sendChatAction.setChatId(chatId.toString());
                    execute(sendChatAction);
                    PDFGenerator pdfGenerator = new PDFGenerator();
                    pdfGenerator.generatePDF(getFilePathsUrls, update.getMessage().getChatId());

                }
                if (lists != null) {
                    for (List<PhotoSize> list : lists) {
                        System.out.println(apiUrl + list.get(list.size() - 1).getFileId());
                        GetFile getFile = new GetFile();
                        System.out.println(list);
                        getFile.setFileId(list.get(list.size() - 1).getFileId());
                        File executed = execute(getFile);
                        String filePath = executed.getFilePath();
                        getFilePathsUrls.add(filePath);
                    }
                    Thread.sleep(2000);
                    SendChatAction sendChatAction = new SendChatAction();
                    sendChatAction.setAction(ActionType.UPLOADDOCUMENT);
                    sendChatAction.setChatId(chatId.toString());
                    execute(sendChatAction);
                    PDFGenerator pdfGenerator = new PDFGenerator();
                    pdfGenerator.generatePDF(getFilePathsUrls, update.getMessage().getChatId());

                }

                SendDocument sendDocument = new SendDocument();
                sendDocument.setChatId(update.getMessage().getChatId().toString());
                sendDocument.setCaption(userLanguage.name().equals(Language.UZBEK.name()) ? "PDF file tayyor ! Botimizni do'stlaringizga ham ulashing @pdfdocsbot" : userLanguage.name().equals(Language.ENGLISH.name()) ?
                        "PDF is ready ! If you like the bot, Please share to your friends @pdfdocsbot" : "PDF готов, если вам нравится наш бот, поделитесь им с друзьями @pdfdocsbot");
                InputFile inputFile = new InputFile();
                inputFile.setMedia(new java.io.File("src/main/java/com/google/resources/PDFS/" + chatId + ".pdf"));
                sendDocument.setDocument(inputFile);
                sendDocument.setReplyToMessageId(update.getMessage().getMessageId());
                sendDocument.setReplyMarkup(FramesController.optionsButton(userLanguage));
                execute(sendDocument);
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
                keyboardButton.setText("Share");
                keyboardButton.setSwitchInlineQueryCurrentChat("Shared message");
                List<InlineKeyboardButton> buttons = new ArrayList<>();
                List<List<InlineKeyboardButton>> col = new ArrayList<>();
                col.add(buttons);
                inlineKeyboardMarkup.setKeyboard(col);
                sendDocument.setReplyMarkup(inlineKeyboardMarkup);
                java.io.File file = new java.io.File("src/main/java/com/google/resources/PDFS/" + chatId + ".pdf");
                if (file.delete()) {
                    System.out.println("deleted");
                }

                if (lists != null) {
                    deletePhotos(chatId);
                }
                if (documents != null) {
                    deletePhotos(chatId);
                }
                photosMap.remove(update.getMessage().getChatId());
                documentMap.remove(update.getMessage().getChatId());
            } catch (IOException | DocumentException | InterruptedException e) {
                e.printStackTrace();
                photosMap.remove(update.getMessage().getChatId());
                documentMap.remove(update.getMessage().getChatId());
                String s = e.toString();
                try {
                    execute(new SendMessage("968877318", s));
                } catch (TelegramApiException telegramApiException) {
                    telegramApiException.printStackTrace();
                }

            }
        } else {
            SendMessage sendMessage = MessageController.askPhoto(update);
            DatabaseService.setBotState(update, BotState.GETPHOTO);
            execute(sendMessage);
        }

    }


}

