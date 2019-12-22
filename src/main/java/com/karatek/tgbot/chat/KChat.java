package com.karatek.tgbot.chat;

import com.karatek.tgbot.TGBot;
import com.karatek.tgbot.chat.commands.commandStart;
import com.karatek.tgbot.chat.commands.commandTest;
import com.karatek.tgbot.utils.DateHelper;
import com.karatek.tgbot.utils.MessageHelper;
import com.karatek.tgbot.utils.MysqlHelper;
import com.karatek.tgbot.utils.ProcessBlacklist;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.*;

public class KChat {

    private static TGBot main = new TGBot();

    public static void handleUpdateEvent(Update update) {
        update.getMessage().getFrom().getUserName();

        long ID = update.getMessage().getChatId();
        String name = "GROUP_" + update.getMessage().getChatId().toString().replace("-", "") + "_BLACKLIST";
        try {
            if(MysqlHelper.checkChat(ID)) {
                System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] Database entry exists.");
            } else {
                String query = " insert into CHATS (id, name, type)" + " values (?, ?, ?)";
                PreparedStatement preparedStmt = TGBot.connection.prepareStatement(query);


                if(update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {

                    try {
                        Statement stmt = TGBot.connection.createStatement();

                        String sql = "CREATE TABLE `" + name + "` (" +
                                "`name` MEDIUMTEXT CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '',\n" +
                                "`delete` BOOLEAN," +
                                "`answer` BOOLEAN," +
                                "`string` MEDIUMTEXT CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT ''" +
                                ")";

                        stmt.executeUpdate(sql);
                        System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] Created database table " + name);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    preparedStmt.setLong(1, update.getMessage().getChatId());
                    preparedStmt.setString(3, "GROUP");
                    preparedStmt.setString(2, update.getMessage().getChat().getTitle());



                } else {
                    preparedStmt.setLong(1, ID);
                    preparedStmt.setString(3, "USER");
                    preparedStmt.setString(2, update.getMessage().getChat().getFirstName() + " " + update.getMessage().getChat().getLastName());
                }

                preparedStmt.execute();
                System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] Added entry to database.");


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String msg = update.getMessage().getText();
        if (update.hasMessage() && update.getMessage().hasText()) {
            if(msg.startsWith("/")) {
                System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] " + update.getMessage().getFrom().getUserName() + " executed command: " + update.getMessage().getText());
            } else {
                System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] " + update.getMessage().getFrom().getUserName() + " sent message: " + update.getMessage().getText());
            }
            if(update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
                try {
                    ProcessBlacklist.process(update, name);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            String[] messages = msg.split(" ");
            switch (messages[0].replace("@karatekbot", "")) {
                case "/start":
                    commandStart.execute(update);
                    break;
                case "/test":
                    commandTest.execute(update);
                    break;
                default:
                    if(messages[0].startsWith("/")) {
                        MessageHelper.sendMessage(ID, messages[0].replace("/", "") + ": Command not found.");
                    }
                    break;
            }
        }
    }
}
