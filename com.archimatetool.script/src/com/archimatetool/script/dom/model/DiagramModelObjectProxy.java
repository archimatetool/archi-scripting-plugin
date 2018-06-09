/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDiagramModelReference;

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
    
    DiagramModelObjectProxy(IDiagramModelObject object) {
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
     * @return true if this diagram component references a Diagram Model
     */
    private boolean isDiagramModelReference() {
        return getEObject() instanceof IDiagramModelReference;
    }
    
    @Override
    protected EObject getReferencedConcept() {
        if(isDiagramModelReference()) {
            return ((IDiagramModelReference)getEObject()).getReferencedModel();
        }

        return super.getReferencedConcept();
    }

    /**
     * @return child node diagram objects of this diagram object (if any)
     */
    @Override
    public EObjectProxyCollection children() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IDiagramModelContainer) {
            for(IDiagramModelObject dmo : ((IDiagramModelContainer)getEObject()).getChildren()) {
                list.add(new DiagramModelObjectProxy(dmo));
            }
        }
        
        return list;
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case BOUNDS:
                return getBounds();
            case FILL_COLOR:
                return getEObject().getFillColor();
        }
        
        return super.attr(attribute);
    }

    @Override
    public void delete() {
        checkModelAccess();
        delete(getEObject());
    }
    
    private void delete(IDiagramModelObject object) {
        ((IDiagramModelContainer)object.eContainer()).getChildren().remove(object);

        for(IDiagramModelConnection connection : new ArrayList<>(object.getSourceConnections())) {
            delete(connection);
        }

        for(IDiagramModelConnection connection : new ArrayList<>(object.getTargetConnections())) {
            delete(connection);
        }
        
        if(object instanceof IDiagramModelContainer) {
            for(IDiagramModelObject child : new ArrayList<>(((IDiagramModelContainer)object).getChildren())) {
                delete(child);
            }
        }
    }
    
}
