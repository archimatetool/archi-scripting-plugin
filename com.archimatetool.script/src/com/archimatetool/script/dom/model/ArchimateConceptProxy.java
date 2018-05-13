/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IFolder;

/**
 * Archimate Concept wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class ArchimateConceptProxy extends EObjectProxy {
    
    public ArchimateConceptProxy() {
    }
    
    public ArchimateConceptProxy(IArchimateConcept concept) {
        super(concept);
    }
    
    @Override
    protected IArchimateConcept getEObject() {
        return (IArchimateConcept)super.getEObject();
    }
    
    @Override
    public boolean isConcept() {
        return true;
    }

    @Override
    public Object invoke(String methodName, Object... args) {
        switch(methodName) {
            case "replace": //$NON-NLS-1$
                if(args.length == 1 && args[0] instanceof String) {
                    return replace((String)args[0]);
                }
                break;

            default:
                break;
        }
        
        return super.invoke(methodName, args);
    }

    @Override
    public void delete() {
        checkModelAccess();
        
        if(getSourceRelationships().isEmpty() && getTargetRelationships().isEmpty() && getDiagramComponentInstances().isEmpty()) {
            ((IFolder)getEObject().eContainer()).getElements().remove(getEObject());
        }
        else {
            throw new ArchiScriptException(Messages.ArchimateConceptProxy_0 + " " + this); //$NON-NLS-1$
        }
    }
    
    /**
     * Replace this concept with a new concept of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    public abstract ArchimateConceptProxy replace(String type);
    
    public EObjectProxyCollection<ArchimateRelationshipProxy> getSourceRelationships() {
        EObjectProxyCollection<ArchimateRelationshipProxy> list = new EObjectProxyCollection<ArchimateRelationshipProxy>();
        for(IArchimateRelationship r : getEObject().getSourceRelationships()) {
            list.add(new ArchimateRelationshipProxy(r));
        }
        return list;
    }
    
    public EObjectProxyCollection<ArchimateRelationshipProxy> getTargetRelationships() {
        EObjectProxyCollection<ArchimateRelationshipProxy> list = new EObjectProxyCollection<ArchimateRelationshipProxy>();
        for(IArchimateRelationship r : getEObject().getTargetRelationships()) {
            list.add(new ArchimateRelationshipProxy(r));
        }
        return list;
    }
    
    public EObjectProxyCollection<DiagramModelComponentProxy> getDiagramComponentInstances() {
        EObjectProxyCollection<DiagramModelComponentProxy> list = new EObjectProxyCollection<DiagramModelComponentProxy>();
        
        for(IDiagramModel dm : getEObject().getArchimateModel().getDiagramModels()) {
            for(IDiagramModelArchimateComponent dmc : DiagramModelUtils.findDiagramModelComponentsForArchimateConcept(dm, getEObject())) {
                list.add((DiagramModelComponentProxy)EObjectProxy.get(dmc));
            }
        }
        
        return list;
    }
    
    @Override
    public Object attr(String attribute) {
        switch(attribute) {
            case SOURCE_RELATIONSHIPS:
                return getSourceRelationships();
            case TARGET_RELATIONSHIPS:
                return getTargetRelationships();
            case DIAGRAM_COMPONENT_INSTANCES:
                return getDiagramComponentInstances();
        }
        
        return super.attr(attribute);
    }

}
