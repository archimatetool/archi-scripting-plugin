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

    public EObjectProxy getSource() {
        return EObjectProxy.get(getEObject().getSource());
    }
    
    public EObjectProxy getTarget() {
        return EObjectProxy.get(getEObject().getTarget());
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

}
