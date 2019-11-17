package org.scaffoldeditor.scaffold.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.json.JSONObject;

import mryurihi.tbnbt.stream.NBTOutputStream;
import mryurihi.tbnbt.tag.NBTTag;
import mryurihi.tbnbt.tag.NBTTagByte;
import mryurihi.tbnbt.tag.NBTTagCompound;
import mryurihi.tbnbt.tag.NBTTagDouble;
import mryurihi.tbnbt.tag.NBTTagInt;
import mryurihi.tbnbt.tag.NBTTagLong;
import mryurihi.tbnbt.tag.NBTTagString;

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
	public NBTTagCompound compile(boolean cheats) {
		NBTTagCompound data = new NBTTagCompound(new HashMap<String, NBTTag>());
		data.put("allowCommands", new NBTTagByte(boolToByte(cheats)));
		data.put("BorderCenterX", new NBTTagDouble(values.getDouble("BorderCenterX")));
		data.put("BorderCenterY", new NBTTagDouble(values.getDouble("BorderCenterZ")));
		data.put("BorderDamagePerBlock", new NBTTagDouble(values.getDouble("BorderDamagePerBlock")));
		data.put("BorderSafeZone", new NBTTagDouble(values.getDouble("BorderSafeZone")));
		data.put("BorderSize", new NBTTagDouble(values.getDouble("BorderSize")));
		data.put("BorderSizeLerpTarget", new NBTTagDouble(values.getDouble("BorderSizeLerpTarget")));
		data.put("BorderSizeLerpTime", new NBTTagLong(values.getLong("BorderSizeLerpTime")));
		data.put("BorderWarningBlocks", new NBTTagDouble(values.getDouble("BorderWarningBlocks")));
		data.put("BorderWarningTime", new NBTTagDouble(values.getDouble("BorderWarningTime")));
		data.put("clearWeatherTime", new NBTTagInt(0));
		data.put("DataVersion", new NBTTagInt(1976));
		data.put("DayTime", new NBTTagInt(values.getInt("DayTime")));
		data.put("Difficulty", new NBTTagByte((byte) values.getInt("Difficulty")));
		data.put("DifficultyLocked", new NBTTagByte(boolToByte(values.getBoolean("DifficultyLocked"))));
		data.put("GameType", new NBTTagInt(values.getInt("GameType")));
		data.put("generatorName", new NBTTagString("flat"));
		data.put("generatorVersion", new NBTTagInt(0));
		data.put("hardcore", new NBTTagByte(boolToByte(values.getBoolean("hardcore"))));
		data.put("intialized", new NBTTagByte(boolToByte(true)));
		data.put("LevelName", new NBTTagString(level.getPrettyName()));
		data.put("MapFeatures", new NBTTagByte(boolToByte(false)));
		data.put("raining", new NBTTagByte(boolToByte(false)));
		data.put("rainTime", new NBTTagInt(100000));
		data.put("RandomSeed", new NBTTagInt(values.getInt("RandomSeed")));
		data.put("SpawnX", new NBTTagInt(values.getInt("SpawnX")));
		data.put("SpawnY", new NBTTagInt(values.getInt("SpawnY")));
		data.put("SpawnZ", new NBTTagInt(values.getInt("SpawnZ")));
		data.put("thundering", new NBTTagInt(boolToByte(false)));
		data.put("thunderTime", new NBTTagInt(100000));
		data.put("Time", new NBTTagLong(0));
		data.put("version", new NBTTagInt(19133));
		data.put("WanderingTraderSpawnChance", new NBTTagInt(0));
		data.put("WanderingTraderSpawnDelay", new NBTTagInt(15600));
		
		// Compile gamerules
		NBTTagCompound gameruleMap = new NBTTagCompound(new HashMap<String, NBTTag>());
		JSONObject gamerules = values.getJSONObject("gamerules");
		for (String key : gamerules.keySet()) {
			gameruleMap.put(key, new NBTTagString(gamerules.getString(key)));	
		}
		data.put("GameRules", gameruleMap);
		
		NBTTagCompound root = new NBTTagCompound(new HashMap<String, NBTTag>());
		root.put("Data", data);
		
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
		NBTTagCompound compiled = compile(cheats);
		
		// Delete file if nessicary.
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		
		NBTOutputStream outputStream = new NBTOutputStream(new FileOutputStream(file));
		outputStream.writeTag(compiled, "");
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
