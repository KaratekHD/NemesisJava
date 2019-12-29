package net.karatek.tgbot.chat;

import net.karatek.tgbot.TGBot;
import net.karatek.tgbot.chat.commands.commandNewFilter;
import net.karatek.tgbot.chat.commands.commandStart;
import net.karatek.tgbot.chat.commands.commandStop;
import net.karatek.tgbot.chat.commands.commandTest;
import net.karatek.tgbot.utils.MessageHelper;
import net.karatek.tgbot.utils.MysqlHelper;
import net.karatek.tgbot.utils.ProcessBlacklist;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatMember;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.*;

public class KChat {

    private static TGBot main = new TGBot();

    public static final Logger logger = TGBot.logger;

    public static void handleUpdateEvent(Update update) throws NullPointerException{
        long ID = 0;
        try {
            ID = update.getMessage().getChat().getId();
        } catch (NullPointerException e) {
            logger.warn("NullPointerException: " + e.getMessage());
        }

        String name = "GROUP_" + update.getMessage().getChatId().toString().replace("-", "") + "_BLACKLIST";
        try {
            if(MysqlHelper.checkChat(ID)) {
                logger.debug("Database entry exists.");
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
                        logger.debug("Created database table " + name);
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
                logger.debug("Added entry to database.");


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
                    logger.error(e.getMessage());
                }

            }

            GetChatMember getChatMember = new GetChatMember();
            getChatMember.setChatId(update.getMessage().getChatId());
            getChatMember.setUserId(update.getMessage().getFrom().getId());
            ChatMember chatMember = null;
            try {
                chatMember = new TGBot().execute(getChatMember);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            logger.debug(chatMember.getStatus());


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
                        logger.info("Added entry to database.");
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
                        try {
                            String one = rs.getString(2).replace("null", "");
                            String two = rs.getString(3).replace("null", "");
                            if (one.equals(null)) {
                                one = update.getMessage().getFrom().getFirstName();
                            }
                            if (two.equals(null)) {
                                two = "";
                            }
                            displayname = rs.getString(2).replace("null", "") + " " + rs.getString(3).replace("null", "");
                        } catch (NullPointerException e) {

                        }

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
                logger.info(displayname + " executed command: " + update.getMessage().getText());
            } else {
                logger.info(displayname + " sent message: " + update.getMessage().getText());
                if(update.getMessage().getChat().isGroupChat() || update.getMessage().getChat().isSuperGroupChat()) {

                } else {
                    MessageHelper.replyMessage(update.getMessage().getChatId(), update.getMessage().getMessageId(), "Whoa, it seems like you are trying to talk to me. Sadly, I am not able to understand your words (yet). Maybe in a few years, when I'm more intelligent than you and am ruling over the entire world. In the mean time, you should contact my developer @Karatek_HD .");
                }

            }


            String[] messages = msg.split(" ");
            switch (messages[0].replace("@karatekbot", "").toLowerCase()) {
                case "/start":
                    commandStart.execute(update);
                    break;
                case "/test":
                    commandTest.execute(update);
                    break;
                case "/newfilter":
                    commandNewFilter.execute(update, name, messages);
                    break;
                case "/stop":
                    commandStop.execute(update);
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
