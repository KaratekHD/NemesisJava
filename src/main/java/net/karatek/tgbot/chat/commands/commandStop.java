package net.karatek.tgbot.chat.commands;

import net.karatek.tgbot.TGBot;
import net.karatek.tgbot.utils.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.SQLException;

public class commandStop {

    public static void execute(Update update) {
        if(TGBot.admins.contains(update.getMessage().getFrom().getId().toString())) {
            MessageHelper.replyMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Shutting down...");
            TGBot.logger.info("Shutting down...");
            try {
                TGBot.connection.close();
            } catch (SQLException e) {
                TGBot.logger.error(e.getMessage());
            }
            System.exit(0);
        } else {
            MessageHelper.replyMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Do you really think I'd let you do that?");
        }

    }

}
