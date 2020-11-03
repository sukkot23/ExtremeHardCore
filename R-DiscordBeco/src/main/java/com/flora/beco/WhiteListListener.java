package com.flora.beco;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhiteListListener extends ListenerAdapter
{
    /* Channel ID */
    public static long CHANNEL = 719144116042072145L;
    /* Role ID */
    public static long CERTIFIED_USER = 725041367432167434L;
    public static long EVERYONE_USER = 400891180587417600L;

    @Override
    public void onReady(ReadyEvent event)
    {
        System.out.println("§9[Discord] Discord Bot Connected");
        M_Discord.jda = event.getJDA();

        setChannelRolePermission(event.getJDA(), true);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onMessageReceived(MessageReceivedEvent event)
    {
        /* Bot Message Cancel */
        if (event.getAuthor().isBot()) { return; }
        /* Check WhiteList Channel */
        if (!(event.getChannel().getIdLong() == CHANNEL)) { return; }

        String[] form = event.getMessage().getContentRaw().split("/");

        if (form.length == 2)
        {
            String minecraftName = form[0];
//          String twitchName = form[1];
            String message = "§9[Discord] §7" + minecraftName + "을(를) 화이트리스트에 추가하였습니다";

            if (getStatusMojangAPI() == 3 || getStatusMojangAPI() == 0) { sendWaringMessage(5, event); return; }
            if (!(isMatchUserName(minecraftName))) { sendWaringMessage(1, event); return; }

            OfflinePlayer player = Bukkit.getOfflinePlayer(minecraftName);
            if (player.isWhitelisted()) { sendWaringMessage(3, event); return; }

            sendCompleteMessage(message, player, event);
        }
        else {
            sendWaringMessage(0, event);
        }
    }

    public static void setChannelRolePermission(JDA jda, boolean value)
    {
        GuildChannel channel = jda.getGuildChannelById(CHANNEL);
        Role role = jda.getRoleById(EVERYONE_USER);

        assert channel != null;
        assert role != null;

        if (value) {
            channel.putPermissionOverride(role).setDeny(Permission.MESSAGE_HISTORY).setAllow(Permission.MESSAGE_WRITE).queue();
        }
        else {
            channel.putPermissionOverride(role).setDeny(Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE).queue();
        }
    }

    private void sendCompleteMessage(String message, OfflinePlayer player, MessageReceivedEvent event)
    {
        event.getMessage().addReaction("U+1F3F7").queue();
        event.getGuild().addRoleToMember(event.getAuthor().getIdLong(), Objects.requireNonNull(event.getJDA().getRoleById(CERTIFIED_USER))).queue();

        for (Player p : Bukkit.getOnlinePlayers())
        {
            if (p.isOp()) {
                p.sendMessage(message);
                System.out.println(message);
            }
        }

        player.setWhitelisted(true);
    }

    /*
     * 0 : Bad Form
     * 1 : Not Search Minecraft Account ID
     * 2 : Not Search Twitch Account ID
     * 3 : Already register User
     * 4 : 404
     * 5 : API Server Error
     */
    private void sendWaringMessage(int errorCode, MessageReceivedEvent event)
    {
        User user = event.getAuthor();
        Member member = event.getMember();
        TextChannel channel = event.getTextChannel();
        Message message = event.getMessage();

        switch (errorCode) {

            /* Bad Form */
            case 0:
                String cause0 = "양식에 맞지 않게 작성되었습니다";
                assert member != null;
                channel.sendMessage(user.getAsMention()).embed(embedWaring(cause0, user, member, message)).queue();
                message.delete().queue();
                break;

            /* Not Search Minecraft Account ID */
            case 1:
                String cause1 = "마인크래프트 계정을 찾을 수 없습니다";//
                assert member != null;
                channel.sendMessage(user.getAsMention()).embed(embedWaring(cause1, user, member, message)).queue();
                message.delete().queue();
                break;

            /* Not Search Twitch Account ID */
            case 2:
                String cause2 = "트위치 계정을 찾을 수 없습니다";
                assert member != null;
                channel.sendMessage(user.getAsMention()).embed(embedWaring(cause2, user, member, message)).queue();
                message.delete().queue();
                break;

            /* Already register User */
            case 3:
                String cause3 = "이미 화이트리스트에 등록되어 있습니다";
                assert member != null;
                channel.sendMessage(user.getAsMention()).embed(embedWaring(cause3, user, member, message)).queue();
                message.delete().queue();
                break;

            /* 404 */
            case 4:
                String cause4 = "알 수 없는 오류";
                assert member != null;
                channel.sendMessage(user.getAsMention()).embed(embedWaring(cause4, user, member, message)).queue();
                message.delete().queue();
                break;

            /* API Server Error */
            case 5:
                String cause5 = "모장 API 서버에 접속할 수 없습니다";
                assert member != null;
                channel.sendMessage(user.getAsMention()).embed(embedWaring(cause5, user, member, message)).queue();
                message.delete().queue();
                break;
        }
    }

    private MessageEmbed embedWaring(String cause, User user, Member member, Message message)
    {
        String hashTag = "(#" + user.getDiscriminator() + ")";

        String name = (member.getNickname() != null) ? member.getNickname() : user.getName();
        String iconUrl = (user.getAvatarUrl() != null) ? user.getAvatarUrl() : user.getDefaultAvatarUrl();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(Color.RED);
        builder.setAuthor(name + hashTag, null, iconUrl);

        builder.setTitle(cause);

        builder.setDescription("឵ ឵឵ ឵신청양식 확인 후 다시 작성해주세요" + "\n឵ ឵");

        builder.addField("[제출 내용]", message.getContentRaw() + "\n ឵ ឵", true);

        builder.setFooter("양식 위반 5회 이상 시 강제퇴장 조치가 이루어질 수 있습니다");

        return builder.build();
    }

    /* URL Connect */
    protected String urlConnector(String link)
    {
        String result = "";

        try {
            URL url = new URL(link);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(300);
            connection.connect();

            InputStreamReader iReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader bReader = new BufferedReader(iReader);

            String line;
            while ((line = bReader.readLine()) != null) result = line;

            bReader.close();
            connection.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("§c[Discord] 잘못된 형식의 URL 입니다");
        } catch (IOException e) {
            System.out.println("§c[Discord] 페이지를 읽을 수 없습니다");
        }

        return result;
    }

    /* Mojang API */
    /*
     * 0 : Error
     * 1 : Green
     * 2 : Yellow
     * 3 : Red
     */
    public int getStatusMojangAPI()
    {
        String mojangAPI_URL = "https://status.mojang.com/check";

        String result = urlConnector(mojangAPI_URL);
        if (result == null || result.isEmpty()) { return 0; }

        String editTexts = result.substring(1).substring(0, result.length() - 2);

        Pattern pattern = Pattern.compile("\"api.mojang.com\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(editTexts);

        if (!(matcher.find())) { return 0; }

        switch (matcher.group(1)) {
            case "green":
                return 1;

            case "yellow":
                return 2;

            case "red":
                return 3;

            default:
                return 0;
        }
    }

    private boolean isMatchUserName(String name)
    {
        String mojangAPI_URL = "https://api.mojang.com/users/profiles/minecraft/" + name;

        String result = urlConnector(mojangAPI_URL);
        if (result.isEmpty()) { return false; }

        String[] editTexts = result.substring(1).substring(0, result.length() - 2).split(",");

        Pattern pattern = Pattern.compile("\"(.*?)\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(editTexts[0]);

        return matcher.find();
    }
}
