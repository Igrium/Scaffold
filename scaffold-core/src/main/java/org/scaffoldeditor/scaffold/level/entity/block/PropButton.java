package org.scaffoldeditor.scaffold.level.entity.block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.scaffoldeditor.nbt.Block;
import org.scaffoldeditor.scaffold.level.Level;
import org.scaffoldeditor.scaffold.level.entity.TargetSelectable;
import org.scaffoldeditor.scaffold.level.entity.Faceable.Direction;
import org.scaffoldeditor.scaffold.logic.Datapack;
import org.scaffoldeditor.scaffold.logic.MCFunction;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.StringTag;

/**
 * Represents a physical button ingame.
 * 
 * @author Sam54123
 */
public class PropButton extends SingleBlock {

	/**
	 * Button type may be wood or stone
	 */
	public enum ButtonType {
		LEVER, STONE, OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK
	}

	/**
	 * The Minecraft blocks that each button type corrisponds to.
	 */
	private static final Map<ButtonType, String> blockNames = new HashMap<ButtonType, String>();

	private static final String PRESSED_OUTPUT_NAME = "OnPressed";
	private static final String UNPRESSED_OUTPUT_NAME = "OnUnpressed";

	/* Name of the scoreboard objective stating whether this button is pressed */
	private String buttonPressedName;
	private ButtonType buttonType = ButtonType.STONE;

	/* Which way the button is facing */
	private Direction facing;

	/* The face on which the button sits */
	private ItemFace face;

	public PropButton(Level level, String name) {
		super(level, name);
		System.out.println("Creating button");
		buttonPressedName = level.getName() + "." + getName() + ".pressed";
		
		setAttribute("buttonType", ButtonType.STONE);
		setAttribute("facing", Direction.NORTH);
		setAttribute("face", ItemFace.WALL);
		
		blockNames.put(ButtonType.LEVER, "minecraft:lever");
		blockNames.put(ButtonType.STONE, "minecraft:stone_button");
		blockNames.put(ButtonType.OAK, "minecraft:oak_button");
		blockNames.put(ButtonType.SPRUCE, "minecraft:spruce_button");
		blockNames.put(ButtonType.BIRCH, "minecraft:birch_button");
		blockNames.put(ButtonType.JUNGLE, "minecraft:jungle_button");
		blockNames.put(ButtonType.ACACIA, "minecraft:acacia_button");
		blockNames.put(ButtonType.DARK_OAK, "minecraft:dark_oak_button");
	}
	
	@Override
	protected void onUnserialized(JSONObject object) {
		super.onUnserialized(object);
		
		// Convert attributes to enums.
		setAttribute("buttonType", ButtonType.valueOf((String) getAttribute("buttonType")));
		setAttribute("facing", Direction.valueOf((String) getAttribute("facing")));
		setAttribute("face", ItemFace.valueOf((String) getAttribute("face")));
	}
	
	@Override
	public List<AttributeDeclaration> getAttributeFields() {
		List<AttributeDeclaration> attributes = super.getAttributeFields();
		
		attributes.add(new AttributeDeclaration("buttonType", ButtonType.class));
		attributes.add(new AttributeDeclaration("facing", Direction.class));
		attributes.add(new AttributeDeclaration("face", ItemFace.class));
		
		return attributes;
	}

	/**
	 * Get the button type.
	 * 
	 * @return Button type.
	 */
	public ButtonType getButtonType() {
		return (ButtonType) getAttribute("buttonType");
	}

	/**
	 * Set the button type.
	 * 
	 * @param type New type.
	 */
	public void SetButtonType(ButtonType type) {
		setAttribute("buttonType", type);
	}

	/**
	 * Get the direction the button is facing.
	 * 
	 * @return Direction.
	 */
	public Direction getFacing() {
		return (Direction) getAttribute("facing");
	}

	/**
	 * Set the direction this button is facing.
	 * 
	 * @param facing New direction.
	 */
	public void setFacing(Direction facing) {
		setAttribute("facing", facing);
	}

