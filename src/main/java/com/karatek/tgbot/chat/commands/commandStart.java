package com.karatek.tgbot.chat.commands;

import com.karatek.tgbot.utils.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

public class commandStart {

    public static void execute(Update update) {
        MessageHelper.replyMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Hey there!\n" +
                "This is Nemesis, a group management bot created by @Karatek_HD.\n" +
                "Currently, it's an early development preview.");
    }
}
