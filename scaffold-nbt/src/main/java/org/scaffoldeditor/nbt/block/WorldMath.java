package org.scaffoldeditor.nbt.block;

import java.util.Objects;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.scaffoldeditor.nbt.math.AbstractVector3i;

public final class WorldMath {
    /**
	 * Class to represent chunk coordinate pairs.
	 * @author Igrium
	 */
	public static class ChunkCoordinate implements Comparable<ChunkCoordinate> {
		public final int x;
		public final int z;
		
		public ChunkCoordinate(int x, int z) {
			this.x = x;
			this.z = z;
		}
		
		public ChunkCoordinate(SectionCoordinate c) {
			this.x = c.x;
			this.z = c.z;
		}

        public ChunkCoordinate(Vector2ic c) {
            this.x = c.x();
            this.z = c.y();
        }
		
		public int x() {
			return x;
		}
		
		public int z() {
			return z;
		}
		
		public int getStartX() {
			return x * Chunk.WIDTH;
		}
		
		public int getStartZ() {
			return z * Chunk.LENGTH;
		}
		
		public int getEndX() {
			return getStartX() + Chunk.WIDTH;
		}
		
		public int getEndZ() {
			return getStartZ() + Chunk.LENGTH;
		}
		
		public Vector3i getStartPos() {
			return new Vector3i(getStartX(), 0, getStartZ());
		}
		
		public Vector3i getEndPos() {
			return new Vector3i(getEndX(), Chunk.HEIGHT, getEndZ());
		}
		
		public Vector3i relativize(Vector3ic vec) {
			return new Vector3i(vec.x() - getStartX(), vec.y(), vec.z() - getStartZ());
		}
		
		public Vector3i resolve(Vector3ic vec) {
			return new Vector3i(vec.x() + getStartX(), vec.y(), vec.z() + getStartZ());
		}

        public Vector2i toVector(Vector2i dest) {
            dest.set(x, z);
            return dest;
        }

        public Vector2i toVector() {
            return toVector(new Vector2i());
        }
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ChunkCoordinate)) {
				return false;
			}
			ChunkCoordinate chunkCoordinate = (ChunkCoordinate) obj;
			return chunkCoordinate.x() == x && chunkCoordinate.z() == z;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(x, z);
		}

		@Override
		public int compareTo(ChunkCoordinate o) {
			if (z == o.z) {
				return x - o.x;
			} else {
				return z - o.z;
			}
		}
		
		@Override
		public String toString() {
			return "["+x+", "+z+"]";
		}
	}

	/**
	 * Represents a 3D section coordinate in section space (each unit is 16 blocks).
	 */
    public static class SectionCoordinate extends AbstractVector3i {

		private int x;
		private int y;
		private int z;
		
		public SectionCoordinate(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		
		public SectionCoordinate(Vector3ic vec) {
			this.x = vec.x();
			this.y = vec.y();
			this.z = vec.z();
		}
		
		public SectionCoordinate(ChunkCoordinate c, int i) {
            this(c.x(), i, c.z());
        }

		@Override
		public int x() {
			return x;
		}

		@Override
		public int y() {
			return y;
		}

		@Override
		public int z() {
			return z;
		}

        public ChunkCoordinate getChunk() {
			return new ChunkCoordinate(x, z);
		}
		
		/**
		 * Get the global starting X of the section.
		 */
		public int getStartX() {
			return x * Chunk.WIDTH;
		}
		
		/**
		 * Get the global starting Y of the section.
		 */
		public int getStartY() {
			return y * Section.HEIGHT;
		}
		
		/**
		 * Get the global starting Z of the section.
		 */
		public int getStartZ() {
			return z * Chunk.LENGTH;
		}
		
		/**
		 * Get the global ending X of the section, non-inclusive.
		 */
		public int getEndX() {
			return getStartX() + Chunk.WIDTH;
		}
		
		/**
		 * Get the global ending Y of the section, non-inclusive.
		 */
		public int getEndY() {
			return getStartY() + Section.HEIGHT ;
		}
		
		/**
		 * Get the global ending Z of the section, non-inclusive.
		 */
		public int getEndZ() {
			return getStartZ() + Chunk.LENGTH;
		}
		
		/**
		 * Get a coordinate in local space relative to this section in global space.
		 * @param in Local space coordinate.
		 * @return Global space coordinate.
		 * @see #relativize
		 */
		public Vector3i resolve(Vector3ic in) {
			return new Vector3i(in.x() + getStartX(), in.y() + getStartY(), in.z() + getStartZ());
		}
		
		/**
		 * Get a coordinate in global space relative to this section in local space.
		 * @param in Global space coordinate.
		 * @return Local space coordinate.
		 * @see #resolve
		 */
		public Vector3i relativize(Vector3ic in) {
			return new Vector3i(in.x() - getStartX(), in.y() - getStartY(), in.z() - getStartZ());
		}
		
		/**
		 * Get this section's start position.
		 * @return Start position (inclusive).
		 */
		public Vector3i getStartPos() {
			return new Vector3i(getStartX(), getStartY(), getStartZ());
		}
		
		/**
		 * Get this section's end position (non-inclusive).
		 * @return End position (non-inclusive).
		 */
		public Vector3i getEntPosition() {
			return new Vector3i(getEndX(), getEndY(), getEndZ());
		}
		
		@Override
		public String toString() {
			return "SectionCoordinate["+x+", "+y+", "+z+"]";
		}
		
	}
}
