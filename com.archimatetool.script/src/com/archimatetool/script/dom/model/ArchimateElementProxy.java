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
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IFeature;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IJunction;
import com.archimatetool.model.IProperty;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.ScriptCommand;
import com.archimatetool.script.commands.SetCommand;
import com.archimatetool.script.commands.SetElementOnDiagramModelObjectCommand;

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
        
        // Add a new Element to the model
        ArchimateElementProxy newElementProxy = ModelFactory.createElement(getArchimateModel(), type, getName(), (IFolder)getEObject().eContainer());
        
        if(newElementProxy == null) {
            return this;
        }
        
        IArchimateElement newElement = newElementProxy.getEObject();

        // Copy Properties to new element
        Collection<IProperty> props = EcoreUtil.copyAll(getEObject().getProperties());
        newElement.getProperties().addAll(props);
        
        // Copy Features to new element
        Collection<IFeature> features = EcoreUtil.copyAll(getEObject().getFeatures());
        newElement.getFeatures().addAll(features);

        // Copy Documentation to new element
        newElement.setDocumentation(getEObject().getDocumentation());

        // Set source relations to this
        for(EObjectProxy outRel : outRels()) {
            ((ArchimateRelationshipProxy)outRel).setSource(newElementProxy, false);
        }

        // Set target relations to this
        for(EObjectProxy inRel : inRels()) {
            ((ArchimateRelationshipProxy)inRel).setTarget(newElementProxy, false);
        }

        // Store the old proxy reference for later
        ArchimateConceptProxy oldProxy = (ArchimateConceptProxy)EObjectProxy.get(getEObject());
        
        // Set all diagram objects to the new element
        for(EObjectProxy dmoProxy : objectRefs()) {
            CommandHandler.executeCommand(new SetElementOnDiagramModelObjectCommand(newElement,
                    (IDiagramModelArchimateObject)dmoProxy.getEObject(), true));
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

        // Delete old proxy
        oldProxy.delete();
        
        return this;
    }
    
    /**
     * Merge this and the other Archimate element into this one element.
     * Diagram instances of the other Archimate element will be replaced with this element
     * @param others
     * @return this
     */
    public ArchimateElementProxy merge(ArchimateElementProxy other) {
        // Check this and the other are in the same model
        ModelUtil.checkComponentsInSameModel(getEObject(), other.getEObject());
        
        // Check the other is of the same type IArchimateElement
        if(other.getEObject().getClass() != getEObject().getClass()) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateElementProxy_0, other));
        }

        // Append Documentation from the other element
        setDocumentation(getDocumentation() + "\n" + other.getDocumentation()); //$NON-NLS-1$

        // Append Properties from the other element
        for(IProperty p : other.getEObject().getProperties()) {
            prop(p.getKey(), p.getValue(), true);
        }

        // Set all diagram objects to this element
        for(EObjectProxy dmoProxy : other.objectRefs()) {
            CommandHandler.executeCommand(new SetElementOnDiagramModelObjectCommand(getEObject(),
                    (IDiagramModelArchimateObject)dmoProxy.getEObject(), false));
        }
        
        // Set source relations of the others to this
        for(EObjectProxy outRel : other.outRels()) {
            ((ArchimateRelationshipProxy)outRel).setSource(this, false);
        }
        
        // Set target relations of the others to this
        for(EObjectProxy inRel : other.inRels()) {
            ((ArchimateRelationshipProxy)inRel).setTarget(this, false);
        }

        return this;
    }
    
    // Junction type
    
    public String getJunctionType() {
        if(getEObject() instanceof IJunction) {
            String type = ((IJunction)getEObject()).getType();
            return IJunction.AND_JUNCTION_TYPE.equals(type) ? "and" : type; //$NON-NLS-1$
        }
        return null;
    }
    
    public EObjectProxy setJunctionType(String type) {
        if(getEObject() instanceof IJunction && type != null) {
            type = type.toLowerCase();
            if(IModelConstants.JUNCTION_TYPES_LIST.contains(type)) {
                if("and".equals(type)) { //$NON-NLS-1$
                    type = IJunction.AND_JUNCTION_TYPE; // Internal Junction type "and" is the empty string ""
                }
                CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.JUNCTION__TYPE, type));
            }
        }
        
        return this;
    }

    // Attr
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case JUNCTION_TYPE:
                return getJunctionType();
        }
        
        return super.attr(attribute);
    }

    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case JUNCTION_TYPE:
                if(value instanceof String) {
                    return setJunctionType((String)value);
                }
        }
        
        return super.attr(attribute, value);
    }
}
