package net.karatek.tgbot.utils;

import net.karatek.tgbot.TGBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MessageHelper {

    public static void sendMessage(long chatID, String string) {
        SendMessage message = new SendMessage().setChatId(chatID).setText(string);
        try {
            new TGBot().execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public static void replyMessage(long chatID, int messageid, String string) {
        SendMessage message = new SendMessage().setChatId(chatID).setText(string).setReplyToMessageId(messageid);
        try {
            new TGBot().execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
