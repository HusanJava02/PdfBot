package com.google.controller;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatMember;

public class SubscriberController {
    public static GetChatMember setValuesGetChatMember(Long chatId, Integer userId){
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId(chatId.toString());
        getChatMember.setUserId(Long.valueOf(userId));
        return getChatMember;
    }
    public static boolean isSubscribed(ChatMember chatMember){
        if(chatMember.getStatus().equals("member")
                || chatMember.getStatus().equals("administrator")
                || chatMember.getStatus().equals("restricted")
                || chatMember.getStatus().equals("creator")){
            return true;
        }else return false;
    }
}
