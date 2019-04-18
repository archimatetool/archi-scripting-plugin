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
import com.archimatetool.model.IDiagramModelBendpoint;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.ILineObject;
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
    public DiagramModelComponentProxy getSource() {
        return (DiagramModelComponentProxy)EObjectProxy.get(getEObject().getSource());
    }
    
    @Override
    public DiagramModelComponentProxy getTarget() {
        return (DiagramModelComponentProxy)EObjectProxy.get(getEObject().getTarget());
    }
    
    public DiagramModelComponentProxy setLineWidth(int value) {
        int width = value;
        if(width < 0) {
            width = 1;
        }
        if(width > 3) {
            width = 3;
        }
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.LINE_OBJECT__LINE_WIDTH, width));
        ((ILineObject)getEObject()).setLineWidth(width);
        return this;
    }
    
    // ===========================================
    // Bendpoints
    // ===========================================
    
    /**
     * @return Relative bendpoints
     */
    public List<Map<String, Integer>> getRelativeBendpoints() {
        List<Map<String, Integer>> list = new ArrayList<Map<String, Integer>>();
        
        for(IDiagramModelBendpoint bp : getEObject().getBendpoints()) {
            HashMap<String, Integer> map = new HashMap<>();
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
        
        int startX = ModelUtil.getIntValueFromMap(map, START_X, 0);
        int startY = ModelUtil.getIntValueFromMap(map, START_Y, 0);
        int endX = ModelUtil.getIntValueFromMap(map, END_X, 0);
        int endY = ModelUtil.getIntValueFromMap(map, END_Y, 0);
        
        IDiagramModelBendpoint bp = IArchimateFactory.eINSTANCE.createDiagramModelBendpoint();
        bp.setStartX(startX);
        bp.setStartY(startY);
        bp.setEndX(endX);
        bp.setEndY(endY);
        
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
            case RELATIVE_BENDPOINTS:
                return getRelativeBendpoints();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case LINE_WIDTH:
                if(value instanceof Integer) {
                    return setLineWidth((int)value);
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
