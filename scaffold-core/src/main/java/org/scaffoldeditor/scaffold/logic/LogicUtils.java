package org.scaffoldeditor.scaffold.logic;

import org.scaffoldeditor.nbt.util.Identifier;
import org.scaffoldeditor.scaffold.level.entity.Entity;
import org.scaffoldeditor.scaffold.logic.datapack.TargetSelector;
import org.scaffoldeditor.scaffold.logic.datapack.commands.Command;
import org.scaffoldeditor.scaffold.logic.datapack.commands.DataCommandBuilder;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.Conditional;
import org.scaffoldeditor.scaffold.logic.datapack.commands.ExecuteCommand.DataStorageConditional;

import net.querz.nbt.tag.CompoundTag;

/**
 * A collection of utility methods related to logic compilation.
 * @author Igrium
 */
public final class LogicUtils {
	private LogicUtils() {};

	/**
	 * Get the info to spawn a Minecraft entity to hold unique scoreboard values for this entity.
	 * Entity type will be <code>minecraft:marker</code>.
	 * @param entity Entity to get the companion of.
	 * @return Companion entity.
	 */
	public static CompoundTag getCompanionEntity(Entity entity) {
		CompoundTag data = new CompoundTag();
		data.putString("scaffoldID", entity.getFinalName());
		
		CompoundTag ent = new CompoundTag();
		ent.put("data", data);
		ent.putString("id", "minecraft:marker");
		
		return ent;
	}
	
	/**
	 * Get the target selector for a Scaffold entity's companion entity. Whether or
	 * not this companion entity exists is up to the Scaffold entity.
	 * 
	 * @param entity Entity to get the companion of.
	 * @return Companion entity.
	 */
	public static TargetSelector getCompanionSelector(Entity entity) {
		return TargetSelector.fromString("@e[type=minecraft:marker,limit=1,nbt={data:{scaffoldID:"+entity.getFinalName()+"}}]");
	}
	
	/**
	 * Get the identifier for a
	 * <a href="https://minecraft.fandom.com/wiki/Commands/data#Storage">storage</a>
	 * entry that is guarenteed to be unique to this Scaffold entity.
	 * 
	 * @param entity
	 * @return
	 */
	public static Identifier getEntityStorage(Entity entity) {
		String namespace = entity.getLevel().getName().toLowerCase();
		String value = entity.getFinalName().toLowerCase();
		return new Identifier(namespace, value);
	}
	
	/**
	 * Get a Minecraft function that's unique to a Scaffold entity.
	 * 
	 * @param entity Entity to use.
	 * @param name   Name of the function.
	 * @return An identifier in the following format:
	 *         <code>[levelName]:[entityName]/[functionName]</code>
	 */
	public static Identifier getEntityFunction(Entity entity, String name) {
		String namespace = entity.getLevel().getName().toLowerCase();
		String value = entity.getFinalName().toLowerCase()+"/"+name;
		return new Identifier(namespace, value);
	}
	
	/**
	 * Generate a command that saves the passed tag into a Scaffold entity's storage entry.
	 * @param entity Scaffold entity to use.
	 * @param nbt The data to merge into the storage.
	 * @return The command.
	 */
	public static Command setStorageCommand(Entity entity, CompoundTag nbt) {
		Identifier storage = getEntityStorage(entity);
		return new DataCommandBuilder().merge().storage(storage).nbt(nbt).build();
	}
	
	/**
	 * Generate a conditional that checks if a Scaffold entity's storage entry has a tag.
	 * @param entity Scaffold entity to use.
	 * @param path Data path to check for.
	 * @return The conditional.
	 */
	public static Conditional ifStorageHas(Entity entity, String path) {
		Identifier storage = getEntityStorage(entity);
		return new DataStorageConditional(storage, path);
	}
	
	/**
	 * Generate a command that will clone an NBT tag.
	 * @param storage1 Storage to clone from.
	 * @param path1 Path of data to clone.
	 * @param storage2 Storage to clone to.
	 * @param path2 Path to clone to.
	 * @return Generated command.
	 */
	public static Command cloneNBT(Identifier storage1, String path1, Identifier storage2, String path2) {
		return new DataCommandBuilder().modify().storage(storage2).path(path2).set().from().storage(storage1).path(path1).build();
	}
	
}
