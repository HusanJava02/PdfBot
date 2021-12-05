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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class UpdatesController extends TelegramLongPollingBot {
    Map<Long,List<List<PhotoSize>>> map = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        try {

            if (update.hasMessage()) {
                String botState1 = DatabaseService.getBotState(update);
                Message message = update.getMessage();
                System.out.println("botState1" + botState1 + " " + message.getChatId());
                Users userWithChatId = DatabaseService.getUserWithChatId(update);
                if (message.hasText()) {
                    String text = message.getText();
                    if (text.equals("/start")) {

                        SendMessage stiker = MessageService.stikerBot(update);
                        if (DatabaseService.exists(message.getChatId())) {
                            SendChatAction sendChatAction = new SendChatAction();
                            sendChatAction.setAction(ActionType.TYPING);
                            sendChatAction.setChatId(message.getChatId());
                            Language userLanguage = DatabaseService.getUserLanguage(update);
                            SendMessage greeting = MessageService.getGreetingWithoutUpdate(update, userLanguage);
                            execute(greeting);

                        } else {
                            SendChatAction sendChatAction = new SendChatAction();
                            sendChatAction.setAction(ActionType.TYPING);
                            sendChatAction.setChatId(message.getChatId());
                            execute(sendChatAction);
                            execute(stiker);
                            execute(MessageService.getStart(update));
                        }
                    }else if (text.equals("/lang")){
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId());
                        SendMessage choose = MessageService.chooseLang(update);
                        execute(choose);
                    }else if (text.equals("/profile")){
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId());
                        sendMessage.setText("Savol taklif mulohazalar uchun \nMurojat : @JavaSpringDev");
                        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                        inlineKeyboardButton.setText("Profile \uD83E\uDDD1\u200D\uD83D\uDCBB");
                        inlineKeyboardButton.setUrl("https://t.me/JavaSpringDev");
                        List<List<InlineKeyboardButton>> profileButton = new ArrayList<>(Collections.singletonList(Collections.singletonList(inlineKeyboardButton)));
                        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(profileButton);
                        sendMessage.setReplyMarkup(markup);
                        execute(sendMessage);
                    }else if (text.equals("/help")){
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("https://telegra.ph/PDF-maker-bot--PDF-file-qollanmasi-12-05");
                        sendMessage.setChatId(message.getChatId());
                        execute(sendMessage);
                    }else {
                        String botState = userWithChatId.getBotState();
                        if (botState != null) {
                            if (botState.equals(BotState.GETPHOTO)) {
                                if (text.equals("PDF Generation \uD83D\uDCD5") || text.equals("PDF yaratish \uD83D\uDCD5")
                                        || text.equals("ПДФ генерация \uD83D\uDCD5")) {
                                    SendChatAction sendChatAction = new SendChatAction();
                                    sendChatAction.setAction(ActionType.TYPING);
                                    sendChatAction.setChatId(message.getChatId());
                                    SendMessage sendMessage = MessageService.askPhoto(update);
                                    sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
                                    Message executed = execute(sendMessage);

                                }

                            } else {
                                SendChatAction sendChatAction = new SendChatAction();
                                sendChatAction.setAction(ActionType.TYPING);
                                sendChatAction.setChatId(message.getChatId());
                                SendMessage sendMessage = new SendMessage(message.getChatId(), userWithChatId.getLanguageUser().name().equals(Language.UZBEK.name()) ? "Iltimos /start buyrug'ini qayta jo'nating" :
                                        userWithChatId.getLanguageUser().name().equals(Language.ENGLISH.name()) ? "Please send /start to use this bot" : "Отправьте команду перезапуска /start");

                                execute(sendMessage);
                            }

                        } else {
                            SendChatAction sendChatAction = new SendChatAction();
                            sendChatAction.setAction(ActionType.TYPING);
                            sendChatAction.setChatId(message.getChatId());
                            SendMessage sendMessage = new SendMessage(message.getChatId(), userWithChatId.getLanguageUser().name().equals(Language.UZBEK.name()) ? "Iltimos /start buyrug'ini qayta jo'nating" :
                                    userWithChatId.getLanguageUser().name().equals(Language.ENGLISH.name()) ? "Please send /start to use this bot" : "Отправьте команду перезапуска /start");
                            DatabaseService.setBotState(update, BotState.START);
                            execute(sendMessage);
                        }
                    }
                } else if (message.hasPhoto()) {
                    List<PhotoSize> photos = message.getPhoto();
                    Users userFromDb = DatabaseService.getUserWithChatId(update);
                    if (map.get(message.getChatId()) == null){
                        map.put(message.getChatId(),new ArrayList<>());
                    }
                    List<List<PhotoSize>> lists = map.get(message.getChatId());
                    lists.add(photos);
                    map.put(message.getChatId(),lists);
                    if (userFromDb.getBotState().equals(BotState.GETPHOTO)) {
                        SendChatAction sendChatAction = new SendChatAction();
                        sendChatAction.setAction(ActionType.TYPING);
                        sendChatAction.setChatId(message.getChatId());

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(MessageService.getChatId(update).getChatId());
                        sendMessage.setReplyToMessageId(message.getMessageId());
                        sendMessage.setText(userWithChatId.getLanguageUser().name().equals(Language.UZBEK.name())?"Rasm qabul qilindi , Generate orqali pdf ni qabul qilib olishingiz mumkin":
                                userWithChatId.getLanguageUser().name().equals(Language.ENGLISH.name())?"Photo haas been saved , You can receive pdf file by pressing Generae button ":"Изображение сделано, вы можете скачать pdf, используя кнопку Generate.\n");
                        sendMessage.setReplyMarkup(FramesController.photoBottomButton(userWithChatId.getLanguageUser(),photos.get(3).getFileUniqueId()));
                        execute(sendMessage);

                    }


                }
            } else if (update.hasCallbackQuery()) {
                String botState = DatabaseService.getBotState(update);
                Language userLanguage = DatabaseService.getUserLanguage(update);
                System.out.println("botState1" + botState + " " + update.getCallbackQuery().getMessage().getChatId());
                CallbackQuery callbackQuery = update.getCallbackQuery();
                String data = callbackQuery.getData();
                if (data.equals(Language.ENGLISH.name())) {

                    EditMessageText start = MessageService.getGreeting(update, Language.ENGLISH);
                    DatabaseService.saveLanguage(update, Language.ENGLISH);
                    execute(start);
                    Message execute = execute(MessageService.getOptionsKeyboard(update, Language.ENGLISH));
                    DatabaseService.setBotState(update, BotState.GETPHOTO);
                } else if (data.equals(Language.RUS.name())) {

                    EditMessageText start = MessageService.getGreeting(update, Language.RUS);
                    DatabaseService.saveLanguage(update, Language.RUS);
                    execute(start);
                    execute(MessageService.getOptionsKeyboard(update, Language.RUS));
                    DatabaseService.setBotState(update, BotState.GETPHOTO);

                } else if (data.equals(Language.UZBEK.name())) {
                    EditMessageText start = MessageService.getGreeting(update, Language.UZBEK);
                    DatabaseService.saveLanguage(update, Language.UZBEK);
                    execute(start);
                    execute(MessageService.getOptionsKeyboard(update, Language.UZBEK));
                    DatabaseService.setBotState(update, BotState.GETPHOTO);
                }else if (data.startsWith("generate")){
                    String botStateNew = DatabaseService.getBotState(update);
                    if (botStateNew.equals(BotState.GETPHOTO)){

                        String apiUrl = "https://api.telegram.org/bot1762336264:AAGz9-95sFr2BtygeUuPtwjb4GeSlxBBjKk/getFile?file_id=";

                        try {
                            List<String> getFilePathsUrls = new ArrayList<>();
                            Long chatId = update.getCallbackQuery().getMessage().getChatId();
                            List<List<PhotoSize>> lists = map.get(chatId);
                            if (lists != null){
                                for (List<PhotoSize> list : lists) {
                                    System.out.println(apiUrl + list.get(3).getFileId());
                                    GetFile getFile = new GetFile();
                                    System.out.println(list);
                                    getFile.setFileId(list.get(3).getFileId());
                                    File executed = execute(getFile);

                                    String filePath = executed.getFilePath();
                                    getFilePathsUrls.add(filePath);
                                }
                                SendChatAction sendChatAction = new SendChatAction().setAction(ActionType.UPLOADDOCUMENT).setChatId(chatId);
                                execute(sendChatAction);
                                execute(sendChatAction);
                                PDFGenerator pdfGenerator = new PDFGenerator();
                                pdfGenerator.generatePDF(getFilePathsUrls,update.getCallbackQuery().getMessage().getChatId());
                            }


                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setChatId(update.getCallbackQuery().getMessage().getChatId());
                            sendDocument.setCaption(userLanguage.name().equals(Language.UZBEK.name())?"PDF file tayyor ! Botimizni do'slaringizga ham ulashing":userLanguage.name().equals(Language.ENGLISH.name())?
                                    "PDF is ready ! If you like the bot, Please share to your friends":"PDF готов, если вам нравится наш бот, поделитесь им с друзьями");
                            sendDocument.setDocument(new java.io.File("src/main/resources/"+chatId+".pdf"));
                            sendDocument.setReplyToMessageId(update.getCallbackQuery().getMessage().getMessageId());
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
                            java.io.File file = new java.io.File("src/main/resources/"+chatId+".pdf");
                            if (file.delete()){
                                System.out.println("deleted");
                            }
                            if (lists != null) {
                                deletePhotos(chatId,lists.size());
                            }
                            DatabaseService.setBotState(update,BotState.START);


                        } catch (IOException | DocumentException e) {
                            e.printStackTrace();
                        }
                    }else {
                        SendMessage sendMessage = MessageService.askPhoto(update);
                        DatabaseService.setBotState(update,BotState.GETPHOTO);
                        execute(sendMessage);
                    }
                }else if (data.startsWith("delete")){
                    List<List<PhotoSize>> lists = map.get(update.getCallbackQuery().getMessage().getChatId());
                    lists.removeIf(photoSizes -> data.endsWith(photoSizes.get(3).getFileUniqueId()));
                    EditMessageText editMessageText = new EditMessageText()
                            .setChatId(update.getCallbackQuery().getMessage().getChatId())
                            .setText("❌❌❌").setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    execute(editMessageText);
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void deletePhotos(Long chatId,int size){
        for (int i = 0; i < size; i++) {
            java.io.File deletePhotoFile = new java.io.File("src/main/resources/images/"+chatId+"_"+i+".jpg");
            if (deletePhotoFile.exists()){
                boolean delete = deletePhotoFile.delete();
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public String getBotToken() {
        return "5039461659:AAGCxFgEjUkzm4ForurJxOFNBiS4qxkJTGI";
    }
}
