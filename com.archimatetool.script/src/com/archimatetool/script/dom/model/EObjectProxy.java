/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateModel;
import com.archimatetool.script.ArchiScriptException;

/**
 * Abstract EObject wrapper proxy
 * 
 * @author Phillip Beauvoir
 * @author jbsarrodie
 */
public abstract class EObjectProxy implements IModelConstants {
    
    private EObject fEObject;
    
    /**
     * Factory method for correct type of EObjectProxy
     * @param eObject
     * @return EObjectProxy type or null if not found
     */
    static EObjectProxy get(EObject eObject) {
        if(eObject instanceof IArchimateModel) {
            return new ArchimateModelProxy((IArchimateModel)eObject);
        }
        
        if(eObject instanceof IArchimateElement) {
            return new ArchimateElementProxy((IArchimateElement)eObject);
        }
        
        if(eObject instanceof IArchimateRelationship) {
            return new ArchimateRelationshipProxy((IArchimateRelationship)eObject);
        }
        
        if(eObject instanceof IDiagramModel) {
            return new DiagramModelProxy((IDiagramModel)eObject);
        }
        
        if(eObject instanceof IDiagramModelObject) {
            return new DiagramModelObjectProxy((IDiagramModelObject)eObject);
        }
        
        if(eObject instanceof IDiagramModelConnection) {
            return new DiagramModelConnectionProxy((IDiagramModelConnection)eObject);
        }

        if(eObject instanceof IFolder) {
            return new FolderProxy((IFolder)eObject);
        }

        return null;
    }
    
    EObjectProxy(EObject eObject) {
        setEObject(eObject);
    }
    
    protected void setEObject(EObject eObject) {
        fEObject = eObject;
    }
    
    protected EObject getEObject() {
        return fEObject;
    }
    
    /**
     * @return The (possibly) referenced eObject underlying this eObject
     * sub-classes can over-ride and return the underlying eObject
     */
    protected EObject getReferencedConcept() {
        return getEObject();
    }
    
    public boolean isSet() {
        return getEObject() != null;
    }
    
    public boolean isConcept() {
        return false;
    }
    
    public boolean isElement() {
        return false;
    }
    
    public boolean isRelationship() {
        return false;
    }
    
    public boolean isFolder() {
        return false;
    }
    
    public boolean isView() {
        return false;
    }
    
    public boolean isCanvas() {
        return false;
    }
    
    public boolean isSketch() {
        return false;
    }
    
    public ArchimateModelProxy getModel() {
        if(getEObject() instanceof IArchimateModelObject) {
            return new ArchimateModelProxy(((IArchimateModelObject)getEObject()).getArchimateModel());
        }
        
        return null;
    }
    
    public String getId() {
        return (String)attr(ID);
    }

    public String getName() {
        return (String)attr(NAME);
    }
    
    public EObjectProxy setName(String name) {
        return attr(NAME, name);
    }
    
    public String getDocumentation() {
        return (String)attr(DOCUMENTATION);
    }
    
    public EObjectProxy setDocumentation(String documentation) {
        return attr(DOCUMENTATION, documentation);
    }
    
    /**
     * @return class type of this object
     */
    public String getType() {
        if(getReferencedConcept() != null) {
            return getReferencedConcept().eClass().getName();
        }
        
        return null;
    }
    
    /**
     * Delete this object.
     * Sub-classes to implement this
     */
    public void delete() {
        throw new ArchiScriptException(NLS.bind(Messages.EObjectProxy_0, this));
    }
    
    /**
     * Invoke a function (method) with parameters
     * @param methodName
     * @param args
     * @return
     * TODO: remove - PHIL: Maybe! This might be handy?
     */
    protected Object invoke(String methodName, Object... args) {
        switch(methodName) {
            case "delete": //$NON-NLS-1$
                delete();
                break;

            default:
                break;
        }
        
        return this;
    }
    
