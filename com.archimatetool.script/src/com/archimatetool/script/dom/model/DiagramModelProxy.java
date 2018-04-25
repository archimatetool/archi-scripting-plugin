/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IDiagramModel;

/**
 * DiagramModel wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelProxy extends EObjectProxy {
    
    public DiagramModelProxy() {
    }
    
    public DiagramModelProxy(IDiagramModel dm) {
        super(dm);
    }
    
    @Override
    protected IDiagramModel getEObject() {
        return (IDiagramModel)super.getEObject();
    }
    
    public String getDocumentation() {
        return (String)attr(DOCUMENTATION);
    }
    
    public ArchimateConceptProxy setDocumentation(String documentation) {
        return (ArchimateConceptProxy)attr(DOCUMENTATION, documentation);
    }
    
    @Override
    protected boolean canReadAttr(String attribute) {
        return super.canReadAttr(attribute) || DOCUMENTATION.equals(attribute);
    }

    @Override
    protected boolean canWriteAttr(String attribute) {
        return super.canReadAttr(attribute) || DOCUMENTATION.equals(attribute);
    }
}
