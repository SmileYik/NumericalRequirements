package org.eu.smileyik.numericalrequirements.test.nms.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.nms.CraftPlayer;
import org.eu.smileyik.numericalrequirements.nms.entity.EntityPlayer;
import org.eu.smileyik.numericalrequirements.nms.network.NetworkManager;
import org.eu.smileyik.numericalrequirements.nms.network.PlayerConnection;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

import java.lang.reflect.Field;

@NeedTest
public class NetworkTest1 implements Listener {
    @NeedTest
    public void init() {
        NumericalRequirements.getPlugin().getServer().getPluginManager().registerEvents(this, NumericalRequirements.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        EntityPlayer handle = CraftPlayer.getHandle(player);
        PlayerConnection playerConnection = handle.playerConnection();
        NetworkManager networkManager = playerConnection.getNetworkManager();
        Channel channel = networkManager.channel();
        channel.pipeline().addBefore("packet_handler", "nreq_packet_handle", new MyHandler());
        System.out.println(channel.isOpen());
        System.out.println(channel);

    }

    static boolean enteredBed = false;
    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        enteredBed = true;
    }

    @EventHandler
    public void onPlayerWakeUp(PlayerBedLeaveEvent event) {
        enteredBed = false;
    }

    public static class MyHandler extends ChannelDuplexHandler {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg != null) {
                Class<?> aClass = msg.getClass();
                boolean flag = true;
                switch (aClass.getSimpleName()) {
                    case "PacketPlayInPosition":
                    case "PacketPlayInLook":
                    case "PacketPlayInPositionLook":
                        break;
                    case "PacketPlayInTeleportAccept":
                        Field declaredField1 = msg.getClass().getDeclaredFields()[0];
                        declaredField1.setAccessible(true);
                        System.out.println(declaredField1.get(msg));
                        flag = !enteredBed;
                    case "PacketPlayInArmAnimation":
                        Field declaredField2 = msg.getClass().getDeclaredFields()[0];
                        declaredField2.setAccessible(true);
                        System.out.println(declaredField2.get(msg));
                    default:
                        System.out.println(aClass);
                }
                if (!flag) {
                    System.out.println("disabled");
                    return;
                }
                super.channelRead(ctx, msg);
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            boolean flag = true;


            if (msg != null) {
                Class<?> aClass = msg.getClass();

                switch (aClass.getSimpleName()) {
                    case "PacketPlayOutEntityVelocity":
                    case "PacketPlayOutRelEntityMove":
                    case "PacketPlayOutRelEntityMoveLook":
                    case "PacketPlayOutEntityMetadata":
                    case "PacketPlayOutEntityHeadRotation":
                    case "PacketPlayOutEntityTeleport":
                        break;
                    default:
                        if (enteredBed)
                        System.out.println(aClass);
                }

                if (!flag) {
                    System.out.println("disabled");
                    return;
                }

                super.write(ctx, msg, promise);
            }
        }
    }
}