    /**
     * @return the descendants of each object in the set of matched objects
     * TODO: Should be marked as protected but this might block jArchi() and $() 
     * PHIL: This *will* break jArchi() and $() !!!!
     */
    public EObjectProxyCollection find() {
    	EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() != null) {
            // Iterate over all model contents and put all objects into the list
            for(Iterator<EObject> iter = getEObject().eAllContents(); iter.hasNext();) {
                EObject eObject = iter.next();
                EObjectProxy proxy = EObjectProxy.get(eObject);
                if(proxy != null) {
                    list.add(proxy);
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
     * TODO: See if really needed
     * @param eObject
     * @return
     */
    public EObjectProxyCollection find(EObject eObject) {
    	EObjectProxyCollection list = new EObjectProxyCollection();
    	
    	EObjectProxy proxy = EObjectProxy.get(eObject);
    	if(proxy != null) {
            list.add(proxy);
    	}
    	
    	return list;
    }
    
    /**
     * TODO: See if really needed
     * @param object
     * @return
     */
    public EObjectProxyCollection find(EObjectProxy object) {
    	EObjectProxyCollection list = new EObjectProxyCollection();
    	
    	if(object != null) {
    	    list.add(object);
    	}
    	
    	return list;
    }
    
    /**
     * @return children as collection. Default is an empty list
     */
    protected EObjectProxyCollection children() {
        return new EObjectProxyCollection();
    }
    
    /**
     * @return children with selector as collection.
     * TODO: remove
     */
    private EObjectProxyCollection children(String selector) {
        return children().filter(selector);
    }
    
    /**
     * @return parent of this object. Default is the eContainer
     * TODO: PHIL: Why protected?
     */
    protected EObjectProxy parent() {
        return getEObject() == null ? null : EObjectProxy.get(getEObject().eContainer());
	}
    
    protected EObjectProxyCollection parents() {
    	if(parent() == null || parent().getEObject() instanceof ArchimateModel) {
    		return null;
    	} else {
    		EObjectProxyCollection list = new EObjectProxyCollection();
    		list.add(parent());
    		return list.add(list.parents());
    	}
    }

	/**
     * Return the list of properties' key
     * @return
     */
    public Set<String> prop() {
    	return getPropertyKey();
    }
    
    /**
     * Return a property value.
     * If multiple properties exist with the same key, then return only the first one.
     * @param propKey
     * @return
     */
    public String prop(String propKey) {
    	return (String)prop(propKey, false);
    }
    
    /**
     * Return a property value.
     * If multiple properties exist with the same key, then return only
     * the first one (if duplicate=false) or an array with all values
     * (if duplicate=true).
     * @param propKey
     * @param allowDuplicate
     * @return
     */
    public Object prop(String propKey, boolean allowDuplicate) {
    	List<String> propValues = getPropertyValue(propKey);
    	
    	if(propValues.isEmpty()) {
            return null;
    	}
    	else if(allowDuplicate) {
    		return propValues;
    	}
    	else {
    		return propValues.get(0);
    	}
    }
    
    /**
     * Sets a property.
     * Property is updated if it already exists.
     * @param propKey
     * @param propValue
     * @return
     */
    public EObjectProxy prop(String propKey, String propValue) {
    	return prop(propKey, propValue, false);
    }
    
    /**
     * Sets a property.
     * Property is updated if it already exists (if duplicate=false)
     * or added anyway (if duplicate=true).
     * @param propKey
     * @param propValue
     * @param allowDuplicate
     * @return
     */
    public EObjectProxy prop(String propKey, String propValue, boolean allowDuplicate) {
    	return allowDuplicate ? addProperty(propKey, propValue) : addOrUpdateProperty(propKey, propValue);
    }
    
    /**
     * Add a property to this object
     * @param key
     * @param value
     */
    private EObjectProxy addProperty(String key, String value) {
        checkModelAccess();
        
        if(getReferencedConcept() instanceof IProperties && key != null && value != null) {
            // TODO use IArchimateFactory.eINSTANCE.createProperty(key, value);
            IProperty prop = IArchimateFactory.eINSTANCE.createProperty();
            prop.setKey(key);
            prop.setValue(value);
            ((IProperties)getReferencedConcept()).getProperties().add(prop);
        }
        
        return this;
    }
    
    /**
     * Add the property only if it doesn't already exists, or update it if it does.
     * If this object already has multiple properties matching the key, all of them are updated.
     * @param key
     * @param value
     */
    private EObjectProxy addOrUpdateProperty(String key, String value) {
        checkModelAccess();
        
        if(getReferencedConcept() instanceof IProperties && key != null && value != null) {
            boolean updated = false;
            
            for(IProperty prop : ((IProperties)getReferencedConcept()).getProperties()) {
                if(prop.getKey().equals(key)) {
                    prop.setValue(value);
                    updated = true;
                }
            }
            
            if(!updated) {
                addProperty(key, value);
            }
        }
        
        return this;
    }

    /**
     * Update the property with new value
     * If this object already has multiple properties matching the key, all of them are updated.
     * @param key
     * @param value
     * @return this
     * TODO: remove
     */
    private EObjectProxy updateProperty(String key, String value) {
        checkModelAccess();
        
        if(getReferencedConcept() instanceof IProperties && key != null && value != null) {
            for(IProperty prop : ((IProperties)getReferencedConcept()).getProperties()) {
                if(prop.getKey().equals(key)) {
                    prop.setValue(value);
                }
            }
        }
        
        return this;
    }

    /**
     * @return an array of strings containing the list of properties keys. A key appears only once (duplicates are removed)
     */
    private Set<String> getPropertyKey() {
        Set<String> list = new LinkedHashSet<String>();
        
        if(getReferencedConcept() instanceof IProperties) {
            for(IProperty p : ((IProperties)getReferencedConcept()).getProperties()) {
                list.add(p.getKey());
            }
        }
        
        return list;
    }
    
    /**
     * @param key
     * @return a list containing the value of property named "key"
     */
    private List<String> getPropertyValue(String key) {
        List<String> list = new ArrayList<String>();
        
        if(getReferencedConcept() instanceof IProperties) {
            for(IProperty p : ((IProperties)getReferencedConcept()).getProperties()) {
                if(p.getKey().equals(key)) {
                    list.add(p.getValue());
                }
            }
        }
        
        return list;
    }
    
    /**
     * @return All Property pairs
     */
    protected List<Property> getProperties() {
        List<Property> list = new ArrayList<Property>();
        
        if(getReferencedConcept() instanceof IProperties) {
            for(IProperty p : ((IProperties)getReferencedConcept()).getProperties()) {
                list.add(new Property(p.getKey(), p.getValue()));
            }
        }
        
        return list;
    }
    
    /**
     * Replace the properties list 
     * @param properties
     */
    protected void setProperties(List<Property> properties) {
        if(getReferencedConcept() instanceof IProperties) {
            // clear
            ((IProperties)getReferencedConcept()).getProperties().clear();
            
            // add new ones
            properties.forEach(p -> {
                addProperty(p.getKey(), p.getValue());
            });
        }
    }
    
    /**
     * Remove all instances of property "key" 
     * @param key
     */
    public EObjectProxy removeProp(String key) {
        return removeProp(key, null);
    }
    
    /**
     * Remove (all instances of) property "key" that matches "value"
     * @param key
     */
    public EObjectProxy removeProp(String key, String value) {
        checkModelAccess();
        
        if(getReferencedConcept() instanceof IProperties) {
            List<IProperty> toRemove = new ArrayList<IProperty>();
            EList<IProperty> props = ((IProperties)getReferencedConcept()).getProperties();
            
            for(IProperty p : props) {
                if(p.getKey().equals(key)) {
                    if(value == null) {
                        toRemove.add(p);
                    }
                    else if(p.getValue().equals(value)) {
                        toRemove.add(p);
                    }
                }
            }
            
            props.removeAll(toRemove);
        }
        
        return this;
    }

    protected Object attr(String attribute) {
        switch(attribute) {
            case TYPE:
                return getType();

            case ID:
                return getEObject() instanceof IIdentifier ? ((IIdentifier)getEObject()).getId() : null;

            case NAME:
                return getEObject() instanceof INameable ? ((INameable)getEObject()).getName() : null;
            
            case DOCUMENTATION:
                return getEObject() instanceof IDocumentable ? ((IDocumentable)getEObject()).getDocumentation() : null;
                
            case CHILDREN:
                return children();

            default:
                return null;
        }
    }
    
    protected EObjectProxy attr(String attribute, Object value) {
        checkModelAccess();
        
        switch(attribute) {
            case NAME:
                if(getEObject() instanceof INameable) {
                    ((INameable)getEObject()).setName((String)value);
                }
                break;
            
            case DOCUMENTATION:
                if(getEObject() instanceof IDocumentable) {
                    ((IDocumentable)getEObject()).setDocumentation((String)value);
                }
                break;
        }
        
        return this;
    }
    
    /**
     * Check model access
     */
    protected void checkModelAccess() {
        ModelHandler.checkModelAccess(getEObject());
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof EObjectProxy)) {
            return false;
        }
        
        if(getEObject() == null) {
            return false;
        }
        
        return getEObject() == ((EObjectProxy)obj).getEObject();
    }
    
    @Override
    public String toString() {
        return getType() + ": " + getName(); //$NON-NLS-1$
    }
}