	/**
	 * Get the face the button is on.
	 * 
	 * @return Face.
	 */
	public ItemFace getFace() {
		return (ItemFace) getAttribute("face");
	}

	/**
	 * Set the face the button should be on.
	 * 
	 * @param face new face.
	 */
	public void setFace(ItemFace face) {
		setAttribute("face", face);
	}
	
	@Override
	protected void onUpdateAttributes() {
		super.onUpdateAttributes();
		regenerateBlock();
	}

	/**
	 * Returns a sub command that checks if the button is pressed. To use, plug this
	 * into execute command (execute if [isPressedCommand]).
	 * 
	 * @return Pressed subcommand.
	 */
	public String isPressedCommand() {
		String blockName = getBlock().getName();
		return "block " + blockX() + " " + blockY() + " " + blockZ() + " " + blockName + "[powered=true]";
	}

	/**
	 * Returns a sub command that checks if the button is unpressed. To use, plug
	 * this into execute command (execute if [isUnpressedCommand]).
	 * 
	 * @return Unpressed subcommand.
	 */
	public String isUnpressedCommand() {
		String blockName = getBlock().getName();
		return "block " + blockX() + " " + blockY() + " " + blockZ() + " " + blockName + "[powered=false]";
	}

	@Override
	public boolean compileLogic(Datapack datapack) {
		// TODO Auto-generated method stub
		if (!super.compileLogic(datapack)) {
			return false;
		}

		TargetSelectable scoreboardEntity = getLevel().getScoreboardEntity();

		// Setup scoreboard.
		getLevel().initFunction().addCommand("scoreboard objectives add " + buttonPressedName + " dummy");
		getLevel().initFunction().addCommand(
				"scoreboard players set " + scoreboardEntity.getTargetSelector() + " " + buttonPressedName + " 0");

		// Add checks to tick function.
		MCFunction levelTickFunction = getLevel().tickFunction();

		// Call functions on tick button is pressed.
		String pressedCheck = "if " + isPressedCommand() + " if score " + scoreboardEntity.getTargetSelector() + " "
				+ buttonPressedName + " matches 0";
		String[] pressedCommands = compileOutput(PRESSED_OUTPUT_NAME, this);
		for (String c : pressedCommands) {
			levelTickFunction.addCommand("execute " + pressedCheck + " run " + c);
		}
		levelTickFunction.addCommand("execute if " + isPressedCommand() + " run scoreboard players set "+scoreboardEntity.getTargetSelector()+" "+buttonPressedName+" 1");

		// Call functions on tick button is unpressed.
		String unpressedCheck = "if " + isUnpressedCommand() + " if score " + scoreboardEntity.getTargetSelector() + " "
				+ buttonPressedName + " matches 1";
		String[] unpressedCommands = compileOutput(UNPRESSED_OUTPUT_NAME, this);
		for (String c : unpressedCommands) {
			levelTickFunction.addCommand("execute " + unpressedCheck + " run " + c);
		}
		levelTickFunction.addCommand("execute if " + isUnpressedCommand() + " run scoreboard players set "+scoreboardEntity.getTargetSelector()+" "+buttonPressedName+" 0");

		return true;
	}

	private void regenerateBlock() {
		CompoundMap properties = new CompoundMap();

		String faceString = null;
		if (face == ItemFace.FLOOR) {
			faceString = "floor";
		} else if (face == ItemFace.WALL) {
			faceString = "wall";
		} else {
			faceString = "ceiling";
		}
		properties.put(new StringTag("face", faceString));

		String facingString = null;
		if (facing == Direction.NORTH) {
			facingString = "north";
		} else if (facing == Direction.SOUTH) {
			facingString = "south";
		} else if (facing == Direction.EAST) {
			facingString = "east";
		} else {
			facingString = "west";
		}
		properties.put(new StringTag("facing", facingString));
		properties.put(new StringTag("powered", "false"));

		block = new Block(blockNames.get(buttonType), properties);
	}

}
