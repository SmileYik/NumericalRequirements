spawn_armor_stand = {

}


spawn_armor_stand.spawn = function(world, x, y, z, marker, small, visible)
    loc = NewLocation:execute(helper:toObjectArray({world, x, y, z}))
    task:runTask(function()
        entity = world:spawnEntity(loc, EntityType.ARMOR_STAND)
        entity:setMarker(marker)
        entity:setSmall(small)
        entity:setVisible(visible)
        item = NewItemStack:execute(helper:toObjectArray({Material.BARRIER}))
        meta = item:getItemMeta()
        meta:setCustomModelData(100)
        item:setItemMeta(meta)
        entity:setHelmet(item)
        packet = NewPacketPlayOutBlockBreakAnimation:execute(helper:toObjectArray({Integer:valueOf(123):intValue(), position, Integer:valueOf(0):intValue()}))
    end)
end

spawn_armor_stand.removeAll = function(world)
    list = world:getEntities()
    size = list:size()
    for i = 1, size do
        entity = list:get(i - 1)
        if entity:getType():equals(EntityType.ARMOR_STAND) then
            entity:remove()
        end
    end
end
