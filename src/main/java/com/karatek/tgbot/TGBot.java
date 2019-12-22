package com.karatek.tgbot;

import com.karatek.tgbot.chat.KChat;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.ArrayList;

public class TGBot extends TelegramLongPollingBot {

    public static final String VERSION = "0.1 Alpha";

    private static String host = "localhost";
    private static int port = 3306;
    private static String user = "root";
    private static String password = "9wTEfjyg9EmNGtRt";
    private static String database = "tgbot";

    public static Connection connection;

    public static ArrayList<String> admins = new ArrayList<String>();

    public void onUpdateReceived(Update update) {
        KChat.handleUpdateEvent(update);
    }

    public String getBotUsername() {
        return "karatekbot";
    }

    public String getBotToken() {
        return Token.TOKEN;
    }

    public static void main(String[] args) {
        admins.add("Karatek_HD");
        admins.add("GesamteVonNyx");
        ApiContextInitializer.init();

        try {
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            System.out.println("Done!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new TGBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }





        try {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "employee", null);
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
