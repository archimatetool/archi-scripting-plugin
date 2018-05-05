/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

/**
 * Collection of DiagramModelArchimateComponentProxy objects
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelArchimateComponentProxyCollection extends EObjectProxyCollection<DiagramModelArchimateComponentProxy> {
    
    public DiagramModelArchimateComponentProxyCollection setArchimateConcept(ArchimateConceptProxy concept) {
        for(EObjectProxy object : this) {
            ((DiagramModelArchimateComponentProxy)object).setArchimateConcept(concept);
        }
        
        return this;
    }
}
