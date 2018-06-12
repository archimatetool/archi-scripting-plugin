/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DeleteFolderObjectCommand;

/**
 * Archimate Concept wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class ArchimateConceptProxy extends EObjectProxy implements IReferencedProxy, IConnectableProxy {
    
    ArchimateConceptProxy(IArchimateConcept concept) {
        super(concept);
    }
    
    @Override
    protected IArchimateConcept getEObject() {
        return (IArchimateConcept)super.getEObject();
    }
    
    /**
     * Set the type of this concept with a new concept of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    public ArchimateConceptProxy setType(String type) {
        if(!StringUtils.isSet(type)) {
            return null;
        }
        
        // Check it's not already this type
        String className = getCamelCase(type);
        if(getEObject().eClass().getName().equals(className)) {
            return null;
        }
        
        return this;
    }
    
    @Override
    public EObjectProxyCollection outRels() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        for(IArchimateRelationship r : getEObject().getSourceRelationships()) {
            list.add(new ArchimateRelationshipProxy(r));
        }
        return list;
    }
    
    @Override
    public EObjectProxyCollection inRels() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        for(IArchimateRelationship r : getEObject().getTargetRelationships()) {
            list.add(new ArchimateRelationshipProxy(r));
        }
        return list;
    }
    
    @Override
    public EObjectProxyCollection objectRefs() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject().getArchimateModel() != null) {
            for(IDiagramModel dm : getEObject().getArchimateModel().getDiagramModels()) {
                for(IDiagramModelArchimateComponent dmc : DiagramModelUtils.findDiagramModelComponentsForArchimateConcept(dm, getEObject())) {
                    list.add(EObjectProxy.get(dmc));
                }
            }
        }
        
        return list;
    }
    
    @Override
    public EObjectProxyCollection viewRefs() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(IDiagramModel dm : DiagramModelUtils.findReferencedDiagramsForArchimateConcept(getEObject())) {
        	list.add(EObjectProxy.get(dm));
        }
        
        return list;
    }

    @Override
    public void delete() {
        // Delete diagram instances first
        for(EObjectProxy proxy : objectRefs()) {
            proxy.delete();
        }
       
        // Delete all connecting relationships
        for(EObjectProxy proxy : inRels()) {
            proxy.delete();
        }
        
        for(EObjectProxy proxy : outRels()) {
            proxy.delete();
        }

        if(getEObject().getArchimateModel() != null) {
            CommandHandler.executeCommand(new DeleteFolderObjectCommand(getEObject()));
        }
      
    }

}
