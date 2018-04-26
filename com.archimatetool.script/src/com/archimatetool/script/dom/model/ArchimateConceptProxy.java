/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;

/**
 * Archimate Concept wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class ArchimateConceptProxy extends EObjectProxy {
    
    public ArchimateConceptProxy() {
    }
    
    public ArchimateConceptProxy(IArchimateConcept concept) {
        super(concept);
    }
    
    @Override
    protected IArchimateConcept getEObject() {
        return (IArchimateConcept)super.getEObject();
    }

    public ExtendedCollection getSourceRelationships() {
        ExtendedCollection list = new ExtendedCollection();
        for(IArchimateRelationship r : getEObject().getSourceRelationships()) {
            list.add(EObjectProxy.get(r));
        }
        return list;
    }
    
    public ExtendedCollection getTargetRelationships() {
        ExtendedCollection list = new ExtendedCollection();
        for(IArchimateRelationship r : getEObject().getTargetRelationships()) {
            list.add(EObjectProxy.get(r));
        }
        return list;
    }

    @Override
    public Object attr(String attribute) {
        switch(attribute) {
            case "sourceRelationships": //$NON-NLS-1$
                return getSourceRelationships();
            case "targetRelationships": //$NON-NLS-1$
                return getTargetRelationships();
        }
        
        return super.attr(attribute);
    }

}
