/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.osgi.util.NLS;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.editor.model.commands.SetProfileCommand;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IProfile;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DeleteFolderObjectCommand;
import com.archimatetool.script.commands.ScriptCommandWrapper;

/**
 * Archimate Concept wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class ArchimateConceptProxy extends EObjectProxy {
    
    ArchimateConceptProxy(IArchimateConcept concept) {
        super(concept);
    }
    
    @Override
    protected IArchimateConcept getEObject() {
        return (IArchimateConcept)super.getEObject();
    }
    
    // Return this
    public ArchimateConceptProxy getConcept() {
        return this;
    }
    
    /**
     * Set the type of this concept with a new concept of class type, preserving all connecting relationships and diagram components
     * Sub-classes call this first.
     * @param type the Archimate type to replace with
     * @return
     */
    protected ArchimateConceptProxy setType(String type) {
        if(!StringUtils.isSet(type)) {
            return null;
        }
        
        // Check it's not already this type
        String className = ModelUtil.getCamelCase(type);
        if(getEObject().eClass().getName().equals(className)) {
            return null;
        }
        
        if(!ModelUtil.isAllowedSetType(getEObject(), type)) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateConceptProxy_1, type));
        }
        
        return this;
    }
    
    protected EObjectProxyCollection outRels() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        for(IArchimateRelationship r : getEObject().getSourceRelationships()) {
            list.add(new ArchimateRelationshipProxy(r));
        }
        return list;
    }
    
    protected EObjectProxyCollection inRels() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        for(IArchimateRelationship r : getEObject().getTargetRelationships()) {
            list.add(new ArchimateRelationshipProxy(r));
        }
        return list;
    }
    
    protected EObjectProxyCollection objectRefs() {
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
    
    protected EObjectProxyCollection viewRefs() {
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
    
    public String getSpecialization() {
        IProfile profile = getEObject().getPrimaryProfile();
        return profile != null ? profile.getName() : null;
    }
    
    public ArchimateConceptProxy setSpecialization(String name) {
        if(getArchimateModel() == null) {
            throw new ArchiScriptException(Messages.ArchimateConceptProxy_2);
        }
        
        // Name can be null but not an empty string
        if(name != null && "".equals(name.trim())) { //$NON-NLS-1$
            throw new ArchiScriptException(Messages.ArchimateConceptProxy_3);
        }
        
        IProfile profile = null;
        
        // If name is not null we are setting it to a profile, else unsetting it
        if(name != null) {
            // Get the Profile
            profile = ArchimateModelUtils.getProfileByNameAndType(getArchimateModel(), name, getEObject().eClass().getName());
            
            // We don't have it
            if(profile == null) {
                throw new ArchiScriptException(NLS.bind(Messages.ArchimateConceptProxy_4, name, getEObject().eClass().getName()));
            }
        }
        
        // Set it
        CommandHandler.executeCommand(new ScriptCommandWrapper(new SetProfileCommand(getEObject(), profile), getEObject()));
        
        return this;
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case SPECIALIZATION:
                return getSpecialization();
        }
        
        return super.attr(attribute);
    }

    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case SPECIALIZATION:
                if(value instanceof String val) {
                    return setSpecialization(val);
                }
        }
        
        return super.attr(attribute, value);
    }


    interface Internal extends IReferencedProxy, IConnectableProxy {}
    
    @Override
    protected Object getInternal() {
        return new Internal() {
            @Override
            public EObjectProxyCollection outRels() {
                return ArchimateConceptProxy.this.outRels();
            }
            
            @Override
            public EObjectProxyCollection inRels() {
                return ArchimateConceptProxy.this.inRels();
            }
            
            @Override
            public EObjectProxyCollection viewRefs() {
                return ArchimateConceptProxy.this.viewRefs();
            }
            
            @Override
            public EObjectProxyCollection objectRefs() {
                return ArchimateConceptProxy.this.objectRefs();
            }
        };
    }

}
