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
    
    @Override
    public boolean isElement() {
        return true;
    }
    
    /**
     * Set the type of this element with a new element of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    @Override
    public ArchimateElementProxy setType(String type) {
        checkModelAccess();
        
        ArchimateElementProxy newElementProxy = getModel().addElement(type, getName());
        
        if(newElementProxy != null) {
            Collection<IProperty> props = EcoreUtil.copyAll(getEObject().getProperties());
            newElementProxy.getEObject().getProperties().addAll(props);
            
            getSourceRelationships().attr(SOURCE, newElementProxy);
            getTargetRelationships().attr(TARGET, newElementProxy);
            getDiagramComponentInstances().attr(ARCHIMATE_CONCEPT, newElementProxy);
            
            delete();
        }
        
        return newElementProxy;
    }
}
