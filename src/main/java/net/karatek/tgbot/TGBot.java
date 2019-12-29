package net.karatek.tgbot;

import net.karatek.tgbot.chat.KChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.ArrayList;

public class TGBot extends TelegramLongPollingBot {

    public static ArrayList<String> admins = new ArrayList<>();
    public static final String VERSION = "0.1 Alpha";

    private static String host = "localhost";
    private static int port = 3306;
    private static String user = "root";
    private static String password = "9wTEfjyg9EmNGtRt";
    private static String database = "tgbot";

    public static Connection connection;

    public static final Logger logger = LogManager.getLogger(TGBot.class);

    public void onUpdateReceived(Update update) {
        new Thread(() -> {
            try {
                KChat.handleUpdateEvent(update);
            } catch (NullPointerException e) {
                logger.warn("NullPointerException: " + e.getMessage());
            }

        }).start();
    }

    public String getBotUsername() {
        return "karatekbot";
    }

    public String getBotToken() {
        return Token.TOKEN;
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();

        admins.add("540549815"); // Karatek_HD
        admins.add("857177538"); // Nyx

        try {
            logger.info("Connecting to database...");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            logger.info("Done!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new TGBot());
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
        }

        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "CHATS", null);
            if (tables.next()) {
            }
            else {
                Statement stmt = connection.createStatement();
                String sql = "CREATE TABLE CHATS " +
                        "(id BIGINT, " +
                        " name VARCHAR(255), " +
                        " type VARCHAR(255), " +
                        " PRIMARY KEY ( id ))";

                stmt.executeUpdate(sql);
            }

        } catch (SQLException e) {

        }




    }



}
