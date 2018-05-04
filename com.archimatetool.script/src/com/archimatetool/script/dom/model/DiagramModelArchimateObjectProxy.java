/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IDiagramModelArchimateObject;

/**
 * Diagram Model Archimate Concept wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelArchimateObjectProxy extends DiagramModelArchimateComponentProxy {
    
    public DiagramModelArchimateObjectProxy() {
    }
    
    public DiagramModelArchimateObjectProxy(IDiagramModelArchimateObject object) {
        super(object);
    }
    
    @Override
    public ArchimateElementProxy getArchimateConcept() {
        return (ArchimateElementProxy)super.getEObject();
    }
    
    @Override
    protected IDiagramModelArchimateObject getEObject() {
        return (IDiagramModelArchimateObject)super.getEObject();
    }
}
