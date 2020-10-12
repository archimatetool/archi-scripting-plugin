/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IDiagramModelReference;

/**
 * Diagram Model View Reference wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelReferenceProxy extends DiagramModelObjectProxy {
    
    DiagramModelReferenceProxy(IDiagramModelReference object) {
        super(object);
    }
    
    @Override
    protected IDiagramModelReference getEObject() {
        return (IDiagramModelReference)super.getEObject();
    }
    
    public DiagramModelProxy getRefView() {
        return (DiagramModelProxy)EObjectProxy.get(getEObject().getReferencedModel());
    }
    
    @Override
    protected EObject getReferencedConcept() {
        return getEObject().getReferencedModel();
    }

}
