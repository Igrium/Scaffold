data modify storage scaffold:internal temp set from entity @s UUID
data modify entity @s data.NoTeleport set value 1b

execute store success entity @s data.NoTeleport byte 1 run data modify storage scaffold:internal temp set from entity ${this} data.NextPath
execute if entity @s[nbt={data:{NoTeleport:0b}}] run function ${jump}
