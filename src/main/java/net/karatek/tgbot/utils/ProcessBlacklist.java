package net.karatek.tgbot.utils;

import net.karatek.tgbot.TGBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcessBlacklist {

    public static void process(Update update, String dbTable) throws SQLException {

        PreparedStatement stat = TGBot.connection.prepareStatement(
        "SELECT * FROM " + dbTable,
        ResultSet.TYPE_FORWARD_ONLY,
        ResultSet.CONCUR_READ_ONLY);
        stat.setFetchSize(Integer.MIN_VALUE);
        ResultSet results = stat.executeQuery();
        while (results.next()) {
            String name = results.getString("name");
            Boolean delete = results.getBoolean("delete");
            Boolean answer = results.getBoolean("answer");
            String string = results.getString("string");
            TGBot.logger.debug(name + delete + answer + string);
        }
    }
}
