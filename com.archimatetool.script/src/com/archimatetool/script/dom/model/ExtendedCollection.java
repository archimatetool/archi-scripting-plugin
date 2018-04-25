/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Extended Collection
 * 
 * @author Phillip Beauvoir
 */
public class ExtendedCollection extends ArrayList<EObjectProxy> implements IModelConstants {
    
    public ExtendedCollection() {
        super();
    }
    
    public EObjectProxy val() {
        return isEmpty() ? null : get(0);
    }
    
    public List<Object> getId() {
        return attr(ID);
    }

    public List<Object> getName() {
        return attr(NAME);
    }
    
    public ExtendedCollection setName(String name) {
        return attr(NAME, name);
    }
    
    public List<Object> getDocumentation() {
        return attr(DOCUMENTATION);
    }
    
    public ExtendedCollection setDocumentation(String documentation) {
        return attr(DOCUMENTATION, documentation);
    }
    
    /**
     * @return class type of members
     */
    public List<String> getType() {
        List<String> list = new ArrayList<>();
        
        for(EObjectProxy object : this) {
            list.add(object.getType());
        }
        
        return list;
    }
    
    /**
     * Add a property to this collection
     * @param key
     * @param value
     */
    public ExtendedCollection addProperty(String key, String value) {
        for(EObjectProxy object : this) {
            object.addProperty(key, value);
        }
        
        return this;
    }
    
    /**
     * Add the property only if it doesn't already exists, or update it if it does.
     * If an object already has multiple properties matching the name, all of them are updated.
     * Returns the updated collection
     * @param key
     * @param value
     */
    public ExtendedCollection addOrUpdateProperty(String key, String value) {
        for(EObjectProxy object : this) {
            object.addOrUpdateProperty(key, value);
        }
        
        return this;
    }

    /**
     * Update the property with new value
     * If this object already has multiple properties matching the key, all of them are updated.
     * @param key
     * @param value
     * @return this
     */
    public ExtendedCollection updateProperty(String key, String value) {
        for(EObjectProxy object : this) {
            object.updateProperty(key, value);
        }
        
        return this;
    }

    /**
     * @return an array of strings containing the list of properties keys. A key appears only once (duplicates are removed)
     */
    public Set<String> getPropertyKey() {
        Set<String> list = new LinkedHashSet<String>();
        
        for(EObjectProxy object : this) {
            list.addAll(object.getPropertyKey());
        }
        
        return list;
    }
    
    /**
     * @param key
     * @return return an array of array containing the value of property named "key" for each object of the collection.
     * This has to be an array of array because the property can appear more than once on a same object
     */
    public List<List<String>> getPropertyValue(String key) {
        List<List<String>> list = new ArrayList<List<String>>();
        
        for(EObjectProxy object : this) {
            List<String> result = object.getPropertyValue(key);
            if(!result.isEmpty()) {
                list.add(result);
            }
        }
        
        return list;
    }

    /**
     * Remove all instances of property "key" on each object of the collection. Returns the updated collection
     * @param key
     */
    public ExtendedCollection removeProperty(String key) {
        for(EObjectProxy object : this) {
            object.removeProperty(key);
        }
        
        return this;
    }

    /**
     * Remove (all instances of) property "key" that matches "value" on each object of the collection. Returns the updated collection.
     * @param key
     * @param value
     */
    public ExtendedCollection removeProperty(String key, String value) {
        for(EObjectProxy object : this) {
            object.removeProperty(key, value);
        }
        
        return this;
    }

    public List<Object> attr(String attribute) {
        List<Object> list = new ArrayList<>();
        
        for(EObjectProxy object : this) {
            Object attr = object.attr(attribute);
            if(attr != null) {
                list.add(attr);
            }
        }
        
        return list;
    }

    public ExtendedCollection attr(String attribute, Object value) {
        for(EObjectProxy object : this) {
            object.attr(attribute, value);
        }
        
        return this;
    }

}
