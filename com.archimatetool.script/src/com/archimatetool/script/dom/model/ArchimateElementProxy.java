/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimateElement;

/**
 * Archimate Element wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateElementProxy extends ArchimateConceptProxy {
    
    public ArchimateElementProxy(IArchimateElement element) {
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
     * Replace this element with a new element of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    @Override
    public ArchimateElementProxy replace(String type) {
        ArchimateElementProxy newElement = getModel().addElement(type, getName());
        
        if(newElement != null) {
            newElement.setProperties(getProperties());
            
            getSourceRelationships().attr(SOURCE, newElement);
            getTargetRelationships().attr(TARGET, newElement);
            getDiagramComponentInstances().attr(ARCHIMATE_CONCEPT, newElement);
            
            delete();
        }
        
        return newElement;
    }
}
