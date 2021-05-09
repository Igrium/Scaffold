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
package com.github.mryurihi.tbnbt.tag.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.tag.NBTTag;

public class NBTListBacker extends ArrayList<NBTTag> implements List<NBTTag> {
	
	private TagType type = TagType.END;
	
	@Override
	public boolean add(NBTTag e) {
		checkValid(e);
		return super.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends NBTTag> c) {
		for(NBTTag t: c)
			checkValid(t);
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends NBTTag> c) {
		for(NBTTag t: c)
			checkValid(t);
		return super.addAll(index, c);
	}
	
	@Override
	public NBTTag set(int index, NBTTag element) {
		checkValid(element);
		return super.set(index, element);
	}
	
	@Override
	public void add(int index, NBTTag element) {
		checkValid(element);
		super.add(index, element);
	}
	
	private void checkValid(NBTTag tag) {
		Objects.requireNonNull(tag);
		if(isEmpty()) type = tag.getTagType();
		else if(tag.getTagType() != type) throw new IllegalArgumentException("tag " + tag + " is not of type " + type);
	}
}
