package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.impl;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.MultiBlockMachine;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.data.MachineDataUpdatable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.listener.MachineListener;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.MultiBlockFace;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.MultiBlockStructureMainBlock;
import org.eu.smileyik.numericalrequirements.multiblockcraft.multiblock.structure.StructureMainBlock;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.Recipe;
import org.eu.smileyik.numericalrequirements.multiblockcraft.recipe.TimeRecipe;

import java.util.*;
import java.util.concurrent.ExecutionException;

public class SimpleMultiBlockMachineData implements MachineDataUpdatable {
    private static final BlockFace[] FACES = new BlockFace[] {
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN,
    };

    private boolean initialized = false;

    protected final MultiBlockMachine machine;
    protected String identifier;
    protected Location location;

    private int faceIndex = 0;
    protected BlockFace face = FACES[faceIndex];
    protected BlockFace[] ways;
    private long nextCheckTimestamp = 0;
    private final List<Container> inputs = new ArrayList<>();
    private final List<Container> outputs = new ArrayList<>();

    private boolean enable;
    private String recipeId;
    private long finishedTimestamp;
    private long nextCheckRecipeTimestamp = 0;

    public SimpleMultiBlockMachineData(Machine machine) {
        this.machine = (MultiBlockMachine) machine;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        this.location = Machine.fromIdentifier(identifier);
    }

