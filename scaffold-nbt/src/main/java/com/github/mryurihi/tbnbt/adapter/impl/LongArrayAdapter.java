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
package com.github.mryurihi.tbnbt.adapter.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.github.mryurihi.tbnbt.TagType;
import com.github.mryurihi.tbnbt.adapter.AdapterRegistry;
import com.github.mryurihi.tbnbt.adapter.NBTAdapter;
import com.github.mryurihi.tbnbt.adapter.NBTParseException;
import com.github.mryurihi.tbnbt.adapter.TypeWrapper;

public class LongArrayAdapter extends NBTAdapter<Long[]> {
	
	@Override
	public Long[] fromNBT(
		TagType id,
		DataInputStream payload,
		TypeWrapper<?> type,
		AdapterRegistry registry
	) throws NBTParseException {
		if(
			!id.equals(TagType.LONG_ARRAY)
		) throw new NBTParseException(String.format("id %s does not match required id 12", id.getId()));
		Long[] out = new Long[(Integer) registry.getIntAdapter().fromNBT(TagType.LONG, payload, null, registry)];
		for(int i = 0; i < out.length; i++) {
			out[i] = (Long) registry.getLongAdapter().fromNBT(TagType.LONG, payload, null, registry);
		}
		return out;
	}
	
	@Override
	public void toNBT(
		DataOutputStream out,
		Object object,
		TypeWrapper<?> type,
		AdapterRegistry registry
	) throws NBTParseException {
		Long[] longArr;
		if(object instanceof long[]) {
			longArr = new Long[((long[]) object).length];
			for(int i = 0; i < longArr.length; i++) {
				longArr[i] = ((long[]) object)[i];
			}
		} else longArr = (Long[]) object;
		registry.writeInt(out, longArr.length);
		for(Long b: longArr)
			registry.writeLong(out, b);
	}
	
	@Override
	public TagType getId() {
		return TagType.LONG_ARRAY;
	}
	
}
