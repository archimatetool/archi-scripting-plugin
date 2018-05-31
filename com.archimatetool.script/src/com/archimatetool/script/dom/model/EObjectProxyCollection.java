/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;


/**
 * Extended Collection of EObjectProxy objects
 * 
 * @author Phillip Beauvoir
 */
public class EObjectProxyCollection<T extends EObjectProxy> extends ArrayList<T> implements IModelConstants {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7614536950474870868L;

	EObjectProxyCollection() {
        super();
    }
    
    public T first() {
        return isEmpty() ? null : get(0);
    }
    /**
     * Check the current matched set of object against a selector and
     * return true if at least one of these objects matches the given arguments.
     * @param selector
     * @return
     */
    public boolean is(String selector) {
    	   return !this.filter(selector).isEmpty();
    }
    
    // TODO: remove
    private Object getId() {
        return attr(ID);
    }

    // TODO: remove
    private Object getName() {
        return attr(NAME);
    }
    
    // TODO: remove
    private EObjectProxyCollection<? extends EObjectProxy> setName(String name) {
        return attr(NAME, name);
    }
    
    // TODO: remove
    private Object getDocumentation() {
        return attr(DOCUMENTATION);
    }
    
    // TODO: remove
    private EObjectProxyCollection<? extends EObjectProxy> setDocumentation(String documentation) {
        return attr(DOCUMENTATION, documentation);
    }
    
    /**
     * @return class type of members
     * TODO: remove
     */
    private List<String> getType() {
        List<String> list = new ArrayList<>();
        
        for(EObjectProxy object : this) {
            list.add(object.getType());
        }
        
        return list;
    }
    
    /**
     * Delete all in collection
     */
    public EObjectProxyCollection<? extends EObjectProxy> delete() {
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
     * TODO: remove
     */
    protected Object invoke(String methodName, Object... args) {
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
            if(object != null) {
	            EObject eObject = object.getEObject();
	            
	            if(filter.accept(eObject)) {
	                list.add(object);
	                
	                if(filter.isSingle()) {
	                    return list;
	                }
	            }
            }
        }
        
        return list;
    }
    
    /**
     * Remove elements from the set of matched elements.
     * @param selector
     * @return
     */
    public EObjectProxyCollection<? extends EObjectProxy> not(String selector) {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<EObjectProxy>();
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(selector);
        if(filter == null) {
            return list;
        }
        
        // Iterate over the collection and filter objects into the list
        for(EObjectProxy object : this) {
            if(object != null) {
	            EObject eObject = object.getEObject();
	            
	            if(!filter.accept(eObject)) {
	                list.add(object);
	            }
            }
        }
        
        return list;
    }
    
    /**
     * Remove elements from the set of matched elements.
     * @param collection
     * @return
     */
    public EObjectProxyCollection<? extends EObjectProxy> not(EObjectProxyCollection<? extends EObjectProxy> collection) {
    	this.removeAll(collection);
    	return this;
    }
    
    
    /**
     * @return children as collection. Default is an empty list
     */
    public EObjectProxyCollection<? extends EObjectProxy> children() {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<>();
        
        for(EObjectProxy object : this) {
            for(EObjectProxy child : object.children()) {
                if(!list.contains(child)) {
                    list.add(child);
                }
            }
        }
        
        return list;
    }
    
    /**
     * @return children with selector as collection. Default is an empty list
     */
    public EObjectProxyCollection<? extends EObjectProxy> children(String selector) {
        return children().filter(selector);
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
    
    public EObjectProxyCollection<? extends EObjectProxy> parent(String selector) {
    	return parent().filter(selector);
    }
    
    
    /**
     * Return the list of properties' key for the first object in the collection
     * @return
     */
    public Set<String> prop() {
    	return isEmpty() ? null : first().prop();
    }
    
    /**
     * Return a property value of the first object in the collection.
     * If multiple properties exist with the same key, then return only the first one.
     * @param propKey
     * @return
     */
    public String prop(String propKey) {
    	return isEmpty() ? null : first().prop(propKey);
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
    	return isEmpty() ? null : first().prop(propKey, allowDuplicate);
    }
    
    /**
     * Sets a property for every objects.
     * Property is updated if it already exists.
     * @param propKey
     * @param propValue
     * @return
     */
    public EObjectProxyCollection<? extends EObjectProxy> prop(String propKey, String propValue) {
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
    public EObjectProxyCollection<? extends EObjectProxy> prop(String propKey, String propValue, boolean allowDuplicate) {
	    for(EObjectProxy object : this) {
	        object.prop(propKey, propValue, allowDuplicate);
	    }
	    
	    return this;
    }

    /**
     * Remove all instances of property "key" on each object of the collection. Returns the updated collection
     * @param key
     */
    public EObjectProxyCollection<? extends EObjectProxy> removeProp(String key) {
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
    public EObjectProxyCollection<? extends EObjectProxy> removeProp(String key, String value) {
        for(EObjectProxy object : this) {
            object.removeProp(key, value);
        }
        
        return this;
    }

    public Object attr(String attribute) {
    	return isEmpty() ? null : first().attr(attribute);
    }

    public EObjectProxyCollection<? extends EObjectProxy> attr(String attribute, Object value) {
        for(EObjectProxy object : this) {
            object.attr(attribute, value);
        }
        
        return this;
    }

    /**
     * Iterate over a collection, executing a function for each object.
     * The function to execute will receive the current object as first argument.
     * @param action
     * @return
     */
    public EObjectProxyCollection<? extends EObjectProxy> each(Consumer<? super T> action) {
    	this.forEach(action);
    	return this;
    }
    
    /**
     * Create a new jArchi Collection with objects added to the set of matched objects.
     * @param selector
     * @return
     */
    public EObjectProxyCollection<? extends EObjectProxy> add(String selector) {
    	return this.add(this.first().getModel().find(selector));
    }
    
    /**
     * Create a new jArchi Collection with objects added to the set of matched objects.
     * @param collection
     * @return
     */
	public EObjectProxyCollection<? extends EObjectProxy> add(EObjectProxyCollection<? extends EObjectProxy> collection) {
		EObjectProxyCollection<? extends EObjectProxy> list = this.clone();
		list.addAll(collection);
		return list;
    }
	
	@SuppressWarnings("unchecked")
	private void addAll(EObjectProxyCollection<? extends EObjectProxy> collection) {
		super.addAll((Collection<? extends T>) collection);
	}

	/**
	 * Create a copy of the set of matched objects.
	 * Objects themselves are not copied, only the collection is.
	 */
	public EObjectProxyCollection<? extends EObjectProxy> clone() {
		return (EObjectProxyCollection<? extends EObjectProxy>) super.clone();
	}
}
