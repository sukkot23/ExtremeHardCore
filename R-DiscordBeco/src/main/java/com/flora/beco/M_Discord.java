package com.flora.beco;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

public class M_Discord extends JavaPlugin
{
    public static String TOKEN = "";
    public static JDA jda;
    boolean isTOKEN;

    @Override
    public void onEnable()
    {
        saveDefaultConfig();
        String configTOKEN = getConfig().getString("TOKEN");
        assert configTOKEN != null;

        if (configTOKEN.isEmpty()) {
            shutdown("§9[Discord] §cDiscord TOKEN Not Found");
        } else {
            TOKEN = getConfig().getString("TOKEN");
            onBuildJDA();
        }
    }

    @Override
    public void onDisable()
    {
        if (isTOKEN)  {
            WhiteListListener.setChannelRolePermission(jda, false);

            jda.shutdown();
            System.out.println("§9[Discord] Discord Bot Disconnected");
        }
    }

    public void onBuildJDA()
    {
        JDABuilder builder = JDABuilder.createLight(TOKEN);

        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);
        builder.setActivity(Activity.of(Activity.ActivityType.WATCHING, "화이트 리스트"));

        builder.addEventListeners(new WhiteListListener());

        try { builder.build(); isTOKEN = true; }
        catch (LoginException e) { shutdown("§9[Discord] §cDiscord TOKEN Not valid"); }
    }

    public static void shutdown(String message)
    {
        System.out.println(message);
        Bukkit.getPluginManager().disablePlugin(JavaPlugin.getPlugin(M_Discord.class));
    }
}
