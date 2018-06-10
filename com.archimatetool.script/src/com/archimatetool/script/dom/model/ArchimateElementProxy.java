/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Collection;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IProperty;

/**
 * Archimate Element wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateElementProxy extends ArchimateConceptProxy {
    
    ArchimateElementProxy(IArchimateElement element) {
        super(element);
    }
    
    @Override
    protected IArchimateElement getEObject() {
        return (IArchimateElement)super.getEObject();
    }
    
    /**
     * Set the type of this element with a new element of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    @Override
    public ArchimateElementProxy setType(String type) {
        if(super.setType(type) == null) {
            return this;
        }
        
        ArchimateElementProxy newElementProxy = getModel().addElement(type, getName());
        
        if(newElementProxy != null) {
            IArchimateElement newElement = newElementProxy.getEObject();
            
            Collection<IProperty> props = EcoreUtil.copyAll(getEObject().getProperties());
            newElement.getProperties().addAll(props);
            
            outRels().attr(SOURCE, newElementProxy);
            inRels().attr(TARGET, newElementProxy);
            objectRefs().attr(ARCHIMATE_CONCEPT, newElementProxy);
            
            delete();
            
            setEObject(newElement);
        }
        
        return this;
    }
}
