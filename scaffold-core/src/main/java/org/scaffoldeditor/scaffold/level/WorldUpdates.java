package org.scaffoldeditor.scaffold.level;

import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;

import org.scaffoldeditor.nbt.block.BlockWorld.ChunkCoordinate;


public final class WorldUpdates {
	public static class WorldUpdateEvent extends EventObject {
		
		private static final long serialVersionUID = -8940483835789392307L;
		/**
		 * The chunks that were updated. If empty, the entire world has been recompiled.
		 */
		public final Set<ChunkCoordinate> updatedChunks;
		
		public WorldUpdateEvent(Object source, Set<ChunkCoordinate> updatedChunks) {
			super(source);
			this.updatedChunks = updatedChunks;
		}
	}
	
	public interface WorldUpdateListener extends EventListener {
		void onWorldUpdated(WorldUpdateEvent e);
	}
}
