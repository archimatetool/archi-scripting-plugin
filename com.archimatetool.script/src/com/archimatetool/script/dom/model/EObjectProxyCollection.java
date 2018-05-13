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
 * Extended Collection of EObjectProxy objects
 * 
 * @author Phillip Beauvoir
 */
public class EObjectProxyCollection<T extends EObjectProxy> extends ArrayList<T> implements IModelConstants {
    
    EObjectProxyCollection() {
        super();
    }
    
    public T val() {
        return isEmpty() ? null : get(0);
    }
    
    public Object getId() {
        return attr(ID);
    }

    public Object getName() {
        return attr(NAME);
    }
    
    public EObjectProxyCollection<T> setName(String name) {
        return attr(NAME, name);
    }
    
    public Object getDocumentation() {
        return attr(DOCUMENTATION);
    }
    
    public EObjectProxyCollection<T> setDocumentation(String documentation) {
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
     * Delete all in collection
     */
    public EObjectProxyCollection<T> delete() {
        for(EObjectProxy object : this) {
            object.delete();
        }
        
        return this;
    }
    
    /**
     * Invoke a function (method) with parameters
     * @param methodName
     * @param args
     * @return
     */
    public Object invoke(String methodName, Object... args) {
        for(EObjectProxy object : this) {
            object.invoke(methodName, args);
        }
        
        return this;
    }

    /**
     * @return the descendants of each object in the set of matched objects
     */
    public EObjectProxyCollection<? extends EObjectProxy> find() {
        return find(null);
    }
    
    /**
     * @param selector
     * @return the descendants of each object in the set of matched objects
     */
    public EObjectProxyCollection<? extends EObjectProxy> find(String selector) {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<>();
        
        for(EObjectProxy object : this) {
            for(EObjectProxy child : selector == null ? object.find() : object.find(selector)) {
                if(!list.contains(child)) {
                    list.add(child);
                }
            }
        }
        
        return list;
    }
    
    /**
     * @return children as collection. Default is an empty list
     */
    public EObjectProxyCollection<? extends EObjectProxy> children() {
        return children(null);
    }
    
    /**
     * @return children with selector as collection. Default is an empty list
     */
    public EObjectProxyCollection<? extends EObjectProxy> children(String selector) {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<>();
        
        for(EObjectProxy object : this) {
            for(EObjectProxy child : selector == null ? object.children() : object.children(selector)) {
                if(!list.contains(child)) {
                    list.add(child);
                }
            }
        }
        
        return list;
    }
    
    /**
     * @return parent
     */
    public EObjectProxyCollection<? extends EObjectProxy> parent() {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<>();
        
        for(EObjectProxy object : this) {
            if(object.parent() != null) {
                list.add(object.parent());
            }
        }
        
        return list;
    }
    
    /**
     * Add a property to this collection
     * @param key
     * @param value
     */
    public EObjectProxyCollection<T> addProperty(String key, String value) {
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
    public EObjectProxyCollection<T> addOrUpdateProperty(String key, String value) {
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
    public EObjectProxyCollection<T> updateProperty(String key, String value) {
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
     * This has to be an array of array because the property can appear more than once on the same object
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
     * @return All Property pairs
     */
    public List<Property> getProperties() {
        List<Property> list = new ArrayList<Property>();
        
        for(EObjectProxy object : this) {
            list.addAll(object.getProperties());
        }
        
        return list;
    }

    /**
     * Remove all instances of property "key" on each object of the collection. Returns the updated collection
     * @param key
     */
    public EObjectProxyCollection<T> removeProperty(String key) {
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
    public EObjectProxyCollection<T> removeProperty(String key, String value) {
        for(EObjectProxy object : this) {
            object.removeProperty(key, value);
        }
        
        return this;
    }

    public Object attr(String attribute) {
    	return isEmpty() ? null : get(0).attr(attribute);
    }

    public EObjectProxyCollection<T> attr(String attribute, Object value) {
        for(EObjectProxy object : this) {
            object.attr(attribute, value);
        }
        
        return this;
    }

}
