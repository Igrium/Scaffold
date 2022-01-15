package org.scaffoldeditor.scaffold.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.scaffoldeditor.scaffold.annotation.Attrib;
import org.scaffoldeditor.scaffold.level.entity.attribute.Attribute;

/**
 * <p>
 * Handles attribute management via Java fields. Intended to ease the
 * programming of new Scaffold entities.
 * </p>
 * <p>
 * To define an attribute, simply mark a field in your class with
 * {@link Attrib @Attrib}. The field must be a type decending from
 * {@link Attribute}. Upon instantiation, the constructor will scan all the
 * fields of your class and its parents (subclasses first) and keep track of all
 * fields marked with this annotation. Then, these values can be dynamically
 * accessed and written to from various functions around the program.
 * </p>
 * <p>
 * Attributes may also be defined at runtime. To do this, simply run
 * {@link #setAttribute(String, Attribute)} on an attribute that's not present
 * in the class or manually add it to {@link #additionalAttributes}.
 * </p>
 */
public class AttributeHolder {

    /**
     * A set of attribute values that aren't defined by a field.
     */
    protected Map<String, Attribute<?>> additionalAttributes = new HashMap<>();

    private Map<String, Field> attributeFields = new HashMap<>();

    public AttributeHolder() {
        setupClassReflection(this.getClass());
    }

    /**
     * Get whether this object has an attribute with a given name.
     * 
     * @param name The name of the attribute to check for.
     * @return Attribute exists?
     */
    public boolean hasAttribute(String name) {
        return (attributeFields.containsKey(name) || additionalAttributes.containsKey(name));
    }
    
    /**
     * Check if a given attribute is defined by a field (as supposed to being a
     * runtime-defined attribute).
     * 
     * @param name The name of the attribute to check.
     * @return Is this attribute field-defined?
     */
    public boolean hasFieldAttribute(String name) {
        return attributeFields.containsKey(name);
    }
    
    /**
     * Get the current value of an attribute.
     * 
     * @param name The name of the attribute to get.
     * @return The attribute's value, or <code>null</code> if no attribute exists by
     *         that name.
     */
    public Attribute<?> getAttribute(String name) {
        Field field = attributeFields.get(name);
        if (field != null) {
            try {
                return (Attribute<?>) field.get(this);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                LogManager.getLogger().error("Unable to retrieve the value of attribute: "+name, e);
                return null;
            }
        } 
        return additionalAttributes.get(name);
    }

    /**
     * Get all of this object's attributes.
     * 
     * @return Map of all this object's attributes. Changes to this map do not
     *         reflect in the underlying object.
     */
    public Map<String, Attribute<?>> getAllAttributes() {

        Map<String, Attribute<?>> attributes = new HashMap<>();
        for (String name : getAttributeNames()) {
            attributes.put(name, getAttribute(name));
        }

        return attributes;
    }

    /**
     * Get the names of all this object's attributes.
     * @return Set of attribute names.
     */
    public Set<String> getAttributeNames() {
        Set<String> names = new HashSet<>();
        names.addAll(attributeFields.keySet());
        names.addAll(additionalAttributes.keySet());
        return names;
    }
    
    /**
     * Set an attribute in this object. If the attribute is defined by a field, the
     * said field will be updated. If not, it will be stored in a map with the other
     * runtime-defined attributes.
     * 
     * @param name  The name of the attribute to set.
     * @param value The value to set it to.
     * @throws IllegalArgumentException If you attempt to assign an attribute to a
     *                                  typed field that doesn't support said
     *                                  attribute.
     */
    public void setAttribute(String name, Attribute<?> value) {
        setAttributeInternal(name, value);
        onSetAttributes(Map.of(name, value));
    }

    private void setAttributeInternal(String name, Attribute<?> value) {
        Field field = attributeFields.get(name);
        if (field == null) {
            additionalAttributes.put(name, value);
            return;
        }

        if (!field.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Attribute of type "+value.getClass().getSimpleName()+" is not assignable to a field of type "+field.getType().getSimpleName());
        }
        
        try {
            field.set(this, value);
        } catch (IllegalAccessException e) {
            throw new SecurityException("Unable to set the value of attribute: "+name, e);
        }
    }

    /**
     * Set multiple attributes in this object. Identical to
     * {@link #setAttribute(String, Attribute)}, except that it sets multiple attributes
     * together while only running internal update code once.
     * 
     * @param values The attributes to set.
     * @throws IllegalArgumentException If you attempt to assign an attribute to a
     *                                  typed field that doesn't support said
     *                                  attribute.
     */
    public void setAttributes(Map<String, Attribute<?>> values) {
        for (String name : values.keySet()) {
            setAttributeInternal(name, values.get(name));
        }
        onSetAttributes(values);
    }

    /**
     * Called whenever one or more attributes have been updated.
     * @param updated A map of the attribute names which have been updated and their values.
     */
    protected void onSetAttributes(Map<String, Attribute<?>> updated) {};

    private void setupClassReflection(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Attrib.class)) continue;
            if (!Attribute.class.isAssignableFrom(field.getType())) {
                LogManager.getLogger().error("Attribute field {} defined in {} is not a subclass of Attribute!", field.getName(), clazz.getSimpleName());
                continue;
            }
            field.setAccessible(true);
            Attrib attrib = field.getAnnotation(Attrib.class);
            String name = attrib.name();
            if (name == null || name.length() == 0) {
                name = field.getName();
            }
            if (attributeFields.containsKey(name)) {
                LogManager.getLogger().error("Duplicate attribute name in {}: '{}'", this.getClass().getSimpleName(), name);
                continue;
            }
            attributeFields.put(name, field);
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null || superclass.equals(Object.class)) return;
        setupClassReflection(superclass);
    }
}
