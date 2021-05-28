package org.scaffoldeditor.nbt.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockReader;
import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;
import org.scaffoldeditor.nbt.schematic.Construction;
import org.scaffoldeditor.nbt.schematic.Construction.ConstructionSegment;
import org.scaffoldeditor.nbt.schematic.Construction.Section;
import org.scaffoldeditor.nbt.schematic.Construction.SelectionBox;

import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.SNBTUtil;
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

	public Construction parse(InputStream in) throws IOException {
		// The construction format has a lot of back and forth, so it's better to load it all into memory at once.
		byte[] bytes = in.readAllBytes();
		ByteArrayInputStream buffer = new ByteArrayInputStream(bytes);
		DataInputStream data = new DataInputStream(buffer);
		
		// Header
		String magicNum = new String(buffer.readNBytes(8), StandardCharsets.UTF_8);
		int version = data.readUnsignedByte();
		
		// Verify magic number
		data.skip(data.available() - 8);
		String magicNum2 = new String(buffer.readNBytes(8), StandardCharsets.UTF_8);
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
	
	public Construction readVersion0(byte[] bytes) throws IOException {
		
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
		System.out.println(SNBTUtil.toSNBT(meta));	
		System.out.println(toHex(ByteBuffer.wrap(meta.getByteArray("section_index_table"))));
		
		while (sectionIndex.available() > 0) {
			int x = Math.floorDiv(readInt(sectionIndex, true), 16);
			int y = Math.floorDiv(readInt(sectionIndex, true), 16);
			int z = Math.floorDiv(readInt(sectionIndex, true), 16);
			
			int width = sectionIndex.readUnsignedByte();
			int height = sectionIndex.readUnsignedByte();
			int length = sectionIndex.readUnsignedByte();
			
			int dataOffset = (int) readInt(sectionIndex, true);
			int dataLength = (int) readInt(sectionIndex, true);
			System.out.println("X: "+x+", Y: "+y+", Z: "+z);
			System.out.println("Length: "+length+", width: "+width+", height: "+height);
			System.out.println("Section: "+dataOffset+", "+dataLength);
			ByteArrayInputStream sectionReader = new ByteArrayInputStream(bytes, dataOffset, dataLength);
			CompoundTag sectionNBT = (CompoundTag) parser.fromStream(sectionReader).getTag();
			Section section;
			if (sectionVersion == 0) {
				section = sectionVersion0(sectionNBT, width, height, length, construction.palette);
			} else {
				throw new IOException("Unknown section version: "+sectionVersion);
			}
			
			construction.sections.put(new SectionCoordinate(x, y, z), section);
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
	
	public Section sectionVersion0(CompoundTag sectionTag, int width, int height, int length, List<Block> palette) throws IOException {
		int[][][] blocks = new int[width][height][length];
		byte blockArrayType = sectionTag.getByte("blocks_array_type");
		BlockArrayWrapper<?> blockArray = new BlockArrayWrapper<ArrayTag<?>>(blockArrayType, (ArrayTag<?>) sectionTag.get("blocks"));
		System.out.println(SNBTUtil.toSNBT(sectionTag));
		int index = 0;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int z = 0; z < length; z++) {
					blocks[x][y][z] = blockArray.get(index);
					index++;
				}
			}
		}
		System.out.println("Palette: "+palette);
		
		Section section = new Section(width, height, length, blocks, palette);	
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
	
	/**
	 * Converts a 4 byte array of unsigned bytes to an long
	 * @param b an array of 4 unsigned bytes
	 * @return a long representing the unsigned int
	 * @throws IOException 
	 */
	public static final int readInt(byte[] b) throws IOException 
	{
	   return readInt(ByteBuffer.wrap(b), false);
	}
	
	public static final int readInt(InputStream in) throws IOException {
		return readInt(in, false);
	}
	
	public static final int readInt(InputStream in, boolean littleEndian) throws IOException {
		return readInt(ByteBuffer.wrap(in.readNBytes(4)), littleEndian);
	}
	
	public static final int readInt(ByteBuffer buffer, boolean littleEndian) throws IOException {
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
	
	public static String toHex(ByteBuffer bb) {
        StringBuilder sb = new StringBuilder("[ ");
        while (bb.hasRemaining()) {
            sb.append(String.format("%02X ", bb.get()));
        }//from  w w  w.  j  a va  2  s .  c  o  m
        sb.append("]");
        return sb.toString();
    }

	@Override
	public ConstructionSegment readBlockCollection(InputStream in) throws IOException {
		Construction construction = parse(in);
		if (construction.selectionBoxes.size() == 0) {
			throw new IOException("Can only read construction segment if there's a selection box in the construction.");
		}
		return construction.getSegment(construction.selectionBoxes.get(0));
	}
}
