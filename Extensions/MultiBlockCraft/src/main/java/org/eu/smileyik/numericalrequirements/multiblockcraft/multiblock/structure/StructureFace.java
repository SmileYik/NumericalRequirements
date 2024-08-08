package org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure;

public enum StructureFace {

    UP(0),
    DOWN(1),
    LEFT(2),
    RIGHT(3),
    FRONT(4),
    BACK(5);

    private final int index;

    StructureFace(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name().toLowerCase();
    }

    public static StructureFace fromIndex(int index) {
        switch (index) {
            case 0: return UP;
            case 1: return DOWN;
            case 2: return LEFT;
            case 3: return RIGHT;
            case 4: return FRONT;
            case 5: return BACK;
        }
        return null;
    }
}
