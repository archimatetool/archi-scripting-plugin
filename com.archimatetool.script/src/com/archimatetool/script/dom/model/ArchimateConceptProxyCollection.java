/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;


/**
 * Collection of ArchimateConceptProxy objects
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateConceptProxyCollection<T extends ArchimateConceptProxy> extends EObjectProxyCollection<T> {
    
    public void delete() {
        for(ArchimateConceptProxy object : this) {
            object.delete();
        }
    }
}
