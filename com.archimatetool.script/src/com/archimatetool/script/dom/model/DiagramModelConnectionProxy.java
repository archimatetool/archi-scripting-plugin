/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.ILineObject;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DisconnectConnectionCommand;
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
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case SOURCE:
                return getSource();
            case TARGET:
                return getTarget();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case LINE_WIDTH:
                if(value instanceof Integer) {
                    int width = (int)value;
                    if(width < 0) {
                        width = 1;
                    }
                    if(width > 3) {
                        width = 3;
                    }
                    
                    CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.LINE_OBJECT__LINE_WIDTH, width));
                    ((ILineObject)getEObject()).setLineWidth(width);
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

        CommandHandler.executeCommand(new DisconnectConnectionCommand(getEObject()));
    }
}
