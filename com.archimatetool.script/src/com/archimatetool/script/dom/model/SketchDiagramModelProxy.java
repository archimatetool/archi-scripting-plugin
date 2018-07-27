/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.ISketchModel;

/**
 * SketchDiagramModelProxy wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class SketchDiagramModelProxy extends DiagramModelProxy {
    
    SketchDiagramModelProxy(ISketchModel sm) {
        super(sm);
    }
    
    @Override
    protected ISketchModel getEObject() {
        return (ISketchModel)super.getEObject();
    }
    
}
