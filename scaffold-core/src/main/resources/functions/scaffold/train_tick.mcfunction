execute if entity @s[tag=forward] at @s run teleport @s ^ ^ ^${speed}
execute if entity @s[tag=backward] at @s run teleport @s ^ ^ ^${speed}

data modify storage scaffold:internal temp set from entity @s data.NextPath
execute if entity @s[tag=forward] at @s as @e[distance=..0.1,type=marker,nbt={data:{ScaffoldType:"path_node"}}] run function ${try_jump}