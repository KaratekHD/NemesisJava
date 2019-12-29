package net.karatek.tgbot.chat.commands;

import net.karatek.tgbot.utils.MessageHelper;
import org.telegram.telegrambots.meta.api.objects.Update;

public class commandStart {

    public static void execute(Update update) {
        MessageHelper.replyMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Hey there!\n" +
                "This is Nemesis, a group management bot created by @Karatek_HD.\n" +
                "Currently, it's an early development preview.\n" +
                "WARNING: If you wish to use this bot, please build it by yourself and host it on your own machine.\n" +
                "You can find the source code at https://github.com/KaratekHD/Nemesis .")
        ;
    }
}
