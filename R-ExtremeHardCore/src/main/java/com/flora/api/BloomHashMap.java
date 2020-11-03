package com.flora.api;

import org.bukkit.configuration.file.FileConfiguration;

public class BloomHashMap
{
    public String key;
    public String name;
    public String uniqueID;
    public boolean penalty;
    public int role;
    public int penalty_diamond;
    public int penalty_coupon;
    public int death_count;

    BloomHashMap(String uuid)
    {
        this.key = uuid;
        onDefaultParameter(uuid);
    }

    BloomHashMap(String uuid, String name, String uniqueID, boolean penalty, int role, int penalty_diamond, int penalty_coupon, int death_count)
    {
        this.key = uuid;

        this.name = name;
        this.uniqueID = uniqueID;
        this.penalty = penalty;
        this.role = role;
        this.penalty_diamond = penalty_diamond;
        this.penalty_coupon = penalty_coupon;
        this.death_count = death_count;
    }

    void onDefaultParameter(String uuid)
    {
        FileConfiguration config = BloomAPI.getDataConfig(uuid);

        this.name = config.getString("name");
        this.uniqueID = config.getString("uuid");

        this.penalty = config.getBoolean("penalty");

        this.role = config.getInt("role");
        this.penalty_diamond = config.getInt("penalty_diamond");
        this.penalty_coupon = config.getInt("penalty_coupon");
        this.death_count = config.getInt("death_count");
    }
}
