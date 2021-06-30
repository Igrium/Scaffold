package org.scaffoldeditor.scaffold.block_textures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.BlockAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.DoubleAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.ListAttribute;
import org.scaffoldeditor.scaffold.level.entity.attribute.LongAttribute;
import org.scaffoldeditor.scaffold.math.OpenSimplex2S;

public class NoiseBlockTexture extends SerializableBlockTexture {
	
	public static final String REGISTRY_NAME = "noise";
	private static final double RANGE = Math.sqrt(3d/4d);
	
	public static void register() {
		BlockTextureRegistry.registry.put(REGISTRY_NAME, new BlockTextureFactory<NoiseBlockTexture>() {

			@Override
			public NoiseBlockTexture create() {
				return new NoiseBlockTexture(1, Arrays.asList(new Block[] {new Block("minecraft:stone")}));
			}
		});
	}
	
	private OpenSimplex2S noiseFunction;
	
	public NoiseBlockTexture(long seed, List<Block> blocks) {
		setAttribute("seed", new LongAttribute(seed));
		List<BlockAttribute> blockList = new ArrayList<>();
		for (Block b : blocks) {
			blockList.add(new BlockAttribute(b));
		}
		setAttribute("blocks", new ListAttribute(blockList));
		setAttribute("scale", new DoubleAttribute(1));
	}

	@Override
	public Block blockAt(double x, double y, double z) {
		double scale = ((DoubleAttribute) getAttribute("scale")).getValue();
		double value = noiseFunction.noise3_Classic(x * scale, y * scale, z * scale);
		List<? extends Attribute<?>> blocks = ((ListAttribute) getAttribute("blocks")).getValue();
		if (blocks.size() == 0) {
			return new Block("minecraft:stone");
		}
		
		int index = (int) Math.floor(remap(value, -RANGE, RANGE, 0, blocks.size()));
		Attribute<?> target = blocks.get(index);
		if (!(target instanceof BlockAttribute)) {
			return new Block("minecraft:stone");
		}
		
		return ((BlockAttribute) target).getValue();
	}

	@Override
	public boolean supportsScaling() {
		return true;
	}

	@Override
	public String getRegistryName() {
		return REGISTRY_NAME;
	}

	@Override
	public Set<String> getDefaultAttributes() {
		Set<String> set = new HashSet<>();
		set.add("seed");
		set.add("blocks");
		set.add("scale");
		return set;
	}
	
	@Override
	protected void onUpdateAttribute(String attribute) {
		if (attribute.matches("seed")) {
			noiseFunction = new OpenSimplex2S(((LongAttribute) getAttribute("seed")).getValue());
		}
	}
	
	private static double remap(double value, double low1, double high1, double low2, double high2) {
		return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
	}
}
