package com.karatek.tgbot.utils;

import com.karatek.tgbot.TGBot;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlHelper {

    public static boolean checkChat(long ID) throws SQLException {
        String queryCheck = "SELECT * from CHATS WHERE id = " + ID;
        Statement st = TGBot.connection.createStatement();
        ResultSet rs = st.executeQuery(queryCheck); // execute the query, and get a java resultset

        // if this ID already exists, we quit
        if(rs.absolute(1)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkString(String dbName, String column, String pattern) throws SQLException {
        String queryCheck = "SELECT * from " + dbName + " WHERE " + column + " = '" + pattern + "'";
        Statement st = TGBot.connection.createStatement();
        ResultSet rs = st.executeQuery(queryCheck);
        if(rs.absolute(1)) {
            return true;
        } else {
            return false;
        }
    }
}
