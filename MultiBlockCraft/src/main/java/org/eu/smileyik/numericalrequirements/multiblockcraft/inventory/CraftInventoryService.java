package org.eu.smileyik.numericalrequirements.multiblockcraft.inventory;

import java.util.HashMap;
import java.util.Map;

public class CraftInventoryService {
    private final Map<String, CraftInventory> idCraftInvMap = new HashMap<>();
    private final Map<Class<? extends CraftInventory>, String> holderToCraftInvId = new HashMap<>();


}
