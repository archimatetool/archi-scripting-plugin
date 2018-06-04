/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;


/**
 * Extended Collection of EObjectProxy objects
 * 
 * @author Phillip Beauvoir
 */
public class EObjectProxyCollection extends ArrayList<EObjectProxy> implements IModelConstants {
    
	EObjectProxyCollection() {
        super();
    }
    
    public EObjectProxy first() {
        return isEmpty() ? null : get(0);
    }
    
    /**
     * Check the current matched set of object against a selector and
     * return true if at least one of these objects matches the given arguments.
     * @param selector
     * @return
     */
    public boolean is(String selector) {
        return !filter(selector).isEmpty();
    }
    
    /**
     * Delete all in collection
     */
    public EObjectProxyCollection delete() {
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
     * TODO: remove PHIL: Maybe?
     */
    protected EObjectProxyCollection invoke(String methodName, Object... args) {
        for(EObjectProxy object : this) {
            object.invoke(methodName, args);
        }
        
        return this;
    }

    /**
     * @return the set of matched objects
     */
    public EObjectProxyCollection find() {
    	EObjectProxyCollection list = new EObjectProxyCollection();
    	
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
     * @return the set of matched objects
     */
    public EObjectProxyCollection find(String selector) {
        return find().filter(selector);
    }
    
    /**
     * Filter the collection and keep only objects that match the selector
     * @param selector
     * @return a filtered collection
     */
    public EObjectProxyCollection filter(String selector) {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
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
     * Filter the collection and keep only objects that pass the function's test.
     * @param predicate
     * @return
     */
    public EObjectProxyCollection filter(Predicate<EObjectProxy> predicate) {
    	EObjectProxyCollection list = new EObjectProxyCollection();
    	if(predicate == null) {
    		return list;
    	}
    	
    	for(EObjectProxy object : this) {
    		if(predicate.test(object)) {
    			list.add(object);
    		}
    	}
    	
    	return list;
    }
    
    /**
     * Reduce the set of matched elements to those that have a descendant that matches the selector.
     * @param selector
     * @return
     */
    public EObjectProxyCollection has(String selector) {
    	if(selector == null) {
    		return this;
    	}
    	
    	return filter((EObjectProxy object) -> object.find().is(selector));
    }
    
    
    /**
     * Remove elements from the set of matched elements.
     * @param selector
     * @return
     */
    public EObjectProxyCollection not(String selector) {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
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
    public EObjectProxyCollection not(EObjectProxyCollection collection) {
    	removeAll(collection);
    	return this;
    }
    
    
    /**
     * @return children as collection. Default is an empty list
     */
    public EObjectProxyCollection children() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(EObjectProxy object : this) {
            for(EObjectProxy child : object.children()) {
                list.addUnique(child);
            }
        }
        
        return list;
    }
    
    /**
     * @return children with selector as collection. Default is an empty list
     */
    public EObjectProxyCollection children(String selector) {
        return children().filter(selector);
    }
    
    /**
     * @return parent
     */
    public EObjectProxyCollection parent() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(EObjectProxy object : this) {
            if(object.parent() != null) {
                list.add(object.parent());
            }
        }
        
        return list;
    }
    
    public EObjectProxyCollection parent(String selector) {
    	return parent().filter(selector);
    }
    
    public EObjectProxyCollection parents() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(EObjectProxy object : this) {
        	EObjectProxyCollection parents = object.parents();
            if(parents != null && !parents.isEmpty()) {
                list = list.add(parents);
            }
        }
        
        return list;
    }
    
    /**
     * Return the list of properties' key for the first object in the collection
     * @return
     */
    public List<String> prop() {
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
    public EObjectProxyCollection prop(String propKey, String propValue) {
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
    public EObjectProxyCollection prop(String propKey, String propValue, boolean allowDuplicate) {
	    for(EObjectProxy object : this) {
	        object.prop(propKey, propValue, allowDuplicate);
	    }
	    
	    return this;
    }

    /**
     * Remove all instances of property "key" on each object of the collection. Returns the updated collection
     * @param key
     */
    public EObjectProxyCollection removeProp(String key) {
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
    public EObjectProxyCollection removeProp(String key, String value) {
        for(EObjectProxy object : this) {
            object.removeProp(key, value);
        }
        
        return this;
    }

    public Object attr(String attribute) {
    	return isEmpty() ? null : first().attr(attribute);
    }

    public EObjectProxyCollection attr(String attribute, Object value) {
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
    public EObjectProxyCollection each(Consumer<EObjectProxy> action) {
    	forEach(action);
    	return this;
    }
    
    /**
     * Create a new jArchi Collection with objects added to the set of matched objects.
     * @param selector
     * @return
     */
    public EObjectProxyCollection add(String selector) {
        EObjectProxy first = first();
        
        if(first != null && first.getModel() != null) {
            return add(first.getModel().find(selector));
        }
        
        return this;
    }
    
    /**
     * Create a new jArchi Collection with objects added to the set of matched objects.
     * @param collection
     * @return
     */
	public EObjectProxyCollection add(EObjectProxyCollection collection) {
		if(collection != null) {
	        // Iterate over the collection and filter objects into the list
	        for(EObjectProxy object : collection) {
	            addUnique(object);
	        }
		}
        
        return this;
    }
	
	/**
	 * Get the concepts that are the targets or the sources
	 * of each relationship in the set of matched objects.
	 * @return
	 */
	public EObjectProxyCollection ends() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
		    if(object instanceof ArchimateRelationshipProxy || object instanceof DiagramModelConnectionProxy) {
		        list.addUnique((EObjectProxy)object.attr(SOURCE));
                list.addUnique((EObjectProxy)object.attr(TARGET));
		    }
        }
		
		return list;
	}
	
	/**
	 * Get the concepts that are the sources of each
	 * relationship in the set of matched objects.
	 * @return
	 */
	public EObjectProxyCollection sourceEnds() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
            if(object instanceof ArchimateRelationshipProxy || object instanceof DiagramModelConnectionProxy) {
                list.addUnique((EObjectProxy)object.attr(SOURCE));
            }
        }
		
		return list;
	}
	
	/**
	 * Get the concepts that are the targets of each
	 * relationship in the set of matched objects.
	 * @return
	 */
	public EObjectProxyCollection targetEnds() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
            if(object instanceof ArchimateRelationshipProxy || object instanceof DiagramModelConnectionProxy) {
                list.addUnique((EObjectProxy)object.attr(TARGET));
            }
        }
		
