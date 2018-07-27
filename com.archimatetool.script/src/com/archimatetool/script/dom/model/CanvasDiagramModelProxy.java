/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.canvas.model.ICanvasModel;

/**
 * CanvasDiagramModelProxy wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class CanvasDiagramModelProxy extends DiagramModelProxy {
    
    CanvasDiagramModelProxy(ICanvasModel cm) {
        super(cm);
    }
    
    @Override
    protected ICanvasModel getEObject() {
        return (ICanvasModel)super.getEObject();
    }
    
}
