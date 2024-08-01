Integer = helper:getClass("java.lang.Integer")

ReflectTool = helper:getClass("org.eu.smileyik.numericalrequirements.reflect.MySimpleReflect")
EntityType = helper:getClass("org.bukkit.entity.EntityType")
Location = helper:getClass("org.bukkit.Location")
ItemStack = helper:getClass("org.bukkit.inventory.ItemStack")
Material = helper:getClass("org.bukkit.Material")
CraftPlayer = helper:getClass("org.eu.smileyik.numericalrequirements.nms.CraftPlayer")
PacketPlayOutBlockBreakAnimation = helper:getClass("net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation")

NewLocation = ReflectTool:get("org.bukkit.Location(org.bukkit.World, double, double, double)")
NewItemStack = ReflectTool:get("org.bukkit.inventory.ItemStack(org.bukkit.Material)");
NewPacketPlayOutBlockBreakAnimation = ReflectTool:get("net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation(int, net.minecraft.core.BlockPosition, int)");
NewBlockPosition = ReflectTool:get("net.minecraft.core.BlockPosition(double, double, double)")
server = nr:getServer()
world = server:getWorld("World")

function send_packet_by_name(name, packet)
    player = server:getPlayer(name)
    cplayer = CraftPlayer:getHandle(player)
    conn = cplayer:playerConnection()
    conn:sendPacket(packet)
end
