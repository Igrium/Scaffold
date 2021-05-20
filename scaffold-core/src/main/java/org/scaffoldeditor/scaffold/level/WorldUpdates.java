package org.scaffoldeditor.scaffold.level;

import java.util.EventListener;
import java.util.EventObject;
import java.util.Set;

import org.scaffoldeditor.nbt.block.Chunk.SectionCoordinate;


public final class WorldUpdates {
	public static class WorldUpdateEvent extends EventObject {
		
		private static final long serialVersionUID = -8940483835789392307L;
		/**
		 * The sections that were updated. If empty, the entire world has been recompiled.
		 */
		public final Set<SectionCoordinate> updatedSections;
		
		public WorldUpdateEvent(Object source, Set<SectionCoordinate> updatedSections) {
			super(source);
			this.updatedSections = updatedSections;
		}
	}
	
	public interface WorldUpdateListener extends EventListener {
		void onWorldUpdated(WorldUpdateEvent e);
	}
}
