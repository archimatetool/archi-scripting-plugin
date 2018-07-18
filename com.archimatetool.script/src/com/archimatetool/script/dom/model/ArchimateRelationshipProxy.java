/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.util.NLS;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IConnectable;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelArchimateConnection;
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
    
    protected ArchimateRelationshipProxy setSource(ArchimateConceptProxy source, boolean updateViews) {
        if(!ArchimateModelUtils.isValidRelationship(source.getEObject(), getEObject().getTarget(), getEObject().eClass())) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateRelationshipProxy_0,
                    new Object[] { getEObject().eClass().getName(), source, getTarget() }));
        }
        
        // Set the new source in the model
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
        
        if(updateViews) {
            // Get each instance of the connection in a view
            for(IDiagramModel diagramModel : getArchimateModel().getDiagramModels()) {
                for(IDiagramModelArchimateConnection matchingConnection : DiagramModelUtils.findDiagramModelConnectionsForRelation(diagramModel, getEObject())) {
                    
                    // Get the first instance of the new source in this view and connect to that
                    List<IDiagramModelArchimateComponent> list = DiagramModelUtils.findDiagramModelComponentsForArchimateConcept(diagramModel, source.getEObject());
                    if(!list.isEmpty()) {
                        IDiagramModelArchimateComponent matchingComponent = list.get(0);
                        IConnectable oldSource = matchingConnection.getSource();
                        
                        CommandHandler.executeCommand(new ScriptCommand("source", getArchimateModel()) { //$NON-NLS-1$
                            @Override
                            public void perform() {
                                matchingConnection.connect(matchingComponent, matchingConnection.getTarget());
                            }
                            
                            @Override
                            public void undo() {
                                matchingConnection.connect(oldSource, matchingConnection.getTarget());
                            }
                        });
                    }
                    // Not found, so delete the matching connection
                    else {
                        new DiagramModelConnectionProxy(matchingConnection).delete();
                    }
                }
            }
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
        
        // Set the new target in the model
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
        
        if(updateViews) {
            for(IDiagramModel diagramModel : getArchimateModel().getDiagramModels()) {
                // Get each instance of the connection in a view
                for(IDiagramModelArchimateConnection matchingConnection : DiagramModelUtils.findDiagramModelConnectionsForRelation(diagramModel, getEObject())) {
                    
                    // Get the first instance of the new target in this view and connect to that
                    List<IDiagramModelArchimateComponent> list = DiagramModelUtils.findDiagramModelComponentsForArchimateConcept(diagramModel, target.getEObject());
                    if(!list.isEmpty()) {
                        IDiagramModelArchimateComponent matchingComponent = list.get(0);
                        IConnectable oldTarget = matchingConnection.getTarget();
                        
                        CommandHandler.executeCommand(new ScriptCommand("target", getArchimateModel()) { //$NON-NLS-1$
                            @Override
                            public void perform() {
                                matchingConnection.connect(matchingConnection.getSource(), matchingComponent);
                            }
                            
                            @Override
                            public void undo() {
                                matchingConnection.connect(matchingConnection.getSource(), oldTarget);
                            }
                        });
                    }
                    // Not found, so delete the matching connection
                    else {
                        new DiagramModelConnectionProxy(matchingConnection).delete();
                    }
                }
            }
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
        
        if(!ModelUtil.isAllowedSetType(getEObject(), type)) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateRelationshipProxy_2, type));
        }
        
        // Add new relationship
        ArchimateRelationshipProxy newRelationshipProxy = ModelUtil.addRelationship(getArchimateModel(), type, getName(),
                getSource().getEObject(), getTarget().getEObject(), (IFolder)getEObject().eContainer());
        
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
            IDiagramModelArchimateConnection dmc = (IDiagramModelArchimateConnection)proxy.getEObject();

            CommandHandler.executeCommand(new ScriptCommand("type", getArchimateModel()) { //$NON-NLS-1$
                @Override
                public void perform() {
                    dmc.setArchimateConcept(newRelationship);
                    ModelUtil.refreshDiagramModelComponent(dmc);
                }

                @Override
                public void undo() {
                    dmc.setArchimateConcept(oldProxy.getEObject());
                    ModelUtil.refreshDiagramModelComponent(dmc);
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
