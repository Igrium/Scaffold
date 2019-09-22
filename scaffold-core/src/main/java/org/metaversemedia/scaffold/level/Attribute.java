package org.metaversemedia.scaffold.level;

import org.json.JSONException;
import org.json.JSONObject;
import org.metaversemedia.scaffold.math.Vector;

/**
 * Describes an entity attribute slot
 * @author Sam54123
 *
 */
public class Attribute {
	
	/**
	 * The types allowed for attributes
	 * @author Sam54123
	 *
	 */
	public enum Type {
		STRING, INTEGER, FLOAT, BOOLEAN, VECTOR, UNKNOWN;
	}
	
	/* Actual value of the attribute */
	private Object value;
	
	private Type type;

	
	/**
	 * Create an  attribute with a value.
	 * @param value Value for the attribute
	 */
	public Attribute(Object value) {
		if (value.getClass().isInstance(String.class)) {
			type = Type.STRING;
		} else if (value.getClass().isInstance(Integer.class)) {
			type = Type.INTEGER;
		} else if (value.getClass().isInstance(Float.class)) {
			type = Type.FLOAT;
		} else if (value.getClass().isInstance(Boolean.class)) {
			type = Type.BOOLEAN;
		} else if (value.getClass().isInstance(Vector.class)) {
			type = Type.VECTOR;
		} else {
			type = Type.UNKNOWN;
		}
		
		this.value = value;
		
	}
	
	/**
	 * Create an empty attribute with a type.
	 * @param type Type of attribute
	 */
	public Attribute(Type type) {
		this.type = type;
	}
	
	/**
	 * Get attribute type.
	 * @return Attribute type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Get attribute value.
	 * @return Value
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Gets value as a string if it is a string. Otherwise, returns null.
	 * @return Value
	 */
	public String getString() {
		if (type == Type.STRING) {
			return (String) value;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Gets value as an integer if it is an integer. Otherwise, returns null.
	 * @return Value
	 */
	public Integer getInt() {
		if (type == Type.INTEGER) {
			return (int) value;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets value as a float if it is a float. Otherwise, returns null.
	 * @return Value
	 */
	public Float getFloat() {
		if (type == Type.FLOAT) {
			return (float) value;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets value as a boolean if it is a boolean. Otherwise, returns null.
	 * @return Value
	 */
	public Boolean getBoolean() {
		if (type == Type.BOOLEAN) {
			return (boolean) value;
		} else {
			return null;
		}
	}
	
	/**
	 * Gets value as a vector if it is a vector. Otherwise, returns null.
	 * @return Value
	 */
	public Vector getVector() {
		if (type == Type.VECTOR) {
			return (Vector) value;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Attempt to set this attribute's value
	 * @param value Value to set
	 */
	public void setValue(Object value) {
		// Make sure type is correct
		if (type == Type.STRING && value.getClass().isInstance(String.class)) {
			this.value = (String) value;
		} else if (type == Type.INTEGER && value.getClass().isInstance(Integer.class)) {
			this.value = (int) value;
		} else if (type == Type.FLOAT && value.getClass().isInstance(Float.class)) {
			this.value = (float) value;
		} else if (type == Type.BOOLEAN && value.getClass().isInstance(Boolean.class)) {
			this.value = (boolean) value;
		} else if (type == Type.VECTOR && value.getClass().isInstance(Vector.class)) {
			this.value = (Vector) value;
		} else if (type == Type.UNKNOWN) {
			this.value = value;
		}
	}
	
	/**
	 * Serialize attribute into JSONObject.
	 * @return Serialized attribute.
	 */
	public JSONObject serialize() {
		JSONObject object = new JSONObject();
		
		// Set type
		object.put("type", type);
		
		// Set value
		if (type == Type.VECTOR) {
			object.put("value", ((Vector) value).toJSONArray());
		} else {
			object.put("value", value);
		}
		
		return object;
	}
	
	/**
	 * Unserialize an attribute
	 * @param serialized Serialized attribute
	 * @return Unserialized attribute
	 */
	public static Attribute unserialize(JSONObject serialized) {
		try {
			// Get type
			Attribute attribute = new Attribute(serialized.getEnum(Type.class, "type"));
			
			// Get value
			if (attribute.getType() == Type.VECTOR) {
				attribute.value = Vector.fromJSONArray(serialized.getJSONArray("value"));
			} else {
				attribute.setValue(serialized.get("value"));
			}
			
			return attribute;
			
		} catch (JSONException e) {
			System.out.println("Improperly formatted attribute: "+serialized.toString());
			return null;
		}
		
	}
	
}
