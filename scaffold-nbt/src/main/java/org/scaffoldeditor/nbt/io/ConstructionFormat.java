package org.scaffoldeditor.nbt.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockReader;
import org.scaffoldeditor.nbt.block.SizedBlockCollection;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.schematic.Construction;
import org.scaffoldeditor.nbt.schematic.Construction.ConstructionSegment;
import org.scaffoldeditor.nbt.schematic.Construction.Section;
import org.scaffoldeditor.nbt.schematic.Construction.SelectionBox;

import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.tag.ArrayTag;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.LongArrayTag;

/**
 * Parses Amulet Editor's <a href="https://github.com/Amulet-Team/construction-specification">Construction format</a>
 * @author Igrium
 */
public class ConstructionFormat implements BlockReader<ConstructionSegment> {
	
	final NBTDeserializer parser = new NBTDeserializer(true);
	
	/**
	 * Parse a Construction file. Note: this caches the entire input stream into a byte array during parsing.
	 * Make sure the input stream it's fed is capped.
	 * @param in Input stream to parse.
	 * @return Loaded Construction.
	 * @throws IOException If an IO Exception occurs for any reason.
	 */
	public Construction parse(InputStream in) throws IOException {
		// The construction format has a lot of back and forth, so it's better to load it all into memory at once.
		byte[] bytes = IOUtils.toByteArray(in);
		ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
		DataInputStream data = new DataInputStream(buffer);
		
		// Header
		String magicNum = new String(readNBytes(buffer,8), StandardCharsets.UTF_8);
		int version = data.readUnsignedByte();
		
		// Verify magic number
		data.skip(data.available() - 8);
		String magicNum2 = new String(readNBytes(buffer,8), StandardCharsets.UTF_8);
		if (!magicNum.equals(magicNum2)) {
			throw new IOException("It looks like this file is corrupt. It probably wasn't saved properly");
		}
		
		// Version-specific code.
		if (version == 0) {
			return readVersion0(bytes);
		} else {
			throw new IOException("This build doesn't support construction versions higher than 0.");
		}	
	}
	
	protected Construction readVersion0(byte[] bytes) throws IOException {
		
		// Load metadata
		int meta_offset = (int) readInt(new ByteArrayInputStream(bytes, bytes.length - 12, 4));
		CompoundTag meta = (CompoundTag) parser
				.fromStream(new ByteArrayInputStream(bytes, meta_offset, bytes.length - 12 - meta_offset)).getTag();
		Construction construction = new Construction();
		
		// Load palette
		for (CompoundTag entry : meta.getListTag("block_palette").asCompoundTagList()) {
			String namespace = entry.getString("namespace");
			String blockname = entry.getString("blockname");
			CompoundTag properties = entry.getCompoundTag("properties");
			
			construction.palette.add(new Block(namespace+":"+blockname, properties));
		}
		
		// Load sections at the same time as their entry in the index table.
		byte sectionVersion = meta.getByte("section_version"); 
		DataInputStream sectionIndex = new DataInputStream(
				new ByteArrayInputStream(meta.getByteArray("section_index_table")));
		
		while (sectionIndex.available() > 0) {
			
			int x = readInt(sectionIndex, true);
			int y = readInt(sectionIndex, true);
			int z = readInt(sectionIndex, true);
			
			int width = sectionIndex.readUnsignedByte();
			int height = sectionIndex.readUnsignedByte();
			int length = sectionIndex.readUnsignedByte();
			
			int dataOffset = (int) readInt(sectionIndex, true);
			int dataLength = (int) readInt(sectionIndex, true);

			ByteArrayInputStream sectionReader = new ByteArrayInputStream(bytes, dataOffset, dataLength);
			CompoundTag sectionNBT = (CompoundTag) parser.fromStream(sectionReader).getTag();
			
			SectionCoordinate coord = new SectionCoordinate(
					Math.floorDiv(x, 16), Math.floorDiv(y, 16), Math.floorDiv(z, 16));
			
			Section section;
			if (sectionVersion == 0) {
				section = sectionVersion0(sectionNBT, width, height, length, x - coord.getStartX(),
						y - coord.getStartY(), z - coord.getStartZ(), construction.palette);
			} else {
				throw new IOException("Unknown section version: "+sectionVersion);
			}
			

			construction.sections.put(coord, section);
		}
		
		int[] selectionBoxes = meta.getIntArray("selection_boxes");
		for (int i = 0; i < selectionBoxes.length-5; i += 6) {
			int minX = selectionBoxes[i];
			int minY = selectionBoxes[i + 1];
			int minZ = selectionBoxes[i + 2];
			
			int maxX = selectionBoxes[i + 3];
			int maxY = selectionBoxes[i + 4];
			int maxZ = selectionBoxes[i + 5];
			
			construction.selectionBoxes.add(new SelectionBox(minX, minY, minZ, maxX, maxY, maxZ));
		}

		return construction;
	}
	
