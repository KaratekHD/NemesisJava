package com.karatek.tgbot.chat.commands;

import com.karatek.tgbot.utils.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

public class commandNewFilter {

    public static void execute(Update update, String dbName, String[] cmd) {
        if(update.getMessage().getChat().isSuperGroupChat() || update.getMessage().getChat().isGroupChat()) {

        } else {
            MessageHelper.sendMessage(update.getMessage().getChatId(), "Please execute this command in a group.");
        }
    }
}
