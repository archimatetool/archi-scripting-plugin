/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Collection;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.ScriptCommand;

/**
 * Archimate Element wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateElementProxy extends ArchimateConceptProxy {
    
    ArchimateElementProxy(IArchimateElement element) {
        super(element);
    }
    
    @Override
    protected IArchimateElement getEObject() {
        return (IArchimateElement)super.getEObject();
    }
    
    /**
     * Set the type of this element with a new element of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    @Override
    public ArchimateElementProxy setType(String type) {
        if(super.setType(type) == null) {
            return this;
        }
        
        // Add new Element
        ArchimateElementProxy newElementProxy = getModel().addElement(type, getName(), (IFolder)getEObject().eContainer());
        
        if(newElementProxy == null) {
            return this;
        }
        
        IArchimateElement newElement = newElementProxy.getEObject();

        // Copy Properties
        Collection<IProperty> props = EcoreUtil.copyAll(getEObject().getProperties());
        newElement.getProperties().addAll(props);

        // Set source relations to this
        for(EObjectProxy proxy : outRels()) {
            ((ArchimateRelationshipProxy)proxy).setSource(newElementProxy, false);
        }

        // Set target relations to this
        for(EObjectProxy proxy : inRels()) {
            ((ArchimateRelationshipProxy)proxy).setTarget(newElementProxy, false);
        }

        // Store old element
        ArchimateElementProxy oldProxy = new ArchimateElementProxy(getEObject());

        // Update all diagram objects
        for(EObjectProxy proxy : objectRefs()) {
            // Store view for updating
            IDiagramModel dm = ((IDiagramModelArchimateComponent)proxy.getEObject()).getDiagramModel();
            
            CommandHandler.executeCommand(new ScriptCommand("type", getArchimateModel()) { //$NON-NLS-1$
                @Override
                public void perform() {
                    ((IDiagramModelArchimateComponent)proxy.getEObject()).setArchimateConcept(newElement);
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
                setEObject(newElement);
            }

            @Override
            public void undo() {
                setEObject(oldProxy.getEObject());
            }
        });

        // Delete old element
        oldProxy.delete();
        
        return this;
    }
    
    class SetTypeCommand extends ScriptCommand {

        public SetTypeCommand(String type) {
            super("type", getArchimateModel()); //$NON-NLS-1$
        }
        
        @Override
        public void undo() {
            
        }

        @Override
        public void perform() {
            
        }
        
        @Override
        public void dispose() {
        }
    }
}