	protected Section sectionVersion0(CompoundTag sectionTag, int width, int height, int length, int startX, int startY, int startZ, List<Block> palette) throws IOException {
		int[][][] blocks = new int[width][height][length];
		byte blockArrayType = sectionTag.getByte("blocks_array_type");
		BlockArrayWrapper<?> blockArray = new BlockArrayWrapper<ArrayTag<?>>(blockArrayType, (ArrayTag<?>) sectionTag.get("blocks"));
		int index = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					blocks[x][y][z] = blockArray.get(index);
					index++;
				}
			}
		}
		
		Section section = new Section(width, height, length, blocks, palette, new int[] { startX, startY, startZ });	
		if (sectionTag.getListTag("entities").size() > 0) {
			for (CompoundTag entity : sectionTag.getListTag("entities").asCompoundTagList()) {
				section.entities.add(entity);
			}
		}
		
		if (sectionTag.getListTag("entities").size() > 0) {
			for (CompoundTag entity : sectionTag.getListTag("block_entities").asCompoundTagList()) {
				section.blockEntities.add(entity);
			}	
		}
		
		return section;
	}
	
	
	private static final int readInt(InputStream in) throws IOException {
		return readInt(in, false);
	}
	
	private static final int readInt(InputStream in, boolean littleEndian) throws IOException {
		return readInt(ByteBuffer.wrap(readNBytes(in, 4)), littleEndian);
	}
	
	private static final int readInt(ByteBuffer buffer, boolean littleEndian) throws IOException {
		if (littleEndian) buffer.order(ByteOrder.LITTLE_ENDIAN);
		int num = buffer.getInt();
		return num;
	}
	
	private static class BlockArrayWrapper<T extends ArrayTag<?>> {
		public final T array;
		public final byte type;
		
		public BlockArrayWrapper(byte type, T array) {
			this.array = array;
			this.type = type;
		}
		
		public int get(int index) {
			if (type == 7) {
				ByteArrayTag tag = (ByteArrayTag) array;
				return tag.getValue()[index];
			} else if (type == 11) {
				IntArrayTag tag = (IntArrayTag) array;
				return tag.getValue()[index];
			} else if (type == 12) {
				LongArrayTag tag = (LongArrayTag) array;
				return (int) tag.getValue()[index];
			} else if (type == -1) {
				return -1;
			} else {
				throw new IllegalArgumentException("Unknown array type: "+type);
			}
		}
	}
	
	/**
	 * Parse a construction file and get it's first selection box as a {@link SizedBlockCollection}.
	 * Note: this caches the entire input stream into a byte array during parsing. Make sure the input stream it's fed is capped.
	 */
	public ConstructionSegment readBlockCollection(InputStream in) throws IOException {
		Construction construction = parse(in);
		if (construction.selectionBoxes.size() == 0) {
			throw new IOException("Can only read construction segment if there's a selection box in the construction.");
		}
		return construction.getSegment(construction.selectionBoxes.get(0));
	}
	
	private static byte[] readNBytes(InputStream in, int length) throws IOException {
		byte[] bytes = new byte[length];
		in.read(bytes);
		return bytes;
	}
}