    @Override
    public Machine getMachine() {
        return machine;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isEnable() {
        return enable;
    }

    @Override
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public Recipe getRecipe() {
        return recipeId == null ? null : machine.findRecipe(recipeId);
    }

    @Override
    public double getRemainingTime() {
        return recipeId == null ? 0 : ((finishedTimestamp - System.currentTimeMillis()) / 1E3);
    }

    @Override
    public double getCraftedTime() {
        return recipeId == null ? 0 : getTotalTime() - getRemainingTime();
    }

    @Override
    public boolean isRunning() {
        return recipeId != null && machine.findRecipe(recipeId) != null;
    }

    @Override
    public synchronized void load(ConfigurationSection section) {
        this.identifier = section.getString("identifier");
        this.location = Machine.fromIdentifier(identifier);

        this.face = BlockFace.valueOf(section.getString("face"));

        this.enable = section.getBoolean("enable", false);
        this.recipeId = section.getString("recipe", null);
        this.finishedTimestamp = System.currentTimeMillis() + section.getLong("remainingTime");
    }

    @Override
    public synchronized void store(ConfigurationSection section) {
        section.set("type", getClass().getName());
        section.set("machine", machine.getId());

        section.set("identifier", identifier);

        section.set("face", face.name());

        section.set("enable", enable);
        section.set("recipe", recipeId);
        section.set("remainingTime", getRemainingTime());
    }

    @Override
    public long period() {
        return 0;
    }

    @Override
    public boolean update() {
        try {
            return doUpdate();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean doUpdate() throws ExecutionException, InterruptedException {
        if (!initialized) initialize();
        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis > nextCheckTimestamp) {
            nextCheckTimestamp = currentTimeMillis + machine.getCheckPeriod();
            checkStructure();
        }

        if (!isValid()) {
            recipeId = null;
            return true;
        }

        if (recipeId != null) {
            if (getRemainingTime() <= 0) {
                Recipe recipe = getRecipe();
                recipeId = null;
                MachineListener.CONTAINER_LOCK.writeLock().lock();
                try {
                    for (ItemStack output : recipe.getOutputs()) {
                        output = output.clone();
                        for (Container container : outputs) {
                            for (Map.Entry<Integer, ItemStack> entry : container.getInventory().addItem(output).entrySet()) {
                                output = entry.getValue().clone();
                            }
                        }
                    }
                } finally {
                    MachineListener.CONTAINER_LOCK.writeLock().unlock();
                }
            }
            return true;
        }

        if (!isEnable()) return false;
        if (currentTimeMillis > nextCheckRecipeTimestamp) {
            nextCheckRecipeTimestamp = currentTimeMillis + (machine.getCheckPeriod() >>> 2);
            for (Container container : inputs) {
                MachineListener.CONTAINER_LOCK.writeLock().lock();
                try {
                    Inventory inv = container.getInventory();
                    ItemStack[] contents = inv.getContents();
                    Recipe recipe = machine.findRecipe(contents);
                    if (recipe != null) {
                        DebugLogger.debug("%s find recipe: %s", identifier, recipe.getId());
                        recipe.takeInputs(contents);
                        recipeId = recipe.getId();
                        finishedTimestamp = System.currentTimeMillis() + (recipe instanceof TimeRecipe ? (long) (((TimeRecipe) recipe).getTime() * 1000) : 0L);
                        return true;
                    }
                } finally {
                    MachineListener.CONTAINER_LOCK.writeLock().unlock();
                }
            }
        }
        return true;
    }

    private void initialize() {
        initialized = true;

        if (face == null) {
            faceIndex = 0;
            face = FACES[faceIndex];
        } else {
            while (faceIndex < FACES.length && face != FACES[faceIndex]) {
                ++faceIndex;
            }
        }
    }

    public boolean isValid() {
        return ways != null;
    }

    public void nextFace() {
        faceIndex = (faceIndex + 1) % 6;
        face = FACES[faceIndex];
        DebugLogger.debug("Face: %s", face);
    }

    public BlockFace getFace() {
        return face;
    }

    protected void checkStructure() throws ExecutionException, InterruptedException {
        StructureMainBlock structure = machine.getStructure();
        Block mainBlock = getLocation().getBlock();
        for (MultiBlockFace multiBlockFace : MultiBlockFace.getByFace(face)) {
            BlockFace[] ways = multiBlockFace.getFaces();
            if (structure.isMatch(mainBlock, ways)) {
                this.ways = ways;

                Set<Container> oldInputs = new HashSet<>(inputs);
                Set<Container> oldOutputs = new HashSet<>(outputs);

                inputs.clear();
                outputs.clear();
                for (MultiBlockStructureMainBlock.Node node : structure.getInputPath()) {
                    Container container = syncCall(() -> {
                        MachineListener.CONTAINER_LOCK.writeLock().lock();
                        try {
                            BlockState blockState = structure.getBlock(node, mainBlock, ways).getState();
                            if (blockState instanceof Container) {
                                return (Container) blockState;
                            }
                        } finally {
                            MachineListener.CONTAINER_LOCK.writeLock().unlock();
                        }
                        return null;
                    }).get();
                    if (container != null) {
                        inputs.add(container);
                        if (!oldInputs.remove(container)) {
                            container.setMetadata(MachineListener.MULTI_BLOCK_MACHINE_CONTAINER_KEY, new FixedMetadataValue(
                                    MultiBlockCraftExtension.getInstance().getPlugin(), ""
                            ));
                        }
                    }
                }
                for (MultiBlockStructureMainBlock.Node node : structure.getOutputPath()) {
                    Container container = syncCall(() -> {
                        MachineListener.CONTAINER_LOCK.writeLock().lock();
                        try {
                            BlockState blockState = structure.getBlock(node, mainBlock, ways).getState();
                            if (blockState instanceof Container) {
                                return ((Container) blockState);
                            }
                        } finally {
                            MachineListener.CONTAINER_LOCK.writeLock().unlock();
                        }
                        return null;
                    }).get();
                    if (container != null) {
                        outputs.add(container);
                        if (!oldOutputs.remove(container)) {
                            container.setMetadata(MachineListener.MULTI_BLOCK_MACHINE_CONTAINER_KEY, new FixedMetadataValue(
                                    MultiBlockCraftExtension.getInstance().getPlugin(), ""
                            ));
                        }
                    }
                }

                MachineListener.CONTAINER_LOCK.writeLock().lock();
                try {
                    for (Container old : oldInputs) {
                        old.removeMetadata(MachineListener.MULTI_BLOCK_MACHINE_CONTAINER_KEY, MultiBlockCraftExtension.getInstance().getPlugin());
                    }
                    for (Container old : oldOutputs) {
                        old.removeMetadata(MachineListener.MULTI_BLOCK_MACHINE_CONTAINER_KEY, MultiBlockCraftExtension.getInstance().getPlugin());
                    }
                } finally {
                    MachineListener.CONTAINER_LOCK.writeLock().unlock();
                }
                return;
            }
        }
        this.ways = null;
    }
}
