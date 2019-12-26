package net.karatek.tgbot.chat.commands;

import net.karatek.tgbot.utils.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

public class commandNewFilter {

    public static void execute(Update update, String dbName, String[] cmd) {
        if(update.getMessage().getChat().isSuperGroupChat() || update.getMessage().getChat().isGroupChat()) {
            MessageHelper.replyMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Not yet ready.");
        } else {
            MessageHelper.sendMessage(update.getMessage().getChatId(), "Please execute this command in a group.");
        }
    }
}
