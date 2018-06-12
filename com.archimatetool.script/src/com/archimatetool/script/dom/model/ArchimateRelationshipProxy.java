/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Collection;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.util.NLS;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DisconnectRelationshipCommand;
import com.archimatetool.script.commands.ScriptCommand;

/**
 * Archimate Relationship wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateRelationshipProxy extends ArchimateConceptProxy implements IRelationshipProxy {
    
    ArchimateRelationshipProxy(IArchimateRelationship relationship) {
        super(relationship);
    }
    
    @Override
    protected IArchimateRelationship getEObject() {
        return (IArchimateRelationship)super.getEObject();
    }

    @Override
    public ArchimateConceptProxy getSource() {
        return (ArchimateConceptProxy)EObjectProxy.get(getEObject().getSource());
    }
    
    @Override
    public ArchimateConceptProxy getTarget() {
        return (ArchimateConceptProxy)EObjectProxy.get(getEObject().getTarget());
    }
    
    public ArchimateRelationshipProxy setSource(ArchimateConceptProxy source) {
        return setSource(source, true);
    }
    
    public ArchimateRelationshipProxy setSource(ArchimateConceptProxy source, boolean updateViews) {
        if(!ArchimateModelUtils.isValidRelationship(source.getEObject(), getEObject().getTarget(), getEObject().eClass())) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateRelationshipProxy_0,
                    new Object[] { getEObject().eClass().getName(), source, getTarget() }));
        }
        
        CommandHandler.executeCommand(new ScriptCommand("source", getArchimateModel()) { //$NON-NLS-1$
            IArchimateConcept oldSource = getEObject().getSource();
            
            @Override
            public void perform() {
                getEObject().setSource(source.getEObject());
            }
            
            @Override
            public void undo() {
                getEObject().setSource(oldSource);
            }
        });
        
        // TODO: All diagram connections to be updated.
        //       If the new source diagram object exists in a view, connect to that else delete the connection
        if(updateViews && !objectRefs().isEmpty()) {
            throw new ArchiScriptException("Cannot set Source in Views"); //$NON-NLS-1$
        }
        
        return this;
    }
    
    public ArchimateRelationshipProxy setTarget(ArchimateConceptProxy target) {
        return setTarget(target, true);
    }
    
    protected ArchimateRelationshipProxy setTarget(ArchimateConceptProxy target, boolean updateViews) {
        if(!ArchimateModelUtils.isValidRelationship(getEObject().getSource(), target.getEObject(), getEObject().eClass())) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateRelationshipProxy_1,
                    new Object[] { getEObject().eClass().getName(), getSource(), target }));
        }
        
        CommandHandler.executeCommand(new ScriptCommand("target", getArchimateModel()) { //$NON-NLS-1$
            IArchimateConcept oldTarget = getEObject().getTarget();
            
            @Override
            public void perform() {
                getEObject().setTarget(target.getEObject());
            }
            
            @Override
            public void undo() {
                getEObject().setTarget(oldTarget);
            }
        });
        
        // TODO: All diagram connections to be updated.
        //       If the new target diagram object exists in a view, connect to that else delete the connection
        if(updateViews && !objectRefs().isEmpty()) {
            throw new ArchiScriptException("Cannot set Target in Views"); //$NON-NLS-1$
        }
        
        return this;
    }
    
    /**
     * Set the type of this relationship with a new relationship of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    @Override
    public ArchimateRelationshipProxy setType(String type) {
        if(super.setType(type) == null) {
            return this;
        }
        
        // Add new relationship
        ArchimateRelationshipProxy newRelationshipProxy = getModel().addRelationship(type, getName(),
                getSource(), getTarget(), (IFolder)getEObject().eContainer());
        
        if(newRelationshipProxy == null) {
            return this;
        }
        
        IArchimateRelationship newRelationship = newRelationshipProxy.getEObject();

        // Copy all properties
        Collection<IProperty> props = EcoreUtil.copyAll(getEObject().getProperties());
        newRelationship.getProperties().addAll(props);

        // Set source relations to this
        for(EObjectProxy proxy : outRels()) {
            ((ArchimateRelationshipProxy)proxy).setSource(newRelationshipProxy, false);
        }

        // Set target relations to this
        for(EObjectProxy proxy : inRels()) {
            ((ArchimateRelationshipProxy)proxy).setTarget(newRelationshipProxy, false);
        }

        // Store old relationship
        ArchimateRelationshipProxy oldProxy = new ArchimateRelationshipProxy(getEObject());

        // Update all diagram connections
        for(EObjectProxy proxy : objectRefs()) {
            // Store view for updating
            IDiagramModel dm = ((IDiagramModelArchimateComponent)proxy.getEObject()).getDiagramModel();

            CommandHandler.executeCommand(new ScriptCommand("type", getArchimateModel()) { //$NON-NLS-1$
                @Override
                public void perform() {
                    ((IDiagramModelArchimateComponent)proxy.getEObject()).setArchimateConcept(newRelationship);
                    ModelHandler.refreshEditor(dm);
                }

                @Override
                public void undo() {
                    ((IDiagramModelArchimateComponent)proxy.getEObject()).setArchimateConcept(oldProxy.getEObject());
                    ModelHandler.refreshEditor(dm);
                }
            });
        }

        // Set this eObject
        CommandHandler.executeCommand(new ScriptCommand("set", getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                getEObject().disconnect();
                setEObject(newRelationship);
            }

            @Override
            public void undo() {
                setEObject(oldProxy.getEObject());
                getEObject().reconnect();
            }
        });

        // Delete old relationship
        oldProxy.delete();

        return this;
    }

    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case SOURCE:
                return getSource();
            case TARGET:
                return getTarget();
        }
        
        return super.attr(attribute);
    }

    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case SOURCE:
                if(value instanceof ArchimateConceptProxy) {
                    return setSource((ArchimateConceptProxy)value);
                }
            case TARGET:
                if(value instanceof ArchimateConceptProxy) {
                    return setTarget((ArchimateConceptProxy)value);
                }
        }
        
        return super.attr(attribute, value);
    }
    
    @Override
    public void delete() {
        if(getEObject().getArchimateModel() != null) {
            CommandHandler.executeCommand(new DisconnectRelationshipCommand(getEObject()));
        }
        super.delete();
    }

}
