/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;


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
    	EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<>();
    	
    	for(EObjectProxy object : this) {
            for(EObjectProxy child : object.find()) {
                if(!list.contains(child)) {
                    list.add(child);
                }
            }
        }
        
        return list;
    }
    
    /**
     * @param selector
     * @return the descendants of each object in the set of matched objects
     */
    public EObjectProxyCollection<? extends EObjectProxy> find(String selector) {
        return find().filter(selector);
    }
    
    /**
     * Filter the collection and keep only objects that match selector
     * @param selector
     * @return a filtered collection
     */
    public EObjectProxyCollection<? extends EObjectProxy> filter(String selector) {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<EObjectProxy>();
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(selector);
        if(filter == null) {
            return list;
        }
        
        // Iterate over the collection and filter objects into the list
        for(EObjectProxy object : this) {
            EObject eObject = object.getEObject();
            
            if(filter.accept(eObject)) {
                list.add(object);
                
                if(filter.isSingle()) {
                    return list;
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
     * Return the list of properties' key for the first object in the collection
     * @return
     */
    public Set<String> prop() {
    	return isEmpty() ? null : val().prop();
    }
    
    /**
     * Return a property value of the first object in the collection.
     * If multiple properties exist with the same key, then return only the first one.
     * @param propKey
     * @return
     */
    public String prop(String propKey) {
    	return isEmpty() ? null : val().prop(propKey);
    }
    
    /**
     * Return a property value of the first object in the collection.
     * If multiple properties exist with the same key, then return only
     * the first one (if duplicate=false) or an array with all values
     * (if duplicate=true).
     * @param propKey
     * @param allowDuplicate
     * @return
     */
    public Object prop(String propKey, boolean allowDuplicate) {
    	return isEmpty() ? null : val().prop(propKey, allowDuplicate);
    }
    
    /**
     * Sets a property for every objects.
     * Property is updated if it already exists.
     * @param propKey
     * @param propValue
     * @return
     */
    public EObjectProxyCollection<T> prop(String propKey, String propValue) {
	    for(EObjectProxy object : this) {
	        object.prop(propKey, propValue);
	    }
	    
	    return this;
    }

    /**
     * Sets a property for every objects.
     * Property is updated if it already exists (if duplicate=false)
     * or added anyway (if duplicate=true).
     * @param propKey
     * @param propValue
     * @param allowDuplicate
     * @return
     */
    public EObjectProxyCollection<T> prop(String propKey, String propValue, boolean allowDuplicate) {
	    for(EObjectProxy object : this) {
	        object.prop(propKey, propValue, allowDuplicate);
	    }
	    
	    return this;
    }

    /**
     * Remove all instances of property "key" on each object of the collection. Returns the updated collection
     * @param key
     */
    public EObjectProxyCollection<T> removeProp(String key) {
        for(EObjectProxy object : this) {
            object.removeProp(key);
        }
        
        return this;
    }

    /**
     * Remove (all instances of) property "key" that matches "value" on each object of the collection. Returns the updated collection.
     * @param key
     * @param value
     */
    public EObjectProxyCollection<T> removeProp(String key, String value) {
        for(EObjectProxy object : this) {
            object.removeProp(key, value);
        }
        
        return this;
    }

    public Object attr(String attribute) {
    	return isEmpty() ? null : val().attr(attribute);
    }

    public EObjectProxyCollection<T> attr(String attribute, Object value) {
        for(EObjectProxy object : this) {
            object.attr(attribute, value);
        }
        
        return this;
    }

}
