/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimateConcept;

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

}
