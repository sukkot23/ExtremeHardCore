package com.flora.penalty.event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.flora.api.BloomAPI;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class EventPenaltyDiamond implements Listener
{
    @EventHandler
    public void eatingDiamondEvent(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!(isPenalty(uuid))) return;
        if (!(isUseItem(event))) return;

        onUseDiamond(player, uuid, Objects.requireNonNull(event.getItem()));
    }

    private void onUseDiamond(Player player, String uuid, ItemStack item)
    {
        int getDiamond = BloomAPI.getHashMapPenaltyDiamond(uuid);
        int payDiamond = getDiamond - 1;

        item.setAmount(item.getAmount() - 1);
        BloomAPI.setHashMapPenaltyDiamond(uuid, payDiamond);
        EventReloadScore.onReloadScore(player);

        if (payDiamond < 1)
            onLiberationPenalty(player, uuid);

        if (payDiamond % 5 == 0 && payDiamond != 0)
            player.sendMessage("  §b♢ §r남은 다이아 : §6" + payDiamond + "개");

        eatingEffect(player);
    }

    private void onLiberationPenalty(Player player, String uuid)
    {
        BloomAPI.setHashMapPenalty(uuid, false);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam reload");
        liberationEffect(player);
    }

    private void eatingEffect(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();

        double r = 0.5;

        Vector direction = loc.getDirection().clone();
        Location result = loc.add(direction.multiply(r)).add(0, player.getEyeHeight() - 0.3, 0);

        assert world != null;
        world.spawnParticle(Particle.ITEM_CRACK, result, 5, 0.05, 0.1, 0.05, 0.04, new ItemStack(Material.DIAMOND));
        world.playSound(loc, Sound.ENTITY_PLAYER_BURP, 0.8f, 2.0f);

        sendSlowMotionPacket(player);
    }

    private void sendSlowMotionPacket(Player player)
    {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, player.getEntityId());

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);

        watcher.setEntity(player);
        watcher.setObject(7, serializer, (byte) (0x01));

        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            for (Player p : Bukkit.getOnlinePlayers())
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet); }
        catch (InvocationTargetException e) { e.printStackTrace(); }

        /* Old Source */
        /*
         *
         * CraftPlayer p = (CraftPlayer)player;
         * EntityPlayer e = p.getHandle();
         *
         * DataWatcher water = e.getDataWatcher();
         * water.set(DataWatcherRegistry.a.a(7), Byte.valueOf((byte)1));
         *
         * PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(p.getEntityId(), water, false);
         * e.playerConnection.sendPacket(packet);
         *
         */

        /* Helped People & link */
        //  https://www.spigotmc.org/threads/simulating-potion-effect-glowing-with-protocollib.218828/#post-2246160
        //  https://cafe.naver.com/goldbigdragon/67684
    }

    private void liberationEffect(Player player)
    {
        Location loc = player.getLocation();
        World world = loc.getWorld();

        Location result = loc.clone().add(0, player.getEyeHeight(), 0);

        int particleCount = 150;
        double particleSpeed = 0.5;

        assert world != null;
        world.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.3f, 2.0f);
        BloomAPI.AllPlayerSendPlayerMessage("  §a♦ §b" + player.getName() + "§r님께서 §c벌칙자§r에서 §e해방§r되셨습니다");

        Entity entity = spawnLiberationEntity(world, result);

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                count++;

                switch (count) {
                    case 1:
                        world.spawnParticle(Particle.PORTAL, result, particleCount, 0, 0, 0, 3.0);
                        break;

                    case 2:
                        player.playSound(loc, Sound.ENTITY_WITHER_DEATH, 0.8f, 0);
                        world.spawnParticle(Particle.TOTEM, result, particleCount, 0, 0, 0, particleSpeed);
                        entity.remove();
                        break;

                    default:
                        cancel();
                }
            }
        }.runTaskTimerAsynchronously(BloomAPI.PLUGIN, 10L, 50L);
    }

    private Entity spawnLiberationEntity(World world, Location location)
    {
        Item entity = (Item) world.spawnEntity(location.clone().add(0, -0.37, 0), EntityType.DROPPED_ITEM);
        entity.setItemStack(new ItemStack(Material.DRAGON_EGG));
        entity.setGravity(false);
        entity.setVelocity(entity.getVelocity().multiply(0));
        entity.setPickupDelay(32767);

        return entity;
    }


    private boolean isPenalty(String uuid)
    {
        return BloomAPI.getHashMapPenalty(uuid);
    }

    private boolean isUseItem(PlayerInteractEvent event)
    {
        if (event.getItem() == null) return false;
        if (event.getItem().getType() != Material.DIAMOND) return false;

        return event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK;
    }
}
