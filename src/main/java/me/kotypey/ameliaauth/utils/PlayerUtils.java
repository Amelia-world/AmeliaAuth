package me.kotypey.ameliaauth.utils;

import me.kotypey.AmeliaUtils.Database.PluginMySQL;
import me.kotypey.ameliaauth.Exceptions.MySQLException;
import me.kotypey.ameliaauth.Plugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerUtils {

    public static boolean checkSession(Player p){
        try {
            PreparedStatement statement = PluginMySQL.mySQL.getConnection()
                    .prepareStatement("SELECT lastip FROM users where UUID=?");
            statement.setString(1, p.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            if(resultSet == null) return false;

            return resultSet.getString("lastip").equalsIgnoreCase(p.getAddress().getHostString());
        } catch (SQLException | NullPointerException e) {

            return false;

        }
    }

    public static String getPassword(Player p){
        try{
            PreparedStatement statement = PluginMySQL.mySQL.getConnection().prepareStatement("SELECT password FROM users where UUID=?");
            statement.setString(1,p.getUniqueId().toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getString("password");
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;
    }


    public static boolean isExists(Player p){
        try {
            PreparedStatement statement = PluginMySQL.mySQL.getConnection()
                    .prepareStatement("SELECT * FROM users where UUID=?");
            statement.setString(1, p.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return true;
            }else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void register(Player p, String password){
        try {
            PreparedStatement statement = PluginMySQL.mySQL.getConnection()
                    .prepareStatement("INSERT INTO users(username, password,privileges, UUID) VALUES(?,?,?,?)");
            statement.setString(1, p.getName());
            statement.setString(2, PasswordUtils.encrypt(password));
            statement.setInt(3, 3);
            statement.setString(4, p.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            p.kickPlayer(ChatColor.RED + "Призошла ошибка при добавлении вас в БД, подробности ниже: \n" + e.getMessage());
        }

    }



    public static void login(Player p){
        Plugin.authLocked.add(p.getUniqueId());
    }

    public static void setSession(Player p){
        try {
            PreparedStatement statement = PluginMySQL.mySQL.getConnection()
                    .prepareStatement("UPDATE users SET lastip=\"" +  p.getAddress().getHostString() + "\" where UUID=\"" + p.getUniqueId().toString() +"\"");
            statement.executeUpdate();
        } catch (SQLException e) {
            p.kickPlayer(ChatColor.RED + "Ошибка при установке сесии, подробности ниже:\n " + e.getMessage());
            e.printStackTrace();
        }

    }
}
