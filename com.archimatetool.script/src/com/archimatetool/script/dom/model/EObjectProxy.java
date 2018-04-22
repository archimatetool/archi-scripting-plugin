/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

/**
 * EObject wrapper proxy thing
 * 
 * @author Phillip Beauvoir
 */
public class EObjectProxy implements IModelConstants {
    
    /**
     * White list of attributes that can be read
     */
    static String[] ATTRIBUTE_READ_WHITELIST = {
        "id", //$NON-NLS-1$
        "name", //$NON-NLS-1$
        "documentation", //$NON-NLS-1$
        "purpose", //$NON-NLS-1$
    };
    
    /**
     * White list of attributes that can be written
     */
    static String[] ATTRIBUTE_WRITE_WHITELIST = {
        "name", //$NON-NLS-1$
        "documentation", //$NON-NLS-1$
        "purpose", //$NON-NLS-1$
    };

    // Keep a cache of PropertyDescriptors mapped to class of EObject for efficency
    private static Map<Class<? extends EObject>, Map<String, PropertyDescriptor>> classTable =
            new HashMap<Class<? extends EObject>, Map<String, PropertyDescriptor>>();
    
    private EObject fEObject;
    
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
    
    public void setName(String name) {
        attr(NAME, name);
    }
    
    public String getDocumentation() {
        return (String)attr(DOCUMENTATION);
    }
    
    public void setDocumentation(String documentation) {
        attr(DOCUMENTATION, documentation);
    }
    
    public Object attr(String attribute) {
        if(getEObject() == null || !canWriteAttribute(attribute)) {
            return null;
        }
        
        PropertyDescriptor desc = getPropertyDescriptorForAttribute(attribute);
        try {
            if(desc != null) {
                return desc.getReadMethod().invoke(getEObject());
            }
        }
        catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    public void attr(String attribute, Object value) {
        if(getEObject() == null || !canWriteAttribute(attribute)) {
            return;
        }
        
        PropertyDescriptor desc = getPropertyDescriptorForAttribute(attribute);
        try {
            if(desc != null) {
                desc.getWriteMethod().invoke(getEObject(), value);
            }
        }
        catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param attribute
     * @return A PropertyDescriptor for an attribute
     *         These are cached in a lookup table for speed
     */
    protected PropertyDescriptor getPropertyDescriptorForAttribute(String attribute) {
        Map<String, PropertyDescriptor> propertyDescriptorTable = classTable.get(getEObject().getClass());
        
        // Initialise with all getter and setter attributes for this class
        if(propertyDescriptorTable == null) {
            propertyDescriptorTable = new HashMap<String, PropertyDescriptor>();
            
            try {
                for(PropertyDescriptor desc : Introspector.getBeanInfo(getEObject().getClass()).getPropertyDescriptors()) {
                    propertyDescriptorTable.put(desc.getName(), desc);
                }
            }
            catch(IllegalArgumentException | IntrospectionException ex) {
                ex.printStackTrace();
            }
            
            classTable.put(getEObject().getClass(), propertyDescriptorTable);
        }
        
        return propertyDescriptorTable.get(attribute);
    }
    
    /**
     * @return true if attribute is writeable
     */
    protected boolean canWriteAttribute(String attribute) {
        for(String attr : ATTRIBUTE_WRITE_WHITELIST) {
            if(attr.equals(attribute)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * @return true if attribute is readable
     */
    protected boolean canReadAttribute(String attribute) {
        for(String attr : ATTRIBUTE_READ_WHITELIST) {
            if(attr.equals(attribute)) {
                return true;
            }
        }
        
        return false;
    }
}
