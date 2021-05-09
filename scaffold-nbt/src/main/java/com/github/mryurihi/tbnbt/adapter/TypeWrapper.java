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
package com.github.mryurihi.tbnbt.adapter;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class TypeWrapper<E> {
	
	private Type type;
	private Class<E> classType;
	
	@SuppressWarnings("unchecked")
	protected TypeWrapper() {
		try {
			this.type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		} catch(ClassCastException e) {
			throw new RuntimeException(
				"This class is not parameterized somehow. If this appears, you have messed up badly"
			);
		}
		this.classType = (Class<E>) getTypeAsClassType(type);
	}
	
	@SuppressWarnings("unchecked")
	private TypeWrapper(Type type) {
		this.type = type;
		this.classType = (Class<E>) getTypeAsClassType(type);
	}
	
	public Class<E> getClassType() {
		return classType;
	}
	
	public Type getType() {
		return type;
	}
	
	public static <E> TypeWrapper<E> of(Class<E> cls) {
		return new TypeWrapper<>(cls);
	}
	
	public static TypeWrapper<?> of(Type type) {
		return new TypeWrapper<>(type);
	}
	
	private static Class<?> getTypeAsClassType(Type type) {
		if(type instanceof Class) return (Class<?>) type;
		else if(type instanceof ParameterizedType) return (Class<?>) ((ParameterizedType) type).getRawType();
		else if(type instanceof GenericArrayType) {
			Type arrayType = ((GenericArrayType) type).getGenericComponentType();
			return Array.newInstance(getTypeAsClassType(arrayType), 0).getClass();
		} else if(type instanceof TypeVariable) return Object.class;
		else if(type instanceof WildcardType) return getTypeAsClassType(((WildcardType) type).getUpperBounds()[0]);
		throw new RuntimeException(
			String.format("%s is not an instance of any type implemented by this method", type.getTypeName())
		);
	}
}
