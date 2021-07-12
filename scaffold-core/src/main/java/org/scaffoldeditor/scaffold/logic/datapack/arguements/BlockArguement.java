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
	public final CompoundTag data;
	
	public BlockArguement(String id) {
		this.id = id;
		this.data = null;
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
		this(block, null);
	}
	
	/**
	 * Create a block arguement.
	 * @param block Block to use.
	 * @param data Additional block entity data.
	 */
	public BlockArguement(Block block, CompoundTag data) {
		this.id = block.getName();
		CompoundTag properties = block.getProperties();
		if (properties.size() > 0) {
			blockstate = new HashMap<>();
			for (String key : properties.keySet()) {
				blockstate.put(key, properties.getString(key));
			}
		}
		this.data = data;
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
	
	public String writeBlockstate() {
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
	
	public static Map<String, String> parseBlockStates(String in) {
		in = in.substring(1, in.length() - 1);
		String[] compBlockstates = in.split(",");
		Map<String, String> map = new HashMap<>();
		for (String str : compBlockstates) {
			int index = str.indexOf('=');
			map.put(str.substring(0, index), str.substring(index+1));
		}
		return map;
	}
	
	public static BlockArguement fromString(String in) throws IllegalArgumentException {
		int stateIndex = in.indexOf('[');
		int tagIndex = in.indexOf('{');
		
		String name;
		if (stateIndex >= 0) {
			name = in.substring(0, stateIndex);
		} else if (tagIndex >= 0) {
			name = in.substring(0, tagIndex);
		} else {
			name = in;
		}
		
		Map<String, String> blockState = null;
		if (stateIndex >= 0) {
			blockState = new HashMap<>();
			int stateEnd = in.indexOf(']');
			if (stateEnd < 0) {
				throw new IllegalArgumentException("Unbalenced brackets in blockstate: " + in + "<--");
			}
			String stateString = in.substring(stateIndex + 1, in.indexOf(']'));
			String[] state = stateString.split(",");
			for (String entry : state) {
				String[] entrySplit = entry.split("=");
				if (entrySplit.length < 2) {
					throw new IllegalArgumentException("Missing blockstate value: " + entry + "<--");
				}
				
				blockState.put(entrySplit[0], entrySplit[1]);
			}
		}
		
		CompoundTag data = null;
		if (tagIndex >= 0) {
			int tagEnd = in.lastIndexOf('}');
			try {
				data = (CompoundTag) SNBTUtil.fromSNBT(in.substring(tagIndex, tagEnd + 1));
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		
		return new BlockArguement(name, blockState, data);
	}
}
