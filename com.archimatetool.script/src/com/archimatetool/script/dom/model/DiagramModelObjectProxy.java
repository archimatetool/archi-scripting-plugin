/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDiagramModelReference;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DeleteDiagramModelObjectCommand;
import com.archimatetool.script.commands.SetCommand;

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
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case BOUNDS:
                // TODO: check bounds are correct
                break;
            case FILL_COLOR:
                if(value instanceof String) {
                    checkColorValue((String)value); // check correct color value
                }
                // Set color. A null value is allowed
                CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__FILL_COLOR, value));
                break;
        }
        
        return super.attr(attribute, value);
    }
    
    @Override
    public void delete() {
        for(EObjectProxy rel : inRels()) {
            rel.delete();
        }

        for(EObjectProxy rel : outRels()) {
            rel.delete();
        }
        
        for(EObjectProxy child : children()) {
            child.delete();
        }

        if(getEObject().eContainer() != null) {
            CommandHandler.executeCommand(new DeleteDiagramModelObjectCommand(getEObject()));
        }
    }
    
}
