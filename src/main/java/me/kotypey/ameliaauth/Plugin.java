package me.kotypey.ameliaauth;

import me.kotypey.AmeliaUtils.Abstracts.AmeliaPlugin;
import me.kotypey.ameliaauth.Commands.Blocker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class Plugin extends AmeliaPlugin {

    public static ArrayList<UUID> authLocked = new ArrayList<UUID>();
    public static ArrayList<UUID> registring = new ArrayList<UUID>();
    public static HashMap<UUID, String> confirming_password = new HashMap<UUID, String>();

    @Override
    public void onEnable() {
        registerEvent(new Blocker());

        setPlugin(this);

    }
}
