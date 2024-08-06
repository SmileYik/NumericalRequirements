package org.eu.smileyik.numericalrequirements.core.customblock;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.Location;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMap;
import org.eu.smileyik.numericalrequirements.core.api.util.ConfigurationHashMapSerializable;

import java.util.Objects;
import java.util.UUID;

@DatabaseTable(tableName = "nreq_custom_block_data")
public class CustomBlockData implements ConfigurationHashMapSerializable {
    @DatabaseField(id = true, generatedId = true)
    private UUID id;
    @DatabaseField
    private String world;
    @DatabaseField
    private int blockX;
    @DatabaseField
    private int blockY;
    @DatabaseField
    private int blockZ;
    @DatabaseField
    private String customBlockId;
    @DatabaseField
    private int chunkX;
    @DatabaseField
    private int chunkZ;

    public CustomBlockData() {

    }

    public CustomBlockData(String world, int blockX, int blockY, int blockZ, String customBlockId, int chunkX, int chunkZ) {
        this.id = UUID.randomUUID();
        this.world = world;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.customBlockId = customBlockId;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public CustomBlockData(Pos pos, String customBlockId) {
        this(
                pos.world,
                pos.x,
                pos.y,
                pos.z,
                customBlockId,
                pos.toLocation().getChunk().getX(),
                pos.toLocation().getChunk().getZ()
        );
    }

    public CustomBlockData(Location loc, String customBlockId) {
        this(
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ(),
                customBlockId,
                loc.getChunk().getX(),
                loc.getChunk().getZ()
        );
    }

    public Pos toPos() {
        return new Pos(world, blockX, blockY, blockZ);
    }

    public Location toLocation() {
        return new Location(
                NumericalRequirements.getPlugin().getServer().getWorld(world),
                blockX, blockY, blockZ
        );
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getBlockX() {
        return blockX;
    }

    public void setBlockX(int blockX) {
        this.blockX = blockX;
    }

    public int getBlockY() {
        return blockY;
    }

    public void setBlockY(int blockY) {
        this.blockY = blockY;
    }

    public int getBlockZ() {
        return blockZ;
    }

    public void setBlockZ(int blockZ) {
        this.blockZ = blockZ;
    }

    public String getCustomBlockId() {
        return customBlockId;
    }

    public void setCustomBlockId(String customBlockId) {
        this.customBlockId = customBlockId;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    @Override
    public ConfigurationHashMap toConfigurationHashMap() {
        return ConfigurationHashMap.Builder.asMap(
                "uuid",             id.toString(),
                "world",                world,
                "block-x",              blockX,
                "block-y",              blockY,
                "block-z",              blockZ,
                "custom-block-id",      customBlockId,
                "chunk-x",              chunkX,
                "chunk-z",              chunkZ
        );
    }

    @Override
    public void fromConfigurationHashMap(ConfigurationHashMap configurationHashMap) {
        if (configurationHashMap.containsKey("uuid")) id = UUID.fromString(configurationHashMap.getString("uuid"));
        if (configurationHashMap.containsKey("world")) world = configurationHashMap.getString("world");
        if (configurationHashMap.containsKey("block-x")) blockX = configurationHashMap.getInt("block-x");
        if (configurationHashMap.containsKey("block-y")) blockY = configurationHashMap.getInt("block-y");
        if (configurationHashMap.containsKey("block-z")) blockZ = configurationHashMap.getInt("block-z");
        if (configurationHashMap.containsKey("custom-block-id")) customBlockId = configurationHashMap.getString("custom-block-id");
        if (configurationHashMap.containsKey("chunk-x")) chunkX = configurationHashMap.getInt("chunk-x");
        if (configurationHashMap.containsKey("chunk-z")) chunkZ = configurationHashMap.getInt("chunk-z");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CustomBlockData that = (CustomBlockData) object;
        return blockX == that.blockX && blockY == that.blockY && blockZ == that.blockZ && chunkX == that.chunkX && chunkZ == that.chunkZ && Objects.equals(world, that.world) && Objects.equals(customBlockId, that.customBlockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, blockX, blockY, blockZ, customBlockId, chunkX, chunkZ);
    }
}
