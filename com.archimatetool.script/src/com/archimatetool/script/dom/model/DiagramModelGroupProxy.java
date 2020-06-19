/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IDiagramModelGroup;

/**
 * Diagram Model Note wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelGroupProxy extends DiagramModelObjectProxy {
    
    DiagramModelGroupProxy(IDiagramModelGroup object) {
        super(object);
    }
    
    @Override
    protected IDiagramModelGroup getEObject() {
        return (IDiagramModelGroup)super.getEObject();
    }
    
}
