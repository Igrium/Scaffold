package org.scaffoldeditor.nbt.math;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.joml.Options;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.joml.Runtime;

/**
 * Boilerplate read-only reimplementation of {@link Vector3i} allowing for
 * subclasses to provide actual values.
 */
public abstract class AbstractVector3i implements Vector3ic {

    private Vector3i vectorProxy = new Vector3i();

    @Override
    public IntBuffer get(IntBuffer buffer) {
        checkProxy();
        return vectorProxy.get(buffer);
    }

    @Override
    public IntBuffer get(int index, IntBuffer buffer) {
        checkProxy();
        return vectorProxy.get(index, buffer);
    }

    @Override
    public ByteBuffer get(ByteBuffer buffer) {
        checkProxy();
        return vectorProxy.get(buffer);
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        checkProxy();
        return vectorProxy.get(index, buffer);
    }

    @Override
    public Vector3ic getToAddress(long address) {
        checkProxy();
        return vectorProxy.getToAddress(address);
    }

    @Override
    public Vector3i sub(Vector3ic v, Vector3i dest) {
        dest.x = x() - v.x();
        dest.y = y() - v.y();
        dest.z = z() - v.z();
        return dest;
    }

    @Override
    public Vector3i sub(int x, int y, int z, Vector3i dest) {
        dest.x = x() - x;
        dest.y = y() - y;
        dest.z = z() - z;
        return dest;
    }

    @Override
    public Vector3i add(Vector3ic v, Vector3i dest) {
        dest.x = x() + v.x();
        dest.y = y() + v.y();
        dest.z = z() + v.z();
        return dest;
    }

    @Override
    public Vector3i add(int x, int y, int z, Vector3i dest) {
        dest.x = x() + x;
        dest.y = y() + y;
        dest.z = z() + z;
        return dest;
    }

    @Override
    public Vector3i mul(int scalar, Vector3i dest) {
        dest.x = x() * scalar;
        dest.y = y() * scalar;
        dest.z = z() * scalar;
        return dest;
    }

    @Override
    public Vector3i mul(Vector3ic v, Vector3i dest) {
        dest.x = x() * v.x();
        dest.y = y() * v.y();
        dest.z = z() * v.z();
        return dest;
    }

    @Override
    public Vector3i mul(int x, int y, int z, Vector3i dest) {
        dest.x = x() * x;
        dest.y = y() * y;
        dest.z = z() * z;
        return dest;
    }

    @Override
    public Vector3i div(float scalar, Vector3i dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int) (x() * invscalar);
        dest.y = (int) (y() * invscalar);
        dest.z = (int) (z() * invscalar);
        return dest;
    }

    @Override
    public Vector3i div(int scalar, Vector3i dest) {
        dest.x = x() / scalar;
        dest.y = y() / scalar;
        dest.z = z() / scalar;
        return dest;
    }

    @Override
    public long lengthSquared() {
        return x() * x() + y() * y() + z() * z();
    }

    @Override
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    @Override
    public double distance(Vector3ic v) {
        int dx = this.x() - v.x();
        int dy = this.y() - v.y();
        int dz = this.z() - v.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public double distance(int x, int y, int z) {
        int dx = this.x() - x;
        int dy = this.y() - y;
        int dz = this.z() - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public long gridDistance(Vector3ic v) {
        return Math.abs(v.x() - x()) + Math.abs(v.y() - y())  + Math.abs(v.z() - z());
    }

    @Override
    public long gridDistance(int x, int y, int z) {
        return Math.abs(x - x()) + Math.abs(y - y()) + Math.abs(z - z());
    }

    @Override
    public long distanceSquared(Vector3ic v) {
        int dx = this.x() - v.x();
        int dy = this.y() - v.y();
        int dz = this.z() - v.z();
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public long distanceSquared(int x, int y, int z) {
        int dx = this.x() - x;
        int dy = this.y() - y;
        int dz = this.z() - z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Return a string representation of this vector.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<code>0.000E0;-</code>".
     * 
     * @return the string representation
     */
    public String toString() {
        return Runtime.formatNumbers(toString(Options.NUMBER_FORMAT));
    }

    /**
     * Return a string representation of this vector by formatting the vector components with the given {@link NumberFormat}.
     * 
     * @param formatter
     *          the {@link NumberFormat} used to format the vector components with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(x()) + " " + formatter.format(y()) + " " + formatter.format(z()) + ")";
    }

    @Override
    public Vector3i negate(Vector3i dest) {
        dest.x = -x();
        dest.y = -y();
        dest.z = -z();
        return dest;
    }

    @Override
    public Vector3i min(Vector3ic v, Vector3i dest) {
        dest.x = x() < v.x() ? x() : v.x();
        dest.y = y() < v.y() ? y() : v.y();
        dest.z = z() < v.z() ? z() : v.z();
        return dest;
    }

    @Override
    public Vector3i max(Vector3ic v, Vector3i dest) {
        dest.x = x() > v.x() ? x() : v.x();
        dest.y = y() > v.y() ? y() : v.y();
        dest.z = z() > v.z() ? z() : v.z();
        return dest;
    }

    @Override
    public int get(int component) throws IllegalArgumentException {
        switch (component) {
            case 0:
                return x();
            case 1:
                return y();
            case 2:
                return z();
            default:
                throw new IllegalArgumentException();
            }
    }

    @Override
    public int maxComponent() {
        float absX = Math.abs(x());
        float absY = Math.abs(y());
        float absZ = Math.abs(z());
        if (absX >= absY && absX >= absZ) {
            return 0;
        } else if (absY >= absZ) {
            return 1;
        }
        return 2;
    }

    @Override
    public int minComponent() {
        float absX = Math.abs(x());
        float absY = Math.abs(y());
        float absZ = Math.abs(z());
        if (absX < absY && absX < absZ) {
            return 0;
        } else if (absY < absZ) {
            return 1;
        }
        return 2;
    }

    @Override
    public Vector3i absolute(Vector3i dest) {
        dest.x = Math.abs(this.x());
        dest.y = Math.abs(this.y());
        dest.z = Math.abs(this.z());
        return dest;
    }

    @Override
    public boolean equals(int x, int y, int z) {
        return (
            this.x() == x &&
            this.y() == y &&
            this.z() == z
        );
    }

    private void checkProxy() {
        if (!vectorProxy.equals(x(), y(), z())) {
            vectorProxy = new Vector3i(x(), y(), z());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3ic)) return false;
        Vector3ic vec = (Vector3ic) obj;
        return (
            vec.x() == this.x() &&
            vec.y() == this.y() &&
            vec.z() == this.z()
        );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x();
        result = prime * result + y();
        result = prime * result + z();
        return result;
    }
    
}
