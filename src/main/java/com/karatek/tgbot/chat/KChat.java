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

            if(update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
                try {
                    ProcessBlacklist.process(update, name);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try {
                if(update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
                    DatabaseMetaData dbm = TGBot.connection.getMetaData();
                    String dbname = "GROUP_" + update.getMessage().getChatId().toString().replace("-", "") + "_MEMBERS";
                    ResultSet tables = dbm.getTables(null, null, dbname, null);
                    if (tables.next()) {
                    }
                    else {
                        Statement stmt = TGBot.connection.createStatement();
                        String sql = "CREATE TABLE " + dbname + " " +
                                "(id BIGINT, " +
                                " firstName VARCHAR(255), " +
                                " lastName VARCHAR(255), " +
                                " userName VARCHAR(255), " +
                                " isBot BOOLEAN, " +
                                " PRIMARY KEY ( id ))";
                        stmt.executeUpdate(sql);
                    }

                    if(!MysqlHelper.checkGroupMemberByID(update.getMessage().getFrom().getId(), dbname)) {
                        String query = " insert into " + dbname + " (id, firstName, lastName, userName, isBot)" + " values (?, ?, ?, ?, ?)";
                        PreparedStatement preparedStmt = TGBot.connection.prepareStatement(query);
                        preparedStmt.setLong(1, update.getMessage().getFrom().getId());
                        preparedStmt.setString(2, update.getMessage().getFrom().getFirstName());
                        preparedStmt.setString(3, update.getMessage().getFrom().getLastName());
                        preparedStmt.setString(4, update.getMessage().getFrom().getUserName());
                        preparedStmt.setBoolean(5, update.getMessage().getFrom().getBot());
                        preparedStmt.execute();
                        System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] Added entry to database.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            String displayname = "";
            if(update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {
                displayname = update.getMessage().getFrom().getUserName();
                String dbname = "GROUP_" + update.getMessage().getChatId().toString().replace("-", "") + "_MEMBERS";
                try {
                    String queryCheck = "SELECT * from " + dbname + " WHERE id = '" + update.getMessage().getFrom().getId() + "'";
                    Statement st = TGBot.connection.createStatement();
                    ResultSet rs = st.executeQuery(queryCheck);
                    if(rs.next()) {
                        displayname = rs.getString(2).replace("null", "") + " " + rs.getString(3).replace("null", "");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else {


                try {
                    String queryCheck = "SELECT * from CHATS WHERE id = '" + update.getMessage().getFrom().getId() + "'";
                    Statement st = TGBot.connection.createStatement();
                    ResultSet rs = st.executeQuery(queryCheck);
                    if(rs.next()) {
                        displayname = rs.getString(2).replace("null", "");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }
            if(msg.startsWith("/")) {
                System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] " + displayname + " executed command: " + update.getMessage().getText());
            } else {
                System.out.println("[LOG " + DateHelper.getCurrentTimeStamp() + "] " + displayname + " sent message: " + update.getMessage().getText());
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
                        if(update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {

                        } else {
                            MessageHelper.sendMessage(ID, messages[0].replace("/", "") + ": Command not found.");
                        }

                    }
                    break;
            }
        }
    }
}
