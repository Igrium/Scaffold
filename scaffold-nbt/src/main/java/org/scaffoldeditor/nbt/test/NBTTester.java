package org.scaffoldeditor.nbt.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.scaffoldeditor.nbt.block.BlockWorld;

public class NBTTester {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: scaffold-nbt [level.dat]");
			return;
		}
		
		String levelDat = args[0];
		
		Path worldFolder = Paths.get(levelDat).getParent();
		Path regionFolder = worldFolder.resolve("region");
		
		try {
			BlockWorld world = BlockWorld.deserialize(regionFolder.toFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
