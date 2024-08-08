entity:setTable({
    a = 1,
    b = function() print("aaaa") end
})

entity:setTable({
    a = 1,
    b = function() print("aaaa") end
})

entity:setCallback(function(table)
print(table[1].a)
    print(table.a)
    table.b()
    table.a = 10
    table.b = function() print("nnn") end
end)

entity:getTable().b()
print(entity:getTable().a)

entity.printJavaStringArray = function(arrays)
    print(arrays[1])
    print(arrays[2])
    entity:getTable().b()
    print(entity:getTable().a)
end

entity.objects = entity:toObjectArray({"abc", "nil", 456, {4.6}})
