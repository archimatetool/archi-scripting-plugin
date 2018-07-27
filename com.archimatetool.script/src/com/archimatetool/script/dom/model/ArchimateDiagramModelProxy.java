/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.script.ArchiScriptException;

/**
 * ArchimateDiagramModelProxy wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateDiagramModelProxy extends DiagramModelProxy {
    
    ArchimateDiagramModelProxy(IArchimateDiagramModel dm) {
        super(dm);
    }
    
    /**
     * Add an Archimate element to an ArchiMate View and return the diagram object
     */
    public DiagramModelObjectProxy add(ArchimateElementProxy elementProxy, int x, int y, int width, int height) {
        return ModelUtil.addArchimateDiagramObject(getEObject(), elementProxy.getEObject(), x, y, width, height);
    }
    
    /**
     * Add an Archimate connection to ArchiMate objects and return thr diagram connection
     */
    public DiagramModelConnectionProxy add(ArchimateRelationshipProxy relation, DiagramModelComponentProxy source, DiagramModelComponentProxy target) {
        if(!source.isArchimateConcept() || !target.isArchimateConcept()) {
            throw new ArchiScriptException(Messages.DiagramModelProxy_0);
        }
        
        return ModelUtil.addArchimateDiagramConnection(relation.getEObject(), (IDiagramModelArchimateComponent)source.getEObject(),
                (IDiagramModelArchimateComponent)target.getEObject());
    }

    @Override
    protected IArchimateDiagramModel getEObject() {
        return (IArchimateDiagramModel)super.getEObject();
    }
    
}
