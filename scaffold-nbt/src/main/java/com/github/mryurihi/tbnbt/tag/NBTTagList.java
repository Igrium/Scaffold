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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.exceptions.MalformedNBTException;
import com.github.mryurihi.tbnbt.tag.internal.NBTListBacker;

public class NBTTagList extends NBTTag implements Iterable<NBTTag> {
	
	private NBTListBacker value;
	
	private TagType typeId;
	
	public NBTTagList(List<NBTTag> value, TagType typeId) {
		this.value = new NBTListBacker();
		this.value.addAll(Objects.requireNonNull(value));
		this.typeId = typeId;
	}
	
	public NBTTagList(TagType typeId) {
		this.value = new NBTListBacker();
		this.typeId = typeId;
	}
	
	public NBTTagList(List<NBTTag> value) {
		this.value = new NBTListBacker();
		this.value.addAll(Objects.requireNonNull(value));
		this.typeId = value.isEmpty()? TagType.END: value.get(0).getTagType();
	}
	
	NBTTagList() {
	}
	
	public List<NBTTag> getValue() {
		return value;
	}
	
	public NBTTag get(int index) {
		return value.get(index);
	}
	
	public boolean add(NBTTag tag) {
		return value.add(tag);
	}
	
	public TagType getTypeId() {
		return typeId;
	}
	
	@Override
	public void writePayloadBytes(DataOutputStream out) throws IOException {
		out.writeByte(typeId.getId());
		out.writeInt(value.size());
		for(NBTTag t: value)
			t.writePayloadBytes(out);
	}
	
	@Override
	public NBTTag readPayloadBytes(DataInputStream in) throws IOException {
		try {
			byte id = in.readByte();
			int length = in.readInt();
			typeId = TagType.getTypeById(id);
			value = new NBTListBacker();
			for(int i = 0; i < length; i++)
				value.add(NBTTag.newTagByType(typeId, in));
			return this;
		} catch(IllegalArgumentException e) {
			throw new MalformedNBTException(e);
		}
	}
	
	@Override
	public TagType getTagType() {
		return TagType.LIST;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	@Override
	protected boolean equalsTag(NBTTag tag) {
		return tag.getTagType().equals(TagType.LIST) && tag.getAsTagList().getValue().equals(value);
	}
	
	@Override
	public Iterator<NBTTag> iterator() {
		return value.iterator();
	}
}
