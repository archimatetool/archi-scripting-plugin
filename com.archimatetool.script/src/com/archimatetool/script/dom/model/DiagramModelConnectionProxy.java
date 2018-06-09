/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IDiagramModelConnection;

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
    public void delete() {
        checkModelAccess();
        
        for(IDiagramModelConnection connection : getConnectionsToDelete(getEObject())) {
            connection.disconnect();
        }
    }
    
    private List<IDiagramModelConnection> getConnectionsToDelete(IDiagramModelConnection connection) {
        List<IDiagramModelConnection> list = new ArrayList<>();
        list.add(connection);
        
        // Recurse on source connections
        for(IDiagramModelConnection conn : connection.getSourceConnections()) {
            list.addAll(getConnectionsToDelete(conn));
        }

        // Recurse on target connections
        for(IDiagramModelConnection conn : connection.getTargetConnections()) {
            list.addAll(getConnectionsToDelete(conn));
        }
        
        return list;
    }
}
