/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;


/**
 * Collection of ArchimateRelationshipProxy objects
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateRelationshipProxyCollection extends ArchimateConceptProxyCollection<ArchimateRelationshipProxy> {
    
    public ArchimateRelationshipProxyCollection setSource(ArchimateConceptProxy source) {
        for(ArchimateRelationshipProxy object : this) {
            object.setSource(source);
        }
        
        return this;
    }

    public ArchimateRelationshipProxyCollection setTarget(ArchimateConceptProxy target) {
        for(ArchimateRelationshipProxy object : this) {
            object.setTarget(target);
        }
        
        return this;
    }
}
