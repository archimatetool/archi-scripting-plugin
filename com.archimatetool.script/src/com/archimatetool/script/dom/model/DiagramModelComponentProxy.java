/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IConnectable;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IFontAttribute;
import com.archimatetool.model.ILineObject;

/**
 * Diagram Model Component wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class DiagramModelComponentProxy extends EObjectProxy {
    
    DiagramModelComponentProxy(IDiagramModelComponent component) {
        super(component);
    }
    
    @Override
    protected IDiagramModelComponent getEObject() {
        return (IDiagramModelComponent)super.getEObject();
    }
    
    public DiagramModelProxy getDiagramModel() {
        return new DiagramModelProxy(getEObject().getDiagramModel());
    }
    
    /**
     * Set this diagram component to ArchimateConceptProxy if this is an ArchiMate type, otherwise does nothing
     * @param concept
     * @return
     */
    public DiagramModelComponentProxy setArchimateConcept(ArchimateConceptProxy concept) {
        if(isArchimateConcept()) {
            checkModelAccess();
            ((IDiagramModelArchimateComponent)getEObject()).setArchimateConcept(concept.getEObject());
        }
        
        return this;
    }
    
    /**
     * @return The ArchiMate component that this diagram component references or null if it does not reference one
     */
    public ArchimateConceptProxy getArchimateConcept() {
        return isArchimateConcept() ? (ArchimateConceptProxy)EObjectProxy.get(((IDiagramModelArchimateComponent)getEObject()).getArchimateConcept()) : null;
    }
    
    /**
     * @return true if this diagram component references an ArchiMate component
     */
    public boolean isArchimateConcept() {
        return getEObject() instanceof IDiagramModelArchimateComponent;
    }
    
    @Override
    protected EObject getReferencedConcept() {
        if(isArchimateConcept()) {
            return ((IDiagramModelArchimateComponent)getEObject()).getArchimateConcept();
        }
        
        return super.getReferencedConcept();
    }

    /**
     * @return a list of source connections (if any)
     */
    public EObjectProxyCollection getSourceConnections() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IConnectable) {
            for(IDiagramModelConnection dmc : ((IConnectable)getEObject()).getSourceConnections()) {
                list.add(new DiagramModelConnectionProxy(dmc));
            }
        }
        
        return list;
    }
    
    /**
     * @return a list of target connections (if any)
     */
    public EObjectProxyCollection getTargetConnections() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IConnectable) {
            for(IDiagramModelConnection dmc : ((IConnectable)getEObject()).getTargetConnections()) {
                list.add(new DiagramModelConnectionProxy(dmc));
            }
        }
        
        return list;
    }

    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case DIAGRAM_MODEL:
                return getDiagramModel();
            case ARCHIMATE_CONCEPT:
                return getArchimateConcept();
            case FONT_COLOR:
                return ((IFontAttribute)getEObject()).getFontColor();
            case FONT:
                return ((IFontAttribute)getEObject()).getFont();
            case LINE_COLOR:
                return ((ILineObject)getEObject()).getLineColor();
            case LINE_WIDTH:
                return ((ILineObject)getEObject()).getLineWidth();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case ARCHIMATE_CONCEPT:
                if(value instanceof ArchimateConceptProxy) {
                    return setArchimateConcept((ArchimateConceptProxy)value);
                }
        }
        
        return super.attr(attribute, value);
    }

}
