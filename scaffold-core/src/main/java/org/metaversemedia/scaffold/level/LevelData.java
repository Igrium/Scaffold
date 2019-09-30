package org.metaversemedia.scaffold.level;

import org.json.JSONObject;

import com.flowpowered.nbt.ByteTag;
import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.DoubleTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.LongTag;
import com.flowpowered.nbt.StringTag;

/**
 * This class stores and represents all the (relevent) data that would normally be in level.dat
 * @author Sam54123
 *
 */
public class LevelData {
	// JSON provides a very nice way to store typed values in a map.
	private JSONObject values = new JSONObject();
	
	public LevelData() {
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
	}
	
	/**
	 * Create a new LevelData from a JSONObject of values
	 * @param values
	 */
	public LevelData(JSONObject values) {
		this.values = values;
	}
	
	/**
	 * Return a JSONObject with all values.
	 * @return Values
	 */
	public JSONObject values() {
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
		data.put(new DoubleTag("BorderCenterY", values.getDouble("BorderCenterY")));
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
		

		
		return null;
	}
	
	private byte boolToByte(boolean bool) {
		if (bool) {
			return 1;
		} else {
			return 0;
		}
	}
}
