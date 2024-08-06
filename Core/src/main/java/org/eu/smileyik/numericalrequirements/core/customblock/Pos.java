package org.eu.smileyik.numericalrequirements.core.customblock;

import org.bukkit.Location;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;

public class Pos {
    final String world;
    final int x;
    final int y;
    final int z;


    public Pos(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Pos(Location location) {
        this(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public Location toLocation() {
        return new Location(
                NumericalRequirements.getInstance().getServer().getWorld(world),
                x, y, z
        );
    }

    public Location toEntityLocation() {
        return new Location(
                NumericalRequirements.getInstance().getServer().getWorld(world),
                x + 0.5, y, z + 0.5
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Pos pos = (Pos) object;
        return x == pos.x && y == pos.y && z == pos.z && world.equals(pos.world);
    }

    @Override
    public int hashCode() {
        int result = world.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "Pos{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
