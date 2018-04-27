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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * Abstract EObject wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class EObjectProxy implements IModelConstants {
    
    private EObject fEObject;
    
    /**
     * Factory method for correct type of EObjectProxy
     * @param eObject
     * @return EObjectProxy type
     */
    public static EObjectProxy get(EObject eObject) {
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
        
        if(eObject instanceof IFolder) {
            return new FolderProxy((IFolder)eObject);
        }

        return null;
    }
    
    public EObjectProxy() {
    }
    
    public EObjectProxy(EObject eObject) {
        setEObject(eObject);
    }
    
    protected void setEObject(EObject eObject) {
        fEObject = eObject;
    }
    
    protected EObject getEObject() {
        return fEObject;
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
        if(getEObject() != null) {
            return getEObject().eClass().getName();
        }
        
        return null;
    }
    
    /**
     * Add a property to this object
     * @param key
     * @param value
     */
    public EObjectProxy addProperty(String key, String value) {
        if(getEObject() instanceof IProperties && key != null && value != null) {
            // TODO use IArchimateFactory.eINSTANCE.createProperty(key, value);
            IProperty prop = IArchimateFactory.eINSTANCE.createProperty();
            prop.setKey(key);
            prop.setValue(value);
            ((IProperties)getEObject()).getProperties().add(prop);
        }
        
        return this;
    }
    
    /**
     * Add the property only if it doesn't already exists, or update it if it does.
     * If this object already has multiple properties matching the key, all of them are updated.
     * @param key
     * @param value
     */
    public EObjectProxy addOrUpdateProperty(String key, String value) {
        if(getEObject() instanceof IProperties && key != null && value != null) {
            boolean updated = false;
            
            for(IProperty prop : ((IProperties)getEObject()).getProperties()) {
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
     */
    public EObjectProxy updateProperty(String key, String value) {
        if(getEObject() instanceof IProperties && key != null && value != null) {
            for(IProperty prop : ((IProperties)getEObject()).getProperties()) {
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
    public Set<String> getPropertyKey() {
        Set<String> list = new LinkedHashSet<String>();
        
        if(getEObject() instanceof IProperties) {
            for(IProperty p : ((IProperties)getEObject()).getProperties()) {
                list.add(p.getKey());
            }
        }
        
        return list;
    }
    
    /**
     * @param key
     * @return a list containing the value of property named "key"
     */
    public List<String> getPropertyValue(String key) {
        List<String> list = new ArrayList<String>();
        
        if(getEObject() instanceof IProperties) {
            for(IProperty p : ((IProperties)getEObject()).getProperties()) {
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
    public List<Property> getProperties() {
        List<Property> list = new ArrayList<Property>();
        
        if(getEObject() instanceof IProperties) {
            for(IProperty p : ((IProperties)getEObject()).getProperties()) {
                list.add(new Property(p.getKey(), p.getValue()));
            }
        }
        
        return list;
    }
    
    /**
     * Remove all instances of property "key" 
     * @param key
     */
    public EObjectProxy removeProperty(String key) {
        return removeProperty(key, null);
    }
    
    /**
     * Remove (all instances of) property "key" that matches "value"
     * @param key
     */
    public EObjectProxy removeProperty(String key, String value) {
        if(getEObject() instanceof IProperties) {
            List<IProperty> toRemove = new ArrayList<IProperty>();
            EList<IProperty> props = ((IProperties)getEObject()).getProperties();
            
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

    public Object attr(String attribute) {
        switch(attribute) {
            case ID:
                return getEObject() instanceof IIdentifier ? ((IIdentifier)getEObject()).getId() : null;

            case NAME:
                return getEObject() instanceof INameable ? ((INameable)getEObject()).getName() : null;
            
            case DOCUMENTATION:
                return getEObject() instanceof IDocumentable ? ((IDocumentable)getEObject()).getDocumentation() : null;

            default:
                return null;
        }
    }
    
    public EObjectProxy attr(String attribute, Object value) {
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
