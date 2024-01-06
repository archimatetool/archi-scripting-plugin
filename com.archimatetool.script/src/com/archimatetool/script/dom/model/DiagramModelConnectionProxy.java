/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelBendpoint;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.ITextAlignment;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DisconnectConnectionCommand;
import com.archimatetool.script.commands.ScriptCommand;
import com.archimatetool.script.commands.SetCommand;

/**
 * Diagram Model Connection wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelConnectionProxy extends DiagramModelComponentProxy implements IRelationshipProxy {
    
    DiagramModelConnectionProxy(IDiagramModelConnection connection) {
        super(connection);
    }
    
    @Override
    protected IDiagramModelConnection getEObject() {
        return (IDiagramModelConnection)super.getEObject();
    }
    
    @Override
    protected boolean isArchimateConcept() {
        return getEObject() instanceof IDiagramModelArchimateConnection;
    }
    
    @Override
    public DiagramModelComponentProxy getSource() {
        return (DiagramModelComponentProxy)EObjectProxy.get(getEObject().getSource());
    }
    
    @Override
    public DiagramModelComponentProxy getTarget() {
        return (DiagramModelComponentProxy)EObjectProxy.get(getEObject().getTarget());
    }
    
    public boolean isLabelVisible() {
        return getEObject().getFeatures().getBoolean(IDiagramModelConnection.FEATURE_NAME_VISIBLE, true);
    }
    
    public DiagramModelConnectionProxy setLabelVisible(boolean visible) {
        CommandHandler.executeCommand(new SetCommand(getEObject(), IDiagramModelConnection.FEATURE_NAME_VISIBLE, visible, true));
        return this;
    }
    
    public DiagramModelConnectionProxy setTextPosition(int position) {
        if(position < 0) {
            position = 0;
        }
        if(position > 2) {
            position = 2;
        }
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_CONNECTION__TEXT_POSITION, position));
        return this;
    }
    
    public int getTextPosition() {
        return getEObject().getTextPosition();
    }
    
    public DiagramModelConnectionProxy setStyle(int style) {
        if(isArchimateConcept()) {
            throw new ArchiScriptException(Messages.DiagramModelConnectionProxy_4);
        }
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_CONNECTION__TYPE, style));
        return this;
    }
    
    public int getStyle() {
        return getEObject().getType();
    }
    
    // ===========================================
    // Bendpoints
    // ===========================================
    
    /**
     * @return Relative bendpoints
     */
    public List<Map<String, Object>> getRelativeBendpoints() {
        List<Map<String, Object>> list = new ArrayList<>();
        
        for(IDiagramModelBendpoint bp : getEObject().getBendpoints()) {
            Map<String, Object> map = new HashMap<>();
            map.put(START_X, bp.getStartX());
            map.put(START_Y, bp.getStartY());
            map.put(END_X, bp.getEndX());
            map.put(END_Y, bp.getEndY());
            list.add(map);
        }
        
        return list;
    }
    
    /**
     * Add a relative bendpoint at index position 
     */
    public DiagramModelConnectionProxy addRelativeBendpoint(Map<?, ?> map, int index) {
        if(index < 0 || index > getEObject().getBendpoints().size()) {
            throw new ArchiScriptException(Messages.DiagramModelConnectionProxy_0 + index);
        }
        
        IDiagramModelBendpoint bp = createBendpointFromMap(map);
        
        CommandHandler.executeCommand(new ScriptCommand(Messages.DiagramModelConnectionProxy_1, getEObject()) {
            @Override
            public void perform() {
                getEObject().getBendpoints().add(index, bp);
            }
            
            @Override
            public void undo() {
                getEObject().getBendpoints().remove(bp);
            }
        });
        
        return this;
    }
    
    public DiagramModelConnectionProxy setRelativeBendpoint(Map<?, ?> map, int index) {
        if(index < 0 || index >= getEObject().getBendpoints().size()) {
            throw new ArchiScriptException(Messages.DiagramModelConnectionProxy_0 + index);
        }
        
        IDiagramModelBendpoint bp = createBendpointFromMap(map);
        
        CommandHandler.executeCommand(new ScriptCommand(Messages.DiagramModelConnectionProxy_1, getEObject()) {
            IDiagramModelBendpoint previous;
            
            @Override
            public void perform() {
                previous = getEObject().getBendpoints().set(index, bp);
            }
            
            @Override
            public void undo() {
                getEObject().getBendpoints().set(index, previous);
            }
        });
        
        return this;
    }
    
    public DiagramModelConnectionProxy deleteAllBendpoints() {
        if(!getEObject().getBendpoints().isEmpty()) {
            for(int index = getEObject().getBendpoints().size() - 1; index >= 0; index--) {
                deleteBendpoint(index);    
            }
        }
        
        return this;
    }
    
    public DiagramModelConnectionProxy deleteBendpoint(int index) {
        if(index < 0 || index > getEObject().getBendpoints().size()) {
            throw new ArchiScriptException(Messages.DiagramModelConnectionProxy_2 + index);
        }
        
        CommandHandler.executeCommand(new ScriptCommand(Messages.DiagramModelConnectionProxy_3, getEObject()) {
            IDiagramModelBendpoint bp;
            
            @Override
            public void perform() {
                bp = getEObject().getBendpoints().remove(index);
            }
            
            @Override
            public void undo() {
                getEObject().getBendpoints().add(index, bp);
            }
        });
        
        return this;
    }
    
    /**
     * Create a bendpoint from a Map's members
     */
    private IDiagramModelBendpoint createBendpointFromMap(Map<?, ?> map) {
        int startX = ModelUtil.getIntValueFromMap(map, START_X, 0);
        int startY = ModelUtil.getIntValueFromMap(map, START_Y, 0);
        int endX = ModelUtil.getIntValueFromMap(map, END_X, 0);
        int endY = ModelUtil.getIntValueFromMap(map, END_Y, 0);
        
        IDiagramModelBendpoint bp = IArchimateFactory.eINSTANCE.createDiagramModelBendpoint();
        bp.setStartX(startX);
        bp.setStartY(startY);
        bp.setEndX(endX);
        bp.setEndY(endY);

        return bp;
    }
    
    public DiagramModelConnectionProxy setTextAlignment(int alignment) {
        if(alignment != ITextAlignment.TEXT_ALIGNMENT_CENTER
                && alignment != ITextAlignment.TEXT_ALIGNMENT_LEFT
                && alignment != ITextAlignment.TEXT_ALIGNMENT_RIGHT) {
            throw new ArchiScriptException(Messages.DiagramModelObjectProxy_2);
        }
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.TEXT_ALIGNMENT__TEXT_ALIGNMENT, alignment));
        
        return this;
    }
    
    public Object getTextAlignment() {
        return getEObject().getTextAlignment();
    }
    
    // ===========================================
    // Find / Attr
    // ===========================================
    
    @Override
    protected EObjectProxyCollection find() {
        // We don't include connected relationships
        return new EObjectProxyCollection();
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case SOURCE:
                return getSource();
            case TARGET:
                return getTarget();
            case LABEL_VISIBLE:
                return isLabelVisible();
            case TEXT_POSITION:
                return getTextPosition();
            case TEXT_ALIGNMENT:
                return getTextAlignment();
            case RELATIVE_BENDPOINTS:
                return getRelativeBendpoints();
            case STYLE:
                return getStyle();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case LABEL_VISIBLE:
                if(value instanceof Boolean) {
                    return setLabelVisible((boolean)value);
                }
                break;
            case TEXT_POSITION:
                if(value instanceof Integer) {
                    return setTextPosition((int)value);
                }
                break;
            case TEXT_ALIGNMENT:
                if(value instanceof Integer) {
                    return setTextAlignment((int)value);
                }
                break;
            case STYLE:
                if(value instanceof Integer) {
                    return setStyle((int)value);
                }
                break;
        }
        
        return super.attr(attribute, value);
    }

    @Override
    public void delete() {
        for(EObjectProxy proxy : inRels()) {
            proxy.delete();
        }
        
        for(EObjectProxy proxy : outRels()) {
            proxy.delete();
        }

        if(getArchimateModel() != null) {
            CommandHandler.executeCommand(new DisconnectConnectionCommand(getEObject()));
        }
    }
}
