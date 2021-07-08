teleport ${this} @s
data modify entity ${this} data.CurrentPath set from entity @s UUID
execute if data entity @s data.next run data modify entity ${this} data.NextPath set from entity @s data.next
execute unless data entity @s data.next run data remove entity ${this} data.NextPath
execute unless data entity @s data.next run tag ${this} remove forward