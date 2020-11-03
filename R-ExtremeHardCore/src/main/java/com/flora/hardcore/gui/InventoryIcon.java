package com.flora.hardcore.gui;

import com.flora.api.BloomAPI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryIcon
{
    /* Player Data Head */
    public static ItemStack iconHead(OfflinePlayer player, String name, String uuid, boolean penalty, int role, int penalty_diamond, int penalty_coupon, int death_count, String last_date, String register_date)
    {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        assert meta != null;
        if (!(meta.hasOwner()))
            meta.setOwningPlayer(player);

        if (player.isOnline())
            meta.setDisplayName("§a" + name);
        else
            meta.setDisplayName("§c" + name);

        List<String> lore = new ArrayList<>();
        lore.add("§7직업 : " + getRoleName(role));
        lore.add("");

        if (penalty)
            lore.add(" §7벌칙자 : §c" + true);
        else
            lore.add(" §7벌칙자 : §a" + false);

        lore.add(" §7면제권 : §e" + penalty_coupon);
        lore.add(" §7벌칙다이아 : §b" + penalty_diamond);
        lore.add("");

        lore.add(" §7죽은횟수 : §c" + death_count);
        lore.add(" §7마지막 접속일 : " + last_date);
        lore.add(" §7최초 접속일 : " + register_date);
        lore.add("");

        lore.add("§7" + uuid);

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }



    /* Role */
    public static ItemStack iconRole(int role)
    {
        switch (role) {
            case 0:
                return iconRoleA();

            case 1:
                return iconRoleB();

            case 2:
                return iconRoleC();

            case 3:
                return iconRoleD();
        }

        return null;
    }


    /* Role - Normal */
    public static ItemStack iconRoleA()
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7좌클릭 : '§8지하팀§7'으로 변경");
        lore.add(" §7우클릭 : '§d매니저§7'으로 변경");

        return iconDefault(Material.GHAST_SPAWN_EGG, "§f무  직", lore);
    }

    /* Role - UnderGround */
    public static ItemStack iconRoleB()
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7좌클릭 : '§2지상팀§7'으로 변경");
        lore.add(" §7우클릭 : '§f무  직§7'으로 변경");

        return iconDefault(Material.SILVERFISH_SPAWN_EGG, "§8지하팀", lore);
    }

    /* Role - Ground */
    public static ItemStack iconRoleC()
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7좌클릭 : '§d매니저§7'으로 변경");
        lore.add(" §7우클릭 : '§8지하팀§7'으로 변경");

        return iconDefault(Material.ZOMBIE_HORSE_SPAWN_EGG, "§2지상팀", lore);
    }

    /* Role - Manager */
    public static ItemStack iconRoleD()
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7좌클릭 : '§f무  직§7'으로 변경");
        lore.add(" §7우클릭 : '§2지상팀§7'으로 변경");

        return iconDefault(Material.PIG_SPAWN_EGG, "§d매니저", lore);
    }



    /* Penalty */
    public static ItemStack iconPenalty(boolean penalty)
    {
        if (penalty) return iconPenaltyOn();
        else return iconPenaltyOff();
    }

    /* Penalty - TRUE */
    public static ItemStack iconPenaltyOn()
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7클릭 시 §c벌칙자§7를 §a해제§7합니다");

        return iconDefault(Material.RED_SHULKER_BOX, "§c벌칙자 입니다", lore);
    }

    /* Penalty - FALSE */
    public static ItemStack iconPenaltyOff()
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7클릭 시 §c벌칙자§7로 §c설정§7합니다");

        return iconDefault(Material.GREEN_SHULKER_BOX, "§a벌칙자가 아닙니다", lore);
    }



    /* Penalty Diamond */
    public static ItemStack iconDiamond(int diamond)
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7좌클릭 : 1개 추가");
        lore.add(" §7우클릭 : 1개 제거");
        lore.add(" §7쉬프트 + 좌클릭 : 10개 추가");
        lore.add(" §7쉬프트 + 우클릭 : 10개 제거");

        return iconDefault(Material.DIAMOND, "§b벌칙다이아 §f: §6" + diamond, lore);
    }



    /* Penalty Exemption Coupon */
    public static ItemStack iconPenaltyCoupon(int coupon)
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7좌클릭 : 1개 추가");
        lore.add(" §7우클릭 : 1개 제거");

        return iconDefault(Material.NAME_TAG, "§e벌칙면제권 §f: §6" + coupon, lore);
    }



    /* Death Count */
    public static ItemStack iconDeathCount(int deathCount)
    {
        List<String> lore = new ArrayList<>();
        lore.add(" §7좌클릭 : 1개 추가");
        lore.add(" §7우클릭 : 1개 제거");
        lore.add(" §7쉬프트 + 좌클릭 : 10개 추가");
        lore.add(" §7쉬프트 + 우클릭 : 10개 제거");

        return iconDefault(Material.BONE, "§c죽은횟수 §f: §6" + deathCount, lore);
    }


    /* Control Icons */
    public static ItemStack iconOperator(int i)
    {
        switch (i) {
            case 40:
                List<String> loreE = new ArrayList<>();
                loreE.add("§e팀을 선정 또는 재선정합니다");
                loreE.add("");
                loreE.add(" §7클릭 시 §6팀 선정 §7스크립트가 진행됩니다");
                loreE.add(" §d* 팀을 선정하는데 오류가 생겼거나, 재선정시 사용하시면 됩니다");

                return iconDefault(Material.ENDER_EYE, "§f팀 선정", loreE);

            case 39:
                List<String> loreB = new ArrayList<>();
                loreB.add("§e게임을 시작합니다");
                loreB.add("");
                loreB.add(" §7클릭 시 §6게임 시작 §7스크립트가 진행됩니다");
                loreB.add(" §d* 문제로 인해 게임이 진행되지 않을 시 사용합니다");
                loreB.add("");
                loreB.add(" §d* 이 스크립트 사용 시 인벤토리 §a'초기화 X'§d, §a'팀 선정 X'§d로 시작됩니다");
                loreB.add(" §d* 인벤토리를 §c'초기화'§d 하고싶다면 §e/edit tiger§d 명령어를 사용해주세요");
                return iconDefault(Material.BROWN_DYE, "§f게임 시작", loreB);

            case 38:
                List<String> loreA = new ArrayList<>();
                loreA.add("§e게임상태를 이전단계로 되돌립니다");
                loreA.add("");
                loreA.add(" §7클릭 시 §6이어하기§7 스크립트가 진행됩니다");
                loreA.add(" §d* 리셋 취소 버튼을 누른 후 이어하기를 원할 때 사용하는 버튼입니다");

                return iconDefault(Material.GOLDEN_CARROT, "§f이어하기", loreA);

            case 37:
                List<String> loreC = new ArrayList<>();
                if (BloomAPI.isRetry)
                    loreC.add("     §a✔ ");
                else
                    loreC.add("     §c✕ ");

                return iconDefault(Material.TOTEM_OF_UNDYING, "§f이어하기 여부", loreC);

            case 36:
                List<String> loreD = new ArrayList<>();
                if (BloomAPI.isReset)
                    loreD.add("    §a✔   ");
                else
                    loreD.add("    §c✕   ");

                return iconDefault(Material.BARRIER, "§f리셋 여부", loreD);

            default:
                return null;
        }
    }



    private static ItemStack iconDefault(Material material, String displayName)
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack iconDefault(Material material, String displayName, List<String> lore)
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack iconDefault(Material material, String displayName, int customModelData)
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);
        meta.setCustomModelData(customModelData);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack iconDefault(Material material, String displayName, List<String> lore, int customModelData)
    {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(displayName);
        meta.setCustomModelData(customModelData);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }


    /* Get Role Names */
    private static String getRoleName(int role)
    {
        switch (role) {
            case 0:
                return "§7무  직";

            case 1:
                return "§8지하팀";

            case 2:
                return "§2지상팀";

            case 3:
                return "§d매니저";

            default:
                return "-";
        }
    }

}
