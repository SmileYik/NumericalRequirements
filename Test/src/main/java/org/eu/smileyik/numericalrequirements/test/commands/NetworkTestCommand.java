package org.eu.smileyik.numericalrequirements.test.commands;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.network.NetworkService;
import org.eu.smileyik.numericalrequirements.core.network.PacketListener;
import org.eu.smileyik.numericalrequirements.core.network.PacketToString;
import org.eu.smileyik.numericalrequirements.core.network.PlayerChannelHandler;
import org.eu.smileyik.numericalrequirements.nms.craftbukkit.CraftWorld;
import org.eu.smileyik.numericalrequirements.nms.entity.EntityArmorStand;
import org.eu.smileyik.numericalrequirements.nms.network.packet.PacketPlayOutEntityMetadata;
import org.eu.smileyik.numericalrequirements.nms.network.packet.PacketPlayOutSpawnEntityLiving;
import org.eu.smileyik.numericalrequirements.test.TestCommand;

import java.util.HashMap;
import java.util.Map;

@TestCommand("network")
public class NetworkTestCommand {
    private final Map<String, PacketListener> map = new HashMap<>();

    /**
     * 打印指定包
     * @param args
     */
    public void print(String[] args) {
        final String packetName = args[0];
        boolean result = args.length > 1 && "true".equalsIgnoreCase(args[1]);
        System.out.println("print packet " + packetName + " cancel: " + result);
        map.putIfAbsent(packetName, new PacketListener() {
            @Override
            public String getPacketName() {
                return packetName;
            }

            @Override
            public Object handlePacketIn(PlayerChannelHandler handler, Object packet) {
                System.out.printf("%s: %s\n", handler.getPlayer().getName(), PacketToString.toString(packet));
                return result ? null : PacketListener.super.handlePacketIn(handler, packet);
            }

            @Override
            public Object handlePacketOut(PlayerChannelHandler handler, Object packet) {
                System.out.printf("%s: %s\n", handler.getPlayer().getName(), PacketToString.toString(packet));
                return result ? null : PacketListener.super.handlePacketOut(handler, packet);
            }
        });
        NumericalRequirements.getInstance().getNetworkService().addPacketListener(map.get(packetName));
    }

    /**
     * 取消打印指定包
     * @param args
     */
    public void cancel(String[] args) {
        PacketListener remove = map.remove(args[0]);
        if (remove != null)
            NumericalRequirements.getInstance().getNetworkService().removePacketListener(remove);
    }

    public void spawnArmorStand1(String[] args) {
        Server server = NumericalRequirements.getPlugin().getServer();
        NetworkService networkService = NumericalRequirements.getInstance().getNetworkService();
        World world = server.getWorld("World");
        EntityArmorStand armorStand = new EntityArmorStand(
                CraftWorld.getHandle(world),
                Double.parseDouble(args[0]),
                Double.parseDouble(args[1]),
                Double.parseDouble(args[2])
        );
        PacketPlayOutSpawnEntityLiving packetSpawn = PacketPlayOutSpawnEntityLiving.newInstance(armorStand);
        networkService.broadcastPacket(packetSpawn);
    }

    public void spawnArmorStand2(String[] args) {
        Server server = NumericalRequirements.getPlugin().getServer();
        NetworkService networkService = NumericalRequirements.getInstance().getNetworkService();
        World world = server.getWorld("World");
        EntityArmorStand armorStand = new EntityArmorStand(
                CraftWorld.getHandle(world),
                Double.parseDouble(args[0]),
                Double.parseDouble(args[1]),
                Double.parseDouble(args[2])
        );
        ArmorStand bukkitEntity = (ArmorStand) armorStand.getBukkitEntity();
        bukkitEntity.setVisible(false);
        bukkitEntity.setMarker(true);
        PacketPlayOutSpawnEntityLiving packetSpawn = PacketPlayOutSpawnEntityLiving.newInstance(armorStand);
        PacketPlayOutEntityMetadata packetMetadata = PacketPlayOutEntityMetadata.newInstance(
                armorStand.getId(), armorStand.getDataWatcher(), true
        );
        networkService.broadcastPacket(packetSpawn);
        networkService.broadcastPacket(packetMetadata);
    }
}
