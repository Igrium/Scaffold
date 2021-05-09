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
import java.util.Arrays;
import java.util.stream.IntStream;

import com.github.mryurihi.tbnbt.TagType;

public class NBTTagIntArray extends NBTTag {
	
	private int[] value;
	
	public NBTTagIntArray(int[] value) {
		this.value = value;
	}
	
	NBTTagIntArray() {
	}
	
	public int[] getValue() {
		return value;
	}
	
	public void setValue(int[] value) {
		this.value = value;
	}
	
	@Override
	public void writePayloadBytes(DataOutputStream out) throws IOException {
		out.writeInt(value.length);
		for(int i: value)
			out.writeInt(i);
	}
	
	@Override
	public NBTTag readPayloadBytes(DataInputStream in) throws IOException {
		value = new int[in.readInt()];
		for(int i = 0; i < value.length; i++)
			value[i] = in.readInt();
		return this;
	}
	
	@Override
	public TagType getTagType() {
		return TagType.INT_ARRAY;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("[I;").append(
			String.join(", ", IntStream.of(value).mapToObj(l -> String.valueOf(l)).toArray(len -> new String[len]))
		).append("]").toString();
	}
	
	@Override
	protected boolean equalsTag(NBTTag tag) {
		return tag.getTagType().equals(TagType.INT_ARRAY) && Arrays.equals(tag.getAsTagIntArray().getValue(), value);
	}
}
