/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimateRelationship;

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
        getEObject().setSource(source.getEObject());
        return this;
    }
    
    public ArchimateRelationshipProxy setTarget(ArchimateConceptProxy target) {
        checkModelAccess();
        getEObject().setTarget(target.getEObject());
        return this;
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
