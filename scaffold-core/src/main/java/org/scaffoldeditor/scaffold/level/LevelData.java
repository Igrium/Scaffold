package org.scaffoldeditor.scaffold.level;

import java.io.File;
import java.io.IOException;
import org.json.JSONObject;
import org.scaffoldeditor.nbt.math.Vector3i;

import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.StringTag;


/**
 * This class stores and represents all the (relevent) data that would normally be in level.dat
 * @author Igrium
 *
 */
public class LevelData {
	// JSON provides a very nice way to store typed values in a map.
	private JSONObject values = new JSONObject();
	private Level level;
	
	/**
	 * Create a new LevelData.
	 * @param level Level this data is of
	 */
	public LevelData(Level level) {
		values.put("BorderCenterX", 0d);
		values.put("BorderCenterZ", 0d);
		values.put("BorderDamagePerBlock", 0.2d);
		values.put("BorderSafeZone", 5d);
		values.put("BorderSize", 60000000d);
		values.put("BorderSizeLerpTarget", 60000000d);
		values.put("BorderSizeLerpTime", 0l);
		values.put("BorderWarningBlocks", 5d);
		values.put("BorderWarningTime", 15d);
		values.put("DayTime", 0);
		values.put("Difficulty", 2);
		values.put("DifficultyLocked", false);
		values.put("GameType", 3);
		values.put("hardcore", false);
		values.put("RandomSeed", Math.random()*Math.pow(10, 18));
		values.put("SpawnX", 8);
		values.put("SpawnY", 55);
		values.put("SpawnZ", 8);
		values.put("version", 19133);
		
		JSONObject gamerules = new JSONObject();
		
		gamerules.put("announceAdvancements", "true");
		gamerules.put("commandBlockOutput", "false");
		gamerules.put("disableElytraMovementCheck", "false");
		gamerules.put("disableRaids", "false");
		gamerules.put("doDaylightCycle", "true");
		gamerules.put("doEntityDrops", "true");
		gamerules.put("doFireTick", "false");
		gamerules.put("doLimitedCrafting", "false");
		gamerules.put("doMobLoot", "true");
		gamerules.put("doMobSpawning", "false");
		gamerules.put("doTileDrops", "true");
		gamerules.put("doWeatherCycle", "false");
		gamerules.put("keepInventory", "false");
		gamerules.put("logAdminCommands", "true");
		gamerules.put("maxCommandChainLength", "65535");
		gamerules.put("maxEntityCramming", "24");
		gamerules.put("mobGriefing", "false");
		gamerules.put("naturalRegeneration", "true");
		gamerules.put("randomTickSpeed", "3");
		gamerules.put("reducedDebugInfo", "false");
		gamerules.put("sendCommandFeedback", "true");
		gamerules.put("showDeathMessages", "true");
		gamerules.put("spawnRadius", "1");
		gamerules.put("spectatorsGenerateChunks", "false");
		
		values.put("gamerules", gamerules);
		
		this.level = level;
	}
	
	/**
	 * Create a new LevelData from a JSONObject of values.
	 * @param level Level this data is of.
	 * @param values Values to copy from.
	 */
	public LevelData(Level level, JSONObject values) {
		this.values = values;
		this.level = level;
	}
	
	/**
	 * Return a JSONObject with all data.
	 * @return data
	 */
	public JSONObject getData() {
		return values;
	}
	
	/**
	 * Compile this LevelData into an NBT compound map
	 * @param cheats Should the compiled world have cheats on?
	 * @return Compiled CompoundMap
	 */
	public CompoundTag compile(boolean cheats) {
		CompoundTag data = new CompoundTag();
		data.put("allowCommands", new ByteTag(cheats));
		data.put("BorderCenterX", new DoubleTag(values.getDouble("BorderCenterX")));
		data.put("BorderCenterY", new DoubleTag(values.getDouble("BorderCenterZ")));
		data.put("BorderDamagePerBlock", new DoubleTag(values.getDouble("BorderDamagePerBlock")));
		data.put("BorderSafeZone", new DoubleTag(values.getDouble("BorderSafeZone")));
		data.put("BorderSize", new DoubleTag(values.getDouble("BorderSize")));
		data.put("BorderSizeLerpTarget", new DoubleTag(values.getDouble("BorderSizeLerpTarget")));
		data.put("BorderSizeLerpTime", new LongTag(values.getLong("BorderSizeLerpTime")));
		data.put("BorderWarningBlocks", new DoubleTag(values.getDouble("BorderWarningBlocks")));
		data.put("BorderWarningTime", new DoubleTag(values.getDouble("BorderWarningTime")));
		data.put("clearWeatherTime", new IntTag(0));
		data.put("DataVersion", new IntTag(1976));
		data.put("DayTime", new IntTag(values.getInt("DayTime")));
		data.put("Difficulty", new ByteTag((byte) values.getInt("Difficulty")));
		data.put("DifficultyLocked", new ByteTag(values.getBoolean("DifficultyLocked")));
		data.put("GameType", new IntTag(values.getInt("GameType")));
		data.put("generatorName", new StringTag("flat"));
		data.put("generatorVersion", new IntTag(0));
		data.put("hardcore", new ByteTag(values.getBoolean("hardcore")));
		data.put("intialized", new ByteTag(true));
		data.put("LevelName", new StringTag(level.getPrettyName()));
		data.put("MapFeatures", new ByteTag(false));
		data.put("raining", new ByteTag(false));
		data.put("rainTime", new IntTag(100000));
		data.put("RandomSeed", new IntTag(values.getInt("RandomSeed")));
		data.put("SpawnX", new IntTag(values.getInt("SpawnX")));
		data.put("SpawnY", new IntTag(values.getInt("SpawnY")));
		data.put("SpawnZ", new IntTag(values.getInt("SpawnZ")));
		data.put("thundering", new IntTag(0));
		data.put("thunderTime", new IntTag(100000));
		data.put("Time", new LongTag(0));
		data.put("version", new IntTag(19133));
		data.put("WanderingTraderSpawnChance", new IntTag(0));
		data.put("WanderingTraderSpawnDelay", new IntTag(15600));
		
		// Compile gamerules
		CompoundTag gameruleMap = new CompoundTag();
		JSONObject gamerules = values.getJSONObject("gamerules");
		for (String key : gamerules.keySet()) {
			gameruleMap.put(key, new StringTag(gamerules.getString(key)));	
		}
		data.put("GameRules", gameruleMap);
		
		CompoundTag root = new CompoundTag();
		root.put("Data", data);
		
		return root;
	}
	
	public void setSpawn(Vector3i pos) {
		values.put("SpawnX", pos.x);
		values.put("SpawnY", pos.y);
		values.put("SpawnZ", pos.z);
	}
	
	/**
	 * Compile level data to nbt file.
	 * @param file File to save to.
	 * @param cheats Should cheats be enabled?
	 * @throws IOException if an I/O error occurs.
	 */
	public void compileFile(File file, boolean cheats) throws IOException {
		CompoundTag compiled = compile(cheats);
		NBTUtil.write(new NamedTag("", compiled), file);
	}
	
}
