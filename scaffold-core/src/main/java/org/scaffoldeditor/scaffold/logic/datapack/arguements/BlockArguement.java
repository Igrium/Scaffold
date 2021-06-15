package org.scaffoldeditor.scaffold.logic.datapack.arguements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.scaffoldeditor.nbt.block.Block;

import net.querz.nbt.io.SNBTUtil;
import net.querz.nbt.tag.CompoundTag;

/**
 * Represents a <a
 * href=https://minecraft.fandom.com/wiki/Argument_types#block_state>Block</a>
 * arguement.
 * 
 * @author Igrium
 */
public class BlockArguement {
	public final String id;
	private Map<String, String> blockstate;
	private CompoundTag data;
	
	public BlockArguement(String id) {
		this.id = id;
	}
	
	public BlockArguement(String id, Map<String, String> blockstate, CompoundTag data) {
		this.id = id;
		this.blockstate = blockstate;
		this.data = data;
	}
	
	public BlockArguement(String id, Map<String, String> blockstate) { 
		this(id, blockstate, null);
	}
	
	public BlockArguement(String id, CompoundTag data) {
		this(id, null, data);
	}
	
	public BlockArguement(Block block) {
		this.id = block.getName();
		CompoundTag properties = block.getProperties();
		if (properties.size() > 0) {
			blockstate = new HashMap<>();
			for (String key : properties.keySet()) {
				blockstate.put(key, properties.get(key).valueToString());
			}
		}
	}
	
	public String compile() {
		String out = id;
		try {
			if (blockstate != null) {
				out = out + writeBlockstate();
			}
			if (data != null) {
				out = out + SNBTUtil.toSNBT(data);
			}
		} catch (IOException e) {
			throw new AssertionError("Unable to compile blockstate NBT!", e);
		}
		return out;
	}
	
	private String writeBlockstate() {
		List<String> compBlockstates = blockstate.keySet().stream().map(key -> {
			return key+"="+blockstate.get(key);
		}).collect(Collectors.toList());
		
		StringJoiner joiner = new StringJoiner(",", "[", "]");
		for (String str : compBlockstates) {
			joiner.add(str);
		}
		return joiner.toString();
	}
	
	@Override
		public String toString() {
			return compile();
		}
}
