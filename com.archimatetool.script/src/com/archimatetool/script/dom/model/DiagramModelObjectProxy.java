/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;

/**
 * Diagram Model Object wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelObjectProxy extends DiagramModelComponentProxy {
    
    public static class Bounds {
        public int x, y, width, height;

        public Bounds(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    
    public DiagramModelObjectProxy(IDiagramModelObject object) {
        super(object);
    }
    
    @Override
    protected IDiagramModelObject getEObject() {
        return (IDiagramModelObject)super.getEObject();
    }
    
    public Bounds getBounds() {
        IBounds b = getEObject().getBounds();
        return new Bounds(b.getX(), b.getY(), b.getWidth(), b.getHeight());
    }
    
    /**
     * @return child node diagram objects of this diagram object (if any)
     */
    @Override
    public EObjectProxyCollection<DiagramModelObjectProxy> children() {
        EObjectProxyCollection<DiagramModelObjectProxy> list = new EObjectProxyCollection<DiagramModelObjectProxy>();
        
        if(getEObject() instanceof IDiagramModelContainer) {
            for(IDiagramModelObject dmo : ((IDiagramModelContainer)getEObject()).getChildren()) {
                list.add(new DiagramModelObjectProxy(dmo));
            }
        }
        
        return list;
    }
    
    @Override
    public Object attr(String attribute) {
        switch(attribute) {
            case CHILDREN:
                return children();
            case BOUNDS:
                return getBounds();
            case FILL_COLOR:
                return getEObject().getFillColor();
        }
        
        return super.attr(attribute);
    }

}
