package org.scaffoldeditor.scaffold.util;

import java.util.AbstractList;

import org.joml.Vector3i;
import org.joml.Vector3ic;

/**
 * Represents a flattened 3D range of coordinates.
 */
public class Range3D extends AbstractList<Vector3ic> {

    private int sizeX;
    private int sizeY;
    private int sizeZ;
    
    public Range3D(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    @Override
    // https://stackoverflow.com/questions/10903149/how-do-i-compute-the-linear-index-of-a-3d-coordinate-and-vice-versa
    public Vector3ic get(int index) {
        int x = index % sizeX;
        index /= sizeX;
        int y = index % sizeY;
        index /= sizeY;
        int z = index;
        return new Vector3i(x, y, z);
    }

    @Override
    public int size() {
        return sizeX * sizeY * sizeZ;
    }
    
}
