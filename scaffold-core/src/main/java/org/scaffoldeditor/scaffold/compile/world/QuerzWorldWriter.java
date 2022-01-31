package org.scaffoldeditor.scaffold.compile.world;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.nbt.block.Block;
import org.scaffoldeditor.nbt.block.BlockWorld;
import org.scaffoldeditor.nbt.block.Chunk;
import org.scaffoldeditor.nbt.block.WorldMath.ChunkCoordinate;

import net.querz.mca.MCAFile;
import net.querz.mca.MCAUtil;

/**
 * A wrapper class allowing Scaffold to use Querz's MCA tools.
 */
public class QuerzWorldWriter implements WorldWriter {
	
	// In this case, ChunkCoordinate is being repurposed as a region coordinate.
	private Map<ChunkCoordinate, MCAFile> mcaFiles = new HashMap<>();

	@Override
	public void writeWorld(Path worldFolder, BlockWorld world) throws IOException {
		File regionFolder = worldFolder.resolve("region").toFile();
		regionFolder.mkdir();
		
		for (ChunkCoordinate chunkCoord : world.getChunks().keySet()) {
			ChunkCoordinate regionCoord = new ChunkCoordinate(MCAUtil.chunkToRegion(chunkCoord.x),
					MCAUtil.chunkToRegion(chunkCoord.z()));
			
			MCAFile regionFile;
			if (mcaFiles.containsKey(regionCoord)) {
				regionFile = mcaFiles.get(regionCoord);
			} else {
				regionFile = new MCAFile(regionCoord.x, regionCoord.z());
				mcaFiles.put(regionCoord, regionFile);
			}
			
			net.querz.mca.Chunk chunk = net.querz.mca.Chunk.newChunk();
			Chunk scaffoldChunk = world.getChunks().get(chunkCoord);
			
			for (int x = 0; x < Chunk.WIDTH; x++) {
				for (int y = 0; y < Chunk.HEIGHT; y++) {
					for (int z = 0; z < Chunk.LENGTH; z++) {
						Block block = scaffoldChunk.blockAt(x, y, z);
						if (block != null) {
							chunk.setBlockStateAt(x, y, z, block.toPaletteEntry(), false);	
						}					
					}
				}
			}
			regionFile.setChunk(chunkCoord.x, chunkCoord.z, chunk);
		}
		
		for (ChunkCoordinate regionCoord : mcaFiles.keySet()) {
			MCAFile regionFile = mcaFiles.get(regionCoord);
			regionFile.cleanupPalettesAndBlockStates();
			File file = regionFolder.toPath().resolve(MCAUtil.createNameFromRegionLocation(regionCoord.x, regionCoord.z)).toFile();
			LogManager.getLogger().info("Writing region file: "+file.toString());
			MCAUtil.write(regionFile, file, true);
		}
	}
}
