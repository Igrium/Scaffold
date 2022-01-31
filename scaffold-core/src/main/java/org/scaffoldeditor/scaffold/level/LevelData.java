package org.scaffoldeditor.scaffold.level;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.joml.Vector3ic;
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
	
	public enum GameType {
		SURVIVAL,
		CREATIVE,
		ADVENTURE,
		SPECTATOR
	}
	
	public LevelData(Level level) {
		this.level = level;
		
		data.putByte("Difficulty", (byte) 2);
		data.putBoolean("DifficultyLocked", false);
		data.putBoolean("hardcore", false);
		data.putLong("RandomSeed", (long) (Math.random() * Math.pow(10, 18)));
		data.put("GameRules", gamerules);
		
		try {
			NBTMerger.mergeCompound(data, loadTemplate().getCompoundTag("Data"), false, ListMergeMode.REPLACE);
		} catch (IOException e) {
			LogManager.getLogger().error("Error loading default level data.", e);
		}
	}
	
	/**
	 * Set the level data from a compound tag.
	 * @param data Compound tag to set.
	 */
	public void setData(CompoundTag data) {
		this.data = data;
		if (data.containsKey("GameRules")) {
			this.gamerules = data.getCompoundTag("GameRules");
		} else {
			this.gamerules = new CompoundTag();
			data.put("GameRules", gamerules);
		}
		
		
		try {
			CompoundTag template = loadTemplate();
			NBTMerger.mergeCompound(data, template.getCompoundTag("Data"), false, ListMergeMode.REPLACE);
		} catch (IOException e) {
			LogManager.getLogger().error("Error loading default level data.", e);
		}
	}
	
	private CompoundTag loadTemplate() throws IOException {
		AssetManager assetManager = level.getProject().assetManager();
		return (CompoundTag) new NBTDeserializer(true).fromStream(assetManager.getAssetAsStream("defaults/template_level.dat")).getTag();
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
	 * @param gameType Initial gamemode for the player.
	 * @return Compiled NBT
	 */
	public CompoundTag compile(boolean cheats, GameType gameType) throws IOException {
		AssetManager assetManager = level.getProject().assetManager();
		
		NamedTag named = new NBTDeserializer(true).fromStream(assetManager.getAssetAsStream("defaults/default_level.dat"));
		CompoundTag tag = (CompoundTag) named.getTag();
		CompoundTag data = tag.getCompoundTag("Data");
		NBTMerger.mergeCompound(data, this.data, true, ListMergeMode.REPLACE);
		
		// Identify player start
		Entity start = null;
		for (Entity ent : level.getLevelStack()) {
			if (ent instanceof PlayerStart) {
				start = ent;
			}
		}
		
		if (start != null) {
			Vector3ic startPos = start.getBlockPosition();
			data.putInt("SpawnX", startPos.x());
			data.putInt("SpawnY", startPos.y());
			data.putInt("SpawnZ", startPos.z());
		}
		
		data.putBoolean("allowCommands", cheats);
		data.putString("LevelName", level.getPrettyName());
		data.putInt("GameType", gameType.ordinal());
		
		return tag;
	}
	

	
	/**
	 * Compile level data to nbt file.
	 * @param file File to save to.
	 * @param cheats Should cheats be enabled?
	 * @param gameType Initial gamemode for the player.
	 * @throws IOException if an I/O error occurs.
	 */
	public void compileFile(File file, boolean cheats, GameType gameType) throws IOException {
		CompoundTag compiled = compile(cheats, gameType);
		NBTUtil.write(new NamedTag("", compiled), file);
	}
	
}
