/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.osgi.util.NLS;

import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.util.ArchimateModelUtils;

/**
 * Archimate Relationship wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateRelationshipProxy extends ArchimateConceptProxy {
    
    public ArchimateRelationshipProxy() {
    }
    
    public ArchimateRelationshipProxy(IArchimateRelationship relationship) {
        super(relationship);
    }
    
    @Override
    protected IArchimateRelationship getEObject() {
        return (IArchimateRelationship)super.getEObject();
    }

    public ArchimateConceptProxy getSource() {
        return (ArchimateConceptProxy)EObjectProxy.get(getEObject().getSource());
    }
    
    public ArchimateConceptProxy getTarget() {
        return (ArchimateConceptProxy)EObjectProxy.get(getEObject().getTarget());
    }
    
    public ArchimateRelationshipProxy setSource(ArchimateConceptProxy source) {
        checkModelAccess();
        
        if(!ArchimateModelUtils.isValidRelationship(source.getEObject(), getEObject().getTarget(), getEObject().eClass())) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateRelationshipProxy_0,
                    new Object[] { getEObject().eClass().getName(), source, getTarget() }));
        }
        
        getEObject().setSource(source.getEObject());

        return this;
    }
    
    public ArchimateRelationshipProxy setTarget(ArchimateConceptProxy target) {
        checkModelAccess();
        
        if(!ArchimateModelUtils.isValidRelationship(getEObject().getSource(), target.getEObject(), getEObject().eClass())) {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateRelationshipProxy_1,
                    new Object[] { getEObject().eClass().getName(), getSource(), target }));
        }
        
        getEObject().setTarget(target.getEObject());

        return this;
    }
    
    /**
     * Replace this relationship with a new relationship of class type, preserving all connecting relationships and diagram components
     * @param type the Archimate type to replace with
     * @return
     */
    @Override
    public ArchimateRelationshipProxy replace(String type) {
        ArchimateRelationshipProxy newRelationship = getModel().addRelationship(type, getName(), getSource(), getTarget());
        
        if(newRelationship != null) {
            newRelationship.setProperties(getProperties());
            
            getSourceRelationships().setSource(newRelationship);
            getTargetRelationships().setTarget(newRelationship);
            getDiagramComponentInstances().setArchimateConcept(newRelationship);
            
            getEObject().disconnect();
            delete();
        }
        
        return newRelationship;
    }

    @Override
    public Object attr(String attribute) {
        switch(attribute) {
            case "source": //$NON-NLS-1$
                return getSource();
            case "target": //$NON-NLS-1$
                return getTarget();
        }
        
        return super.attr(attribute);
    }

    @Override
    public EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case "source": //$NON-NLS-1$
                if(value instanceof ArchimateConceptProxy) {
                    return setSource((ArchimateConceptProxy)value);
                }
            case "target": //$NON-NLS-1$
                if(value instanceof ArchimateConceptProxy) {
                    return setTarget((ArchimateConceptProxy)value);
                }
        }
        
        return super.attr(attribute, value);
    }
}
