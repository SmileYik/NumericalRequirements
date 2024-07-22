package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock;

import org.bukkit.block.BlockFace;

public enum MultiBlockFace {
    WEST(BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST),
    EAST(BlockFace.UP, BlockFace.DOWN, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST),
    SOUTH(BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH),
    NORTH(BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH),
    DOWN_1(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.DOWN, BlockFace.UP),
    DOWN_2(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.DOWN, BlockFace.UP),
    DOWN_3(BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN, BlockFace.UP),
    DOWN_4(BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.DOWN, BlockFace.UP),
    UP_1(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.UP, BlockFace.DOWN),
    UP_2(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN),
    UP_3(BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN),
    UP_4(BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN),
    ;

    private static final MultiBlockFace[] ARRAY_UP = new MultiBlockFace[] {UP_1, UP_2, UP_3, UP_4};
    private static final MultiBlockFace[] ARRAY_DOWN = new MultiBlockFace[] {DOWN_1, DOWN_2, DOWN_3, DOWN_4};
    private static final MultiBlockFace[] ARRAY_WEST = new MultiBlockFace[] {WEST};
    private static final MultiBlockFace[] ARRAY_EAST = new MultiBlockFace[] {EAST};
    private static final MultiBlockFace[] ARRAY_NORTH = new MultiBlockFace[] {NORTH};
    private static final MultiBlockFace[] ARRAY_SOUTH = new MultiBlockFace[] {SOUTH};

    private final BlockFace[] faces;

    MultiBlockFace(BlockFace up, BlockFace down, BlockFace left, BlockFace right, BlockFace front, BlockFace back) {
        this.faces = new BlockFace[] {up, down, left, right, front, back};
    }

    public BlockFace[] getFaces() {
        return faces;
    }

    public BlockFace getUp() {
        return faces[0];
    }

    public BlockFace getDown() {
        return faces[1];
    }

    public BlockFace getLeft() {
        return faces[2];
    }

    public BlockFace getRight() {
        return faces[3];
    }

    public BlockFace getFront() {
        return faces[4];
    }

    public BlockFace getBack() {
        return faces[5];
    }

    public static MultiBlockFace[] getByFace(BlockFace face) {
        switch (face) {
            case UP:
                return ARRAY_UP;
            case DOWN:
                return ARRAY_DOWN;
            case EAST:
                return ARRAY_EAST;
            case NORTH:
                return ARRAY_NORTH;
            case SOUTH:
                return ARRAY_SOUTH;
            case WEST:
                return ARRAY_WEST;
        }
        return new MultiBlockFace[0];
    }
}
