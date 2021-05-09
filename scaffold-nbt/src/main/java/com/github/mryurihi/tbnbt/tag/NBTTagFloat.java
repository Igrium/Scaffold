/*
MIT License

Copyright (c) 2017 MrYurihi Redstone

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.github.mryurihi.tbnbt.tag;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.github.mryurihi.tbnbt.TagType;

public class NBTTagFloat extends NBTTag {
	
	private float value;
	
	public NBTTagFloat(float value) {
		this.value = value;
	}
	
	NBTTagFloat() {
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	@Override
	public void writePayloadBytes(DataOutputStream out) throws IOException {
		out.writeFloat(value);
	}
	
	@Override
	public NBTTag readPayloadBytes(DataInputStream in) throws IOException {
		this.value = in.readFloat();
		return this;
	}
	
	@Override
	public TagType getTagType() {
		return TagType.FLOAT;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value) + "f";
	}
	
	@Override
	protected boolean equalsTag(NBTTag tag) {
		return tag.getTagType().equals(TagType.FLOAT) &&
			Float.floatToIntBits(tag.getAsTagFloat().getValue()) == Float.floatToIntBits(value);
	}
}
