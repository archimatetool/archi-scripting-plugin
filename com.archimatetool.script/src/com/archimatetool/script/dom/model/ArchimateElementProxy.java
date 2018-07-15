/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Collection;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.osgi.util.NLS;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.script.ArchiScriptException;
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
        
        if(!ModelUtil.isAllowedSetType(getEObject(), type)) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateElementProxy_0, type));
        }
        
        // Add new Element
        ArchimateElementProxy newElementProxy = ModelUtil.addElement(getArchimateModel(), type, getName(), (IFolder)getEObject().eContainer());
        
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
            CommandHandler.executeCommand(new ScriptCommand("type", getArchimateModel()) { //$NON-NLS-1$
                IDiagramModelArchimateObject dmo = (IDiagramModelArchimateObject)proxy.getEObject();

                @Override
                public void perform() {
                    dmo.setArchimateConcept(newElement);
                    ModelUtil.refreshDiagramModelComponent(dmo);
                }

                @Override
                public void undo() {
                    dmo.setArchimateConcept(oldProxy.getEObject());
                    ModelUtil.refreshDiagramModelComponent(dmo);
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
}
