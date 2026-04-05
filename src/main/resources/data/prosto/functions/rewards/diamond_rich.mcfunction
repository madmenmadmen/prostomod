# Дополнительные бонусы
give @s prosto:diamond_poop 5
give @s minecraft:diamond 3

# Эффекты для праздника
effect give @s minecraft:glowing 1200 0
effect give @s minecraft:luck 2400 1
effect give @s minecraft:speed 1200 1

# Фейерверк из частиц
execute as @s run particle minecraft:end_rod ~ ~1 ~ 2 2 2 0.1 100
execute as @s run particle minecraft:firework ~ ~1 ~ 1 1 1 0.2 50

# Сообщения игроку
tellraw @s ["", {"text":"🎉 ","color":"gold"}, {"text":"ПОЗДРАВЛЯЮ! ","color":"yellow"}, {"text":"Ты настоящий король какашек!","color":"gold"}]
tellraw @s ["", {"text":"💎 ","color":"aqua"}, {"text":"Ты накопил 128 алмазных какашек","color":"light_purple"}]
tellraw @s ["", {"text":"💎 ","color":"aqua"}, {"text":"+5 Алмазных какашек","color":"aqua"}]
tellraw @s ["", {"text":"♦ ","color":"aqua"}, {"text":"+3 Алмаза","color":"aqua"}]

# Звуки победы
playsound minecraft:entity.player.levelup master @s ~ ~ ~ 1 1
playsound minecraft:entity.experience_orb.pickup master @s ~ ~ ~ 1 0.8