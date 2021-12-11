package com.google.Frames;

import com.google.enums.Language;
import com.google.templates.BotState;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FramesController {
    public static InlineKeyboardMarkup makeButtonLanguage() {
        InlineKeyboardButton uzbutton = new InlineKeyboardButton();
        uzbutton.setText("O'zbekcha \uD83C\uDDFA\uD83C\uDDFF");
        uzbutton.setCallbackData(Language.UZBEK.name());
        InlineKeyboardButton rusButton = new InlineKeyboardButton();
        rusButton.setText("Pусский \uD83C\uDDF7\uD83C\uDDFA");
        rusButton.setCallbackData(Language.RUS.name());
        InlineKeyboardButton engButton = new InlineKeyboardButton();
        engButton.setText("English \uD83C\uDDFA\uD83C\uDDF8");
        engButton.setCallbackData(Language.ENGLISH.name());

        List<InlineKeyboardButton> langList = new ArrayList<>(Arrays.asList(uzbutton, rusButton, engButton));
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(langList);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(keyboard);
        return inlineKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup optionsButton(Language language) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        KeyboardButton replyKeyboard = new KeyboardButton();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(language.name().equals(Language.ENGLISH.name()) ? "PDF Generation \uD83D\uDCD5" : language.name().equals(Language.RUS.name()) ? "ПДФ генерация \uD83D\uDCD5" : "PDF yaratish \uD83D\uDCD5");
//        keyboardRow.add(language.name().equals(Language.ENGLISH.name())?"PDF Generation":language.name().equals(Language.RUS.name())?"ПДФ генерация":"PDF yaratish")
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup photoBottomButton(Language language,String fileUniqueId) {
        InlineKeyboardButton generateButton = new InlineKeyboardButton();
        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        switch (language) {
            case ENGLISH:
                generateButton.setText("Generate ✅");
                deleteButton.setText("Delete ❌");
                break;
            case RUS:
                generateButton.setText("Генерировать ✅");
                deleteButton.setText("Удалить ❌");
                break;
            case UZBEK:
                generateButton.setText("PDF yaratish ✅");
                deleteButton.setText("O'chirish ❌");
                break;
        }
        generateButton.setCallbackData("generate");
        deleteButton.setCallbackData("delete ❌"+fileUniqueId);



        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(deleteButton);
        List<List<InlineKeyboardButton>> column = new ArrayList<>();
        column.add(buttons);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(column);
        return inlineKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup makeGenerateKeyboardButton() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        replyKeyboardMarkup.setResizeKeyboard(true);
        keyboardRow.add("Generate \uD83D\uDDC2");
        replyKeyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        return replyKeyboardMarkup;
    }
}
