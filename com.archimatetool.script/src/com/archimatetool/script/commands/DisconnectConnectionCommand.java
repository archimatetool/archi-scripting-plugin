/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.IDiagramModelConnection;

/**
 * DisconnectRelationshipCommand
 * 
 * @author Phillip Beauvoir
 */
public class DisconnectConnectionCommand extends ScriptCommand {
    
    private IDiagramModelConnection connection;
    
    // Store the positions of the connection in their lists
    private int oldSourcePosition;
    private int oldTargetPosition;

    public DisconnectConnectionCommand(IDiagramModelConnection connection) {
        super("delete", connection); //$NON-NLS-1$
        this.connection = connection;
    }

    @Override
    public void undo() {
        connection.reconnect();
        
        // restore these
        EList<IDiagramModelConnection> sources = connection.getSource().getSourceConnections();
        if(oldSourcePosition >= 0 && oldSourcePosition < sources.size() && sources.contains(connection)) {
            sources.move(oldSourcePosition, connection);
        }
        
        EList<IDiagramModelConnection> targets = connection.getTarget().getTargetConnections();
        if(oldTargetPosition >= 0 && oldTargetPosition < targets.size() && targets.contains(connection)) {
            targets.move(oldTargetPosition, connection);
        }
    }

    @Override
    public void perform() {
        // Store these here
        oldSourcePosition = connection.getSource().getSourceConnections().indexOf(connection);
        oldTargetPosition = connection.getTarget().getTargetConnections().indexOf(connection);

        connection.disconnect();
    }
    
    @Override
    public void dispose() {
        super.dispose();
        connection = null;
    }
}
