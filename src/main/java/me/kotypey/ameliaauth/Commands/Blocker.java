package me.kotypey.ameliaauth.Commands;

import me.kotypey.ameliaauth.Plugin;
import me.kotypey.ameliaauth.utils.PasswordUtils;
import me.kotypey.ameliaauth.utils.PlayerUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.mindrot.jbcrypt.BCrypt;

public class Blocker implements Listener {


    @EventHandler
    public void PreCommand(PlayerCommandPreprocessEvent e){
        if(Plugin.registring.contains((e.getPlayer().getUniqueId()))){
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Вы не авторизованы!"));
            e.setCancelled(true);
        }else if(Plugin.authLocked.contains(e.getPlayer().getUniqueId())){
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Вы не авторизованы!"));
            e.setCancelled(true);
        }else if(Plugin.confirming_password.containsKey(e.getPlayer().getUniqueId())){
            e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Вы не авторизованы!"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void JoinEvent(PlayerJoinEvent e){
        boolean playerExists = PlayerUtils.isExists(e.getPlayer());
        if(!playerExists){
            e.getPlayer().spigot().sendMessage(ChatMessageType.SYSTEM, TextComponent.fromLegacyText(ChatColor.RED + "Вам необходимо зарегестрироватся"));
            Plugin.registring.add(e.getPlayer().getUniqueId());
        }else{
            boolean session = PlayerUtils.checkSession(e.getPlayer());
            if(session){
                e.getPlayer().sendMessage(ChatColor.GREEN + "Вы автоматически авторизированы!");
            }else{
                Plugin.authLocked.add(e.getPlayer().getUniqueId());
            }

        }

    }


    @EventHandler
    public void ChatEvent(PlayerChatEvent e){
        if(Plugin.registring.contains((e.getPlayer().getUniqueId()))){
            String password = e.getMessage();
            if(password.length() < 8){
                Player player  = e.getPlayer();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Пароль должен быть больше 8-и символов!"));
                e.setCancelled(true);
            }else{
                e.getPlayer().sendMessage(ChatColor.RED + "Теперь повторите пароль!");
                Plugin.confirming_password.put(e.getPlayer().getUniqueId(), e.getMessage());
                Plugin.registring.remove(e.getPlayer().getUniqueId());
                e.setCancelled(true);
            }
        }else if(Plugin.confirming_password.containsKey(e.getPlayer().getUniqueId())){
            String rpassword = e.getMessage();
            String password = Plugin.confirming_password.get(e.getPlayer().getUniqueId());
            if(rpassword.equalsIgnoreCase(password)){
                PlayerUtils.register(e.getPlayer(), password);
                e.getPlayer().sendMessage(ChatColor.RED + "Вы успешно зарегестрированы!");
                Plugin.confirming_password.remove(e.getPlayer().getUniqueId());
                e.setCancelled(true);
            }else {
                Plugin.confirming_password.remove(e.getPlayer().getUniqueId());
                e.getPlayer().kickPlayer(ChatColor.RED + "Пароли не совпадают!\n Попробуйте заново");
            }
        }else if(Plugin.authLocked.contains(e.getPlayer().getUniqueId())){
            String password = PlayerUtils.getPassword(e.getPlayer());
            //TODO: Кеширование паролей

            String md5 = PasswordUtils.getMd5(e.getMessage());
            boolean isright = BCrypt.checkpw(md5, password);
            if(isright){
                PlayerUtils.setSession(e.getPlayer());
                Plugin.authLocked.remove(e.getPlayer().getUniqueId());
                e.getPlayer().sendMessage(ChatColor.GREEN + "Вы успешно авторизированы!");
            }
            e.setCancelled(true);

        }
    }


    @EventHandler
    public void Leave(PlayerQuitEvent e){
        if(Plugin.authLocked.contains(e.getPlayer().getUniqueId())){
            Plugin.authLocked.remove(e.getPlayer().getUniqueId());
        }
        if(Plugin.registring.contains(e.getPlayer().getUniqueId())){
            Plugin.registring.remove(e.getPlayer().getUniqueId());
        }
        if(Plugin.confirming_password.containsKey(e.getPlayer().getUniqueId())){
            Plugin.confirming_password.remove(e.getPlayer().getUniqueId());
        }
    }


    @EventHandler
    public void breakblock(BlockBreakEvent e){
        Player player = e.getPlayer();
        if(Plugin.authLocked.contains(player.getUniqueId()) || Plugin.registring.contains(player.getUniqueId()) || Plugin.confirming_password.containsKey(player.getUniqueId())){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Вам необходимо авторизироватся!"));
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void placeBlock(BlockPlaceEvent e){
        Player player = e.getPlayer();
        if(Plugin.authLocked.contains(player.getUniqueId()) || Plugin.registring.contains(player.getUniqueId()) || Plugin.confirming_password.containsKey(player.getUniqueId())){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Вам необходимо авторизироватся!"));
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void damageEv(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player player = (Player)e.getEntity();
            if(Plugin.authLocked.contains(player.getUniqueId()) || Plugin.registring.contains(player.getUniqueId()) || Plugin.confirming_password.containsKey(player.getUniqueId())){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Вам необходимо авторизироватся!"));
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void MoveEvent(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if(Plugin.authLocked.contains(player.getUniqueId()) || Plugin.registring.contains(player.getUniqueId()) || Plugin.confirming_password.containsKey(player.getUniqueId())){
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "Вам необходимо авторизироватся!"));
            e.setCancelled(true);
        }
    }
}
