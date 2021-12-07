package com.google.services;

import com.google.controller.MessageService;
import com.google.enums.Language;
import com.google.model.ChannelTg;
import com.google.model.Users;
import com.google.templates.BotState;
import com.google.templates.Result;
import org.apache.commons.codec.language.bm.Lang;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    static String dataSourceUrl = "jdbc:postgresql://ec2-54-195-246-55.eu-west-1.compute.amazonaws.com:5432/d9kjl9oq1ti7hd";
    static String userName = "liiuljqdzabjjq";
    static String password = "592add3c49d278cb164fbfe13a9c9f1f4172d717908c193306c9ff283532054c";

//    static String dataSourceUrl = "jdbc:postgresql://localhost:5432/Tg_bot_media";
//    static String userName = "postgres";
//    static String password = "husanboy2002";
    static {
        String createUserTable = "CREATE TABLE IF NOT EXISTS users\n" +
                "(\n" +
                "    id serial,\n" +
                "    user_name character varying NOT NULL,\n" +
                "    user_id bigint NOT NULL,\n" +
                "    botstate character varying NOT NULL,\n" +
                "    language_user varchar(15)," +
                "    PRIMARY KEY (id)\n" +
                ");" +
                "alter table users drop constraint if exists uq_users_chat_id;" +
                "alter table users add  constraint  uq_users_chat_id unique (user_id);" +
                "CREATE TABLE IF NOT EXISTS public.channels\n" +
                "(\n" +
                "    id serial,\n" +
                "    chat_id bigint NOT NULL,\n" +
                "    channel_username character varying NOT NULL,\n" +
                "    active boolean NOT NULL,\n" +
                "    PRIMARY KEY (id)\n" +
                ");\n" +
                "\n" +
                "";


        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(dataSourceUrl, userName, password);
            Statement statement = connection.createStatement();
            boolean execute = statement.execute(createUserTable);
            if (execute) {
                System.out.println("created table users");
            } else System.out.println("not created table users");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("xatolik");
            e.printStackTrace();
        }
        finally {
            if (connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static boolean saveUsers(Users users) {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(dataSourceUrl, userName, password);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into  users(user_name , user_id, botstate) values (?,?,?)");
            preparedStatement.setString(1, users.getUserName());
            preparedStatement.setLong(2, users.getChatId());
            preparedStatement.setString(3, users.getBotState());
            boolean execute = preparedStatement.execute();
            connection.close();
            return execute;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    public static boolean exists(Long chatId) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(dataSourceUrl, userName, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select count(users.*) from users where user_id=" + chatId);
            if (resultSet.next()) {
                int anInt = resultSet.getInt(1);
                connection.close();
                return anInt == 1;
            }
            else connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<ChannelTg> getChannels() {
        List<ChannelTg> channelTgList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(dataSourceUrl, userName, password)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from channels");
            if (resultSet.next()) {
                long chat_id = resultSet.getLong("chat_id");
                String channel_username = resultSet.getString("channel_username");
                boolean active = resultSet.getBoolean("active");
                ChannelTg channelTg = new ChannelTg(null, chat_id, channel_username, active);
                channelTgList.add(channelTg);
            }
            connection.close();
            return channelTgList;
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return null;
    }

    public static boolean saveLanguage(Update update, Language language) {
        Result result = MessageService.getChatId(update);
        Long chatId = result.getChatId();
        try (Connection connection = DriverManager.getConnection(dataSourceUrl, userName, password)){
            String query = "update users set language_user=? where user_id=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, language.name());
            statement.setLong(2, chatId);
            boolean execute = statement.execute();
            connection.close();
            return execute;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    public static Language getUserLanguage(Update update) {
        Result result = MessageService.getChatId(update);
        Long chatId = result.getChatId();
        try (Connection connection = DriverManager.getConnection(dataSourceUrl, userName, password)){
            PreparedStatement preparedStatement = connection.prepareStatement("select language_user from users where user_id=?");
            preparedStatement.setLong(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            String language = null;
            if (resultSet.next()) {
                language = resultSet.getString(1);
            }
            connection.close();
            if (language==null) return Language.UZBEK;
            switch (language) {
                case "ENGLISH":
                    return Language.ENGLISH;
                case "UZBEK":
                    return Language.UZBEK;
                case "RUS":
                    return Language.RUS;
                default:
                    return Language.UZBEK;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Language.UZBEK;
    }

    public static String getBotState(Update update) {
        Result result = MessageService.getChatId(update);
        Long chatId = result.getChatId();

        try (Connection connection = DriverManager.getConnection(dataSourceUrl, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("select botstate from users where user_id=?");
            preparedStatement.setLong(1,chatId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String string = resultSet.getString(1);
                connection.close();
                return string;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BotState.START;
    }
    public static boolean setBotState(Update update,String botState){
        Result result = MessageService.getChatId(update);
        Long chatId = result.getChatId();

        try (Connection connection = DriverManager.getConnection(dataSourceUrl, userName, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("update users set botstate=? where user_id=?");
            preparedStatement.setString(1,botState);
            preparedStatement.setLong(2,chatId);
            boolean execute = preparedStatement.execute();
            connection.close();
            return execute;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    public static Users getUserWithChatId(Update update){
        Result chatId = MessageService.getChatId(update);

        try (Connection connection = DriverManager.getConnection(dataSourceUrl,userName,password)){;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from users where user_id=" + chatId.getChatId());
            Users users = new Users();
            if (resultSet.next()){
                Integer id = resultSet.getInt("id");
                String userName = resultSet.getString("user_name");
                String botstate = resultSet.getString("botstate");
                String langauage = resultSet.getString("language_user");
                users.setId(id);
                users.setUserName(userName);
                users.setBotState(botstate);
                users.setLanguageUser(getFromString(langauage));
                users.setChatId(update.getMessage().getChatId());
                connection.close();
                return users;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Users();
    }
    public static Language getFromString(String lang){
        switch (lang) {
            case "UZBEK":
                return Language.UZBEK;
            case "ENGLISH":
                return Language.ENGLISH;
            case "RUS":
                return Language.RUS;
        }
        return null;
    }


}
