package com.flora.hardcore.command;

import com.flora.api.BloomAPI;
import com.flora.hardcore.gui.InventoryPlayerEdit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class CommandEdit implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender.isOp())) { sender.sendMessage("§cDo Not use Command"); return false; }

        if (args.length > 0) {
            switch (args[0]) {
                case "help":
                    commandHelper(sender, label);
                    break;

                case "try":
                    commandTry(sender, args);
                    break;

                case "life":
                    commandLife(sender, args);
                    break;

                case "game":
                    commandGame(sender, args);
                    break;

                case "player":
                    commandPlayerEdit(sender, args);
                    break;

                case "move":
                    BloomAPI.changeIsMove();
                    break;

                case "tiger":
                    if (sender instanceof Player)
                        ((Player) sender).getInventory().addItem(itemTigerHair());
                    break;

                default:
                    sender.sendMessage("§6/" + label + "§7 help");
                    break;
            }
        }
        else {
            sender.sendMessage("§6/" + label + "§7 help");
        }
        return false;
    }

    private void commandHelper(CommandSender sender, String label)
    {
        sender.sendMessage("");
        sender.sendMessage("§e---------------[ §bExtreme Hard Core Command §e]---------------");
        sender.sendMessage("§6/" + label + "§7 try (count) : §f게임 도전횟수를 조정합니다");
        sender.sendMessage("§6/" + label + "§7 life (count) : §f게임 라이프를 조정합니다");
        sender.sendMessage("§6/" + label + "§7 game (state) : §f게임 상태를 조정합니다");
        sender.sendMessage("§6/" + label + "§7 player [name] : §f플레이어 데이터를 조정합니다");
        sender.sendMessage("");
    }

    private void commandTry(CommandSender sender, String[] args)
    {
        if (args.length < 2) { sender.sendMessage("현재 도전횟수는 §6" + BloomAPI.getServerConfig().getInt("tryCount") + "§f 입니다"); return; }
        try {
            int editCount = Integer.parseInt(args[1]);
            if (editCount < 0) { sender.sendMessage("§c0미만의 값은 가질 수 없습니다"); return;}

            BloomAPI.getServerConfig().set("tryCount", editCount);
            BloomAPI.PLUGIN.saveConfig();

            sender.sendMessage("§e게임 도전횟수가 수정되었습니다 §7(현재 도전횟수 : §8" + editCount + "§7)");
        } catch (NumberFormatException exception) {
            sender.sendMessage("§c숫자외에 다른 문자는 입력할 수 없습니다");
        }
    }

    private void commandLife(CommandSender sender, String[] args)
    {
        if (args.length < 2) { sender.sendMessage("현재 라이프는 §c❤ x" + BloomAPI.getServerConfig().getInt("life") + "§f 입니다"); return; }
        try {
            int editCount = Integer.parseInt(args[1]);
            if (editCount < 0) { sender.sendMessage("§c0미만의 값은 가질 수 없습니다"); return;}

            BloomAPI.getServerConfig().set("life", editCount);
            BloomAPI.PLUGIN.saveConfig();

            sender.sendMessage("§e전체 라이프가 수정되었습니다 §7(현재 라이프 : §c❤ x" + editCount + "§7)");
        } catch (NumberFormatException exception) {
            sender.sendMessage("§c숫자외에 다른 문자는 입력할 수 없습니다");
        }
    }

    private void commandGame(CommandSender sender, String[] args)
    {
        if (args.length < 2)  {
            commandGameState(sender);
            return;
        }

        switch (args[1]) {
            case "state":
                BloomAPI.changeGameState();

                if (BloomAPI.gameState)
                    sender.sendMessage("§e게임 시작 상태 :§a TRUE");
                else
                    sender.sendMessage("§e게임 시작 상태 :§c FALSE");
                break;

            case "retry":
                BloomAPI.changeIsRetry();

                if (BloomAPI.isRetry)
                    sender.sendMessage("§e리트라이 여부 :§a TRUE");
                else
                    sender.sendMessage("§e리트라이 여부 :§c FALSE");
                break;

            case "reset":
                BloomAPI.changeIsReset();

                if (BloomAPI.isReset)
                    sender.sendMessage("§e리셋 여부 :§a TRUE");
                else
                    sender.sendMessage("§e리셋 여부 :§c FALSE");
                break;

            default:
                commandGameState(sender);
                break;
        }
    }

    private void commandGameState(CommandSender sender)
    {
        sender.sendMessage("");
        sender.sendMessage("§e---------------[ §fGame State §e]---------------");

        if (BloomAPI.gameState)
            sender.sendMessage("§e게임 시작 상태 :§a TRUE");
        else
            sender.sendMessage("§e게임 시작 상태 :§c FALSE");

        if (BloomAPI.isRetry)
            sender.sendMessage("§e리트라이 여부 :§a TRUE");
        else
            sender.sendMessage("§e리트라이 여부 :§c FALSE");

        if (BloomAPI.isReset)
            sender.sendMessage("§e리셋 여부 :§a TRUE");
        else
            sender.sendMessage("§e리셋 여부 :§c FALSE");

        sender.sendMessage("");
    }

    private void commandPlayerEdit(CommandSender sender, String[] args)
    {
        if (isConsole(sender)) { System.out.println("§cDo Not Use Command For Console"); return; }
        Player player = (Player) sender;

        if (args.length < 2) { player.sendMessage("§c플레이어 이름을 입력해주세요"); return; }
        if (!(matchPlayerName(args[1]))) { player.sendMessage("§c플레이어를 찾을 수 없습니다"); return; }

        String fileName = Objects.requireNonNull(BloomAPI.getDataFileToName(args[1])).getName();
        UUID uuid = UUID.fromString(fileName.substring(0, fileName.length() - 4));

        player.openInventory(new InventoryPlayerEdit(Bukkit.getOfflinePlayer(uuid)).inv());
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender.isOp())) { sender.sendMessage("§cDo Not use Command"); return null; }

        List<String> empty = new ArrayList<String>() {{ add(""); }};
        String[] commands = { "try", "life", "game", "player", "tiger" };

        if (args.length > 0) {
            switch (args[0]) {
                case "try":
                case "life":
                    if (args.length == 2)
                        return new ArrayList<String>() {{ add("(Number)"); }};
                    else
                        return empty;

                case "game":
                    if (args.length == 2)
                        return tabCompleteSort(new ArrayList<String>() {{ add("state"); add("retry"); add("reset"); }}, args[1]);
                    else
                        return empty;

                case "player":
                    if (args.length == 2)
                        return tabCompleteSort(tabCommandPlayerEdit(), args[1]);
                    else
                        return empty;

                case "tiger":
                    return empty;

                default:
                    return tabCommandMain(commands, args[0]);
            }
        } else {
            return empty;
        }
    }

    private List<String> tabCommandMain(String[] commands, String args)
    {
        List<String> list = new ArrayList<>(Arrays.asList(commands));

        return tabCompleteSort(list, args);
    }

    private List<String> tabCommandPlayerEdit()
    {
        List<String> list = new ArrayList<>();

        for (File file : BloomAPI.getDataFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (config.isString("name"))
                list.add(config.getString("name"));
        }

        Collections.sort(list);

        return list;
    }

    private List<String> tabCompleteSort(List<String> list, String args)
    {
        List<String> sortList = new ArrayList<>();
        for (String s : list)
        {
            if (args.isEmpty()) return list;

            if (s.toLowerCase().startsWith(args.toLowerCase()))
                sortList.add(s);
        }
        return sortList;
    }




    /* Game Control Item */
    public static ItemStack itemTigerHair()
    {
        ItemStack item = new ItemStack(Material.BROWN_DYE);
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();
        lore.add("§a");
        lore.add("§7 왼손에 장착시 게임이 시작됩니다");

        assert (meta != null);
        meta.setDisplayName("§6§l호랭이 털");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }



    private boolean isConsole(CommandSender sender)
    {
        return !(sender instanceof Player);
    }

    private boolean matchPlayerName(String playerName)
    {
        try {
            return Objects.requireNonNull(BloomAPI.getDataFileToName(playerName)).canRead();
        } catch (NullPointerException exception) {
            return false;
        }
    }
}
