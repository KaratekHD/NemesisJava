package net.karatek.tgbot.chat.commands;

import net.karatek.tgbot.TGBot;
import net.karatek.tgbot.utils.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

public class commandTest {
    public static void execute(Update update) {
        MessageHelper.replyMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(),"Nemesis v" + TGBot.VERSION + "\n" +
                "Copyright (C) 2019 Karatek_HD.\n" +
                "Do not distribute!\n\n" +
                "Chat ID: " + update.getMessage().getChatId() + "\n" +
                "User ID: " + update.getMessage().getFrom().getId() + "\n" +
                "Update ID: " + update.getUpdateId());
    }
}
