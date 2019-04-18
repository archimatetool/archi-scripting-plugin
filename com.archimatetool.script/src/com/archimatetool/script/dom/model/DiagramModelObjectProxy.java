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
import com.archimatetool.script.ArchiScriptException;
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
    
    /**
     * Add an Archimate element to this diagram object and return the new diagram object
     */
    public DiagramModelObjectProxy add(ArchimateElementProxy elementProxy, int x, int y, int width, int height) {
        if(getEObject() instanceof IDiagramModelContainer) {
            return ModelFactory.addArchimateDiagramObject((IDiagramModelContainer)getEObject(), elementProxy.getEObject(), x, y, width, height, false);
        }
        
        throw new ArchiScriptException(Messages.DiagramModelObjectProxy_0);
    }
    
    /**
     * Create and add a diagram object and return the diagram object proxy
     */
    public DiagramModelObjectProxy createObject(String type, int x, int y, int width, int height) {
        if(getEObject() instanceof IDiagramModelContainer) {
            return ModelFactory.createDiagramObject((IDiagramModelContainer)getEObject(), type, x, y, width, height, false);
        }
        
        throw new ArchiScriptException(Messages.DiagramModelObjectProxy_1);
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
    
    public DiagramModelObjectProxy setBounds(Map<?, ?> map) {
        int x = ModelUtil.getIntValueFromMap(map, "x", getEObject().getBounds().getX()); //$NON-NLS-1$
        int y = ModelUtil.getIntValueFromMap(map, "y", getEObject().getBounds().getY()); //$NON-NLS-1$
        int width = ModelUtil.getIntValueFromMap(map, "width", getEObject().getBounds().getWidth()); //$NON-NLS-1$
        int height = ModelUtil.getIntValueFromMap(map, "height", getEObject().getBounds().getHeight()); //$NON-NLS-1$
        
        IBounds bounds = IArchimateFactory.eINSTANCE.createBounds(x, y, width, height);
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__BOUNDS, bounds));
        
        return this;
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
    
    @Override
    protected EObjectProxyCollection find() {
        // We don't include relationships
        EObjectProxyCollection all = super.find();
        return all.filter(IModelConstants.ELEMENT);
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
    
    public int getOpacity() {
        return getEObject().getAlpha();
    }
    
    public DiagramModelObjectProxy setOpacity(int value) {
        if(value < 0) {
            value = 0;
        }
        if(value > 255) {
            value = 255;
        }
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__ALPHA, value));
        
        return this;
    }

    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case BOUNDS:
                return getBounds();
            case FILL_COLOR:
                return getFillColor();
            case OPACITY:
                return getOpacity();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case BOUNDS:
                if(value instanceof Map) {
                    return setBounds((Map<?, ?>)value);
                }
                break;
            case FILL_COLOR:
                if(value == null || value instanceof String) {
                    return setFillColor((String)value);
                }
                break;
            case OPACITY:
                if(value instanceof Integer) {
                    return setOpacity((int)value);
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
