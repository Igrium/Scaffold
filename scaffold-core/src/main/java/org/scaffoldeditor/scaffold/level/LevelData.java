package org.scaffoldeditor.scaffold.level;

import java.io.File;
import java.io.IOException;
import org.scaffoldeditor.nbt.Constants;
import org.scaffoldeditor.nbt.math.Vector3i;
import org.scaffoldeditor.nbt.util.NBTMerger;
import org.scaffoldeditor.nbt.util.NBTMerger.ListMergeMode;
import org.scaffoldeditor.scaffold.io.AssetManager;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.level.entity.info.PlayerStart;

import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;


/**
 * This class stores and represents all the (relevent) data that would normally be in level.dat
 * @author Igrium
 *
 */
public class LevelData {
	private Level level;
	private CompoundTag data = new CompoundTag();
	private CompoundTag gamerules = new CompoundTag();
	
	public LevelData(Level level) {
		this.level = level;
		
		data.putByte("Difficulty", (byte) 2);
		data.putBoolean("DifficultyLocked", false);
		data.putBoolean("hardcore", false);
		data.putLong("RandomSeed", (long) (Math.random() * Math.pow(10, 18)));
		data.put("GameRules", gamerules);
	}
	
	/**
	 * Return the current overrides in the level data.
	 */
	public CompoundTag getData() {
		return data;
	}
	
	/**
	 * Get the gamerules tag.
	 */
	public CompoundTag getGamerules() {
		return gamerules;
	}
	
	/**
	 * Set a gamerule.
	 * @param name Gamerule name.
	 * @param value Gamerule value.
	 */
	public void setGamerule(String name, String value) {
		gamerules.putString(name, value);
	}
	
	/**
	 * Get a gamerule.
	 * @param name Gamerule name.
	 * @return Gamerule value, or null if it's not explicitly set.
	 */
	public String getGamerule(String name) {
		return gamerules.getString(name);
	}
	
	/**
	 * Compile this LevelData into a compound tag.
	 * @param cheats Should the compiled world have cheats on?
	 * @return Compiled NBT
	 */
	public CompoundTag compile(boolean cheats) throws IOException {
		AssetManager assetManager = level.getProject().assetManager();
		
		NamedTag named = new NBTDeserializer(true).fromStream(assetManager.getAssetAsStream("defaults/default_level.dat"));
		CompoundTag tag = (CompoundTag) named.getTag();
		CompoundTag data = tag.getCompoundTag("Data");
		NBTMerger.mergeCompound(data, data, true, ListMergeMode.REPLACE);
		
		// Identify player start
		Entity start = null;
		for (String name : level.getEntityStack()) {
			Entity ent = level.getEntity(name);
			if (ent instanceof PlayerStart) {
				start = ent;
			}
		}
		
		if (start != null) {
			Vector3i startPos = start.getBlockPosition();
			data.putInt("SpawnX", startPos.x);
			data.putInt("SpawnY", startPos.y);
			data.putInt("SpawnZ", startPos.z);
		}
		
		data.putBoolean("allowCommands", cheats);
		data.putInt("version", Constants.DEFAULT_DATA_VERSION);
		data.putString("LevelName", level.getPrettyName());
		
		return tag;
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
