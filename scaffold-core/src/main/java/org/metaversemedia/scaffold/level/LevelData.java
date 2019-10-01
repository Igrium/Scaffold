package org.metaversemedia.scaffold.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.LongTag;
import com.flowpowered.nbt.StringTag;
import com.flowpowered.nbt.stream.NBTOutputStream;

/**
 * This class stores and represents all the (relevent) data that would normally be in level.dat
 * @author Sam54123
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
	public CompoundMap compile(boolean cheats) {
		CompoundMap data = new CompoundMap();
		data.put(new ByteTag("allowCommands", boolToByte(cheats)));
		data.put(new DoubleTag("BorderCenterX", values.getDouble("BorderCenterX")));
		data.put(new DoubleTag("BorderCenterY", values.getDouble("BorderCenterZ")));
		data.put(new DoubleTag("BorderDamagePerBlock", values.getDouble("BorderDamagePerBlock")));
		data.put(new DoubleTag("BorderSafeZone", values.getDouble("BorderSafeZone")));
		data.put(new DoubleTag("BorderSize", values.getDouble("BorderSize")));
		data.put(new DoubleTag("BorderSizeLerpTarget", values.getDouble("BorderSizeLerpTarget")));
		data.put(new LongTag("BorderSizeLerpTime", values.getLong("BorderSizeLerpTime")));
		data.put(new DoubleTag("BorderWarningBlocks", values.getDouble("BorderWarningBlocks")));
		data.put(new DoubleTag("BorderWarningTime", values.getDouble("BorderWarningTime")));
		data.put(new IntTag("clearWeatherTime", 0));
		data.put(new IntTag("DataVersion", 1976));
		data.put(new IntTag("DayTime", values.getInt("DayTime")));
		data.put(new ByteTag("Difficulty", (byte) values.getInt("Difficulty")));
		data.put(new ByteTag("DifficultyLocked", boolToByte(values.getBoolean("DifficultyLocked"))));
		data.put(new IntTag("GameType", values.getInt("GameType")));
		data.put(new StringTag("generatorName", "flat"));
		data.put(new IntTag("generatorVersion", 0));
		data.put(new ByteTag("hardcore", boolToByte(values.getBoolean("hardcore"))));
		data.put(new ByteTag("intialized", boolToByte(true)));
		data.put(new StringTag("LevelName", level.getPrettyName()));
		data.put(new ByteTag("MapFeatures", boolToByte(false)));
		data.put(new ByteTag("raining", boolToByte(false)));
		data.put(new IntTag("rainTime", 100000));
		data.put(new IntTag("RandomSeed", values.getInt("RandomSeed")));
		data.put(new IntTag("SpawnX", values.getInt("SpawnX")));
		data.put(new IntTag("SpawnY", values.getInt("SpawnY")));
		data.put(new IntTag("SpawnZ", values.getInt("SpawnZ")));
		data.put(new ByteTag("thundering", boolToByte(false)));
		data.put(new IntTag("thunderTime", 100000));
		data.put(new LongTag("Time", 0));
		data.put(new IntTag("version", 19133));
		data.put(new IntTag("WanderingTraderSpawnChance", 0));
		data.put(new IntTag("WanderingTraderSpawnDelay", 15600));
		
		// Compile gamerules
		CompoundMap gameruleMap = new CompoundMap();
		JSONObject gamerules = values.getJSONObject("gamerules");
		for (String key : gamerules.keySet()) {
			gameruleMap.put(new StringTag(key, gamerules.getString(key)));	
		}
		data.put(new CompoundTag("GameRules", gameruleMap));
		
		CompoundMap root = new CompoundMap();
		root.put(new CompoundTag("Data", data));
		
		return root;
	}
	
	/**
	 * Compile level data to nbt file.
	 * @param file File to save to.
	 * @param cheats Should cheats be enabled?
	 * @throws IOException if an I/O error occurs.
	 * @throws FileNotFoundException  if the file exists but is a directoryrather than a regular file,
	 *  does not exist but cannot be created,
	 *  or cannot be opened for any other reason
	 */
	public void compileFile(File file, boolean cheats) throws FileNotFoundException, IOException {
		CompoundMap compiled = compile(cheats);
		
		// Delete file if nessicary.
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		
		NBTOutputStream outputStream = new NBTOutputStream(new FileOutputStream(file));
		outputStream.writeTag(new CompoundTag("", compiled));
		outputStream.close();
	}
	
	private byte boolToByte(boolean bool) {
		if (bool) {
			return 1;
		} else {
			return 0;
		}
	}
}
