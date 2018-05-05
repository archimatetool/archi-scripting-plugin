/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IDiagramModelArchimateComponent;

/**
 * Diagram Model Archimate Concept wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class DiagramModelArchimateComponentProxy extends EObjectProxy {
    
    public DiagramModelArchimateComponentProxy() {
    }
    
    public DiagramModelArchimateComponentProxy(IDiagramModelArchimateComponent component) {
        super(component);
    }
    
    public DiagramModelArchimateComponentProxy setArchimateConcept(ArchimateConceptProxy concept) {
        checkModelAccess();
        getEObject().setArchimateConcept(concept.getEObject());
        return this;
    }
    
    public ArchimateConceptProxy getArchimateConcept() {
        return (ArchimateConceptProxy)EObjectProxy.get(getEObject().getArchimateConcept());
    }
    
    public DiagramModelProxy getDiagramModel() {
        return new DiagramModelProxy(getEObject().getDiagramModel());
    }
    
    @Override
    protected IDiagramModelArchimateComponent getEObject() {
        return (IDiagramModelArchimateComponent)super.getEObject();
    }
    
    @Override
    public Object attr(String attribute) {
        switch(attribute) {
            case ARCHIMATE_CONCEPT:
                return getArchimateConcept();
            case DIAGRAM_MODEL:
                return getDiagramModel();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    public EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case ARCHIMATE_CONCEPT:
                if(value instanceof ArchimateConceptProxy) {
                    return setArchimateConcept((ArchimateConceptProxy)value);
                }
        }
        
        return super.attr(attribute, value);
    }

}
