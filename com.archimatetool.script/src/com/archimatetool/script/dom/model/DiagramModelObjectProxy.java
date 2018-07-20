/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateFactory;
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
    
    DiagramModelObjectProxy(IDiagramModelObject object) {
        super(object);
    }
    
    @Override
    protected IDiagramModelObject getEObject() {
        return (IDiagramModelObject)super.getEObject();
    }
    
    public Map<String, Integer> getBounds() {
        IBounds b = getEObject().getBounds();
        
        HashMap<String, Integer> map = new HashMap<>();
        map.put("x", b.getX()); //$NON-NLS-1$
        map.put("y", b.getY()); //$NON-NLS-1$
        map.put("width", b.getWidth()); //$NON-NLS-1$
        map.put("height", b.getHeight()); //$NON-NLS-1$
        
        return map;
    }
    
    public void setBounds(Map<?, ?> map) {
        int x = (map.get("x") instanceof Number) ? ((Number)map.get("x")).intValue() : getEObject().getBounds().getX(); //$NON-NLS-1$ //$NON-NLS-2$
        int y = (map.get("y") instanceof Number) ? ((Number)map.get("y")).intValue() : getEObject().getBounds().getY(); //$NON-NLS-1$ //$NON-NLS-2$
        int width = (map.get("width") instanceof Number) ? ((Number)map.get("width")).intValue() : getEObject().getBounds().getWidth(); //$NON-NLS-1$ //$NON-NLS-2$
        int height = (map.get("height") instanceof Number) ? ((Number)map.get("height")).intValue() : getEObject().getBounds().getHeight(); //$NON-NLS-1$ //$NON-NLS-2$
        
        IBounds bounds = IArchimateFactory.eINSTANCE.createBounds(x, y, width, height);
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__BOUNDS, bounds));
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
    protected EObjectProxyCollection children() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IDiagramModelContainer) {
            for(IDiagramModelObject dmo : ((IDiagramModelContainer)getEObject()).getChildren()) {
                list.add(new DiagramModelObjectProxy(dmo));
            }
        }
        
        return list;
    }
    
    public String getFillColor() {
        return getEObject().getFillColor();
    }
    
    public DiagramModelObjectProxy setFillColor(String value) {
        checkColorValue(value); // check correct color value
        // Set color. A null value is allowed
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__FILL_COLOR, value));
        return this;
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case BOUNDS:
                return getBounds();
            case FILL_COLOR:
                return getFillColor();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case BOUNDS:
                if(value instanceof Map) {
                    setBounds((Map<?, ?>)value);
                }
                break;
            case FILL_COLOR:
                if(value == null || value instanceof String) {
                    return setFillColor((String)value);
                }
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