		return list;
	}
	
	/**
	 * Get the concepts that are the targets or the sources of each
	 * relationship in the set of matched objects, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection ends(String selector) {
		return ends().filter(selector);
	}
	
	/**
	 * Get the concepts that are the sources of each relationship
	 * in the set of matched objects, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection sourceEnds(String selector) {
		return sourceEnds().filter(selector);
	}
	
	/**
	 * Get the concepts that are the targets of each relationship
	 * in the set of matched objects, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection targetEnds(String selector) {
		return targetEnds().filter(selector);
	}
	
	boolean addUnique(EObjectProxy object) {
	    if(object != null && !contains(object)) {
	        return add(object);
	    }
	    
	    return false;
	}
	
	/**
	 * Get the visual objects that reference each object in collection.
	 * TODO: Should be extended to work with DiagramModel too
	 * @return
	 */
	public EObjectProxyCollection objectRefs() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
            if(object instanceof ArchimateConceptProxy) {
            	// TODO: Decide if we keep attr() in proxy classes (I'd say no: why bother maintaining it?). If not, update following code.
                list.add((EObjectProxyCollection)object.attr(DIAGRAM_COMPONENT_INSTANCES));
            }
        }
		
		return list;
	}
	
	/**
	 * Get the visual objects that reference each object
	 * in collection, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection objectRefs(String selector) {
		return objectRefs().filter(selector);
	}
	
	/**
	 * Get the views in which each object in collection is referenced.
	 * TODO: Should be extended to work with DiagramModel too
	 * @return
	 */
	public EObjectProxyCollection viewRefs() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
            if(object instanceof ArchimateConceptProxy) {
            	// TODO: Decide if we keep attr() in proxy classes (I'd say no: why bother maintaining it?). If yes, update following code.
                list.add(((ArchimateConceptProxy) object).getDiagramInstances());
            }
        }
		
		return list;
	}
	
	/**
	 * Get the views in which each object in collection
	 * is referenced, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection viewRefs(String selector) {
		return viewRefs().filter(selector);
	}
	
	/**
	 * Get the relationships that start or end at
	 * each concept in the set of matched objects.
	 * TODO: Should be extended to work with diagram objects too (and return connections)
	 * @return
	 */
	public EObjectProxyCollection rels() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
		    if(object instanceof ArchimateConceptProxy) {
		        list.add((EObjectProxyCollection)object.attr(TARGET_RELATIONSHIPS));
                list.add((EObjectProxyCollection)object.attr(SOURCE_RELATIONSHIPS));
		    }
        }
		
		return list;
	}
	
	/**
	 * Get the (incoming) relationships that end at
	 * each concept in the set of matched objects.
	 * @return
	 */
	public EObjectProxyCollection inRels() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
            if(object instanceof ArchimateConceptProxy) {
                list.add((EObjectProxyCollection)object.attr(TARGET_RELATIONSHIPS));
            }
        }
		
		return list;
	}
	
	/**
	 * Get the (outgoing) relationships that start at
	 * each concept in the set of matched objects.
	 * @return
	 */
	public EObjectProxyCollection outRels() {
		EObjectProxyCollection list = new EObjectProxyCollection();
		
		for(EObjectProxy object : this) {
            if(object instanceof ArchimateConceptProxy) {
                list.add((EObjectProxyCollection)object.attr(SOURCE_RELATIONSHIPS));
            }
        }
		
		return list;
	}
	
	/**
	 * Get the relationships that start or end at each concept
	 * in the set of matched objects, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection rels(String selector) {
		return rels().filter(selector);
	}
	
	/**
	 * Get the (incoming) relationships that end at each concept
	 * in the set of matched objects, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection inRels(String selector) {
		return inRels().filter(selector);
	}
	
	/**
	 * Get the (outgoing) relationships that start at each concept
	 * in the set of matched objects, filtered by a selector.
	 * @param selector
	 * @return
	 */
	public EObjectProxyCollection outRels(String selector) {
		return outRels().filter(selector);
	}

}
