/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IDiagramModelConnection;

/**
 * DisconnectRelationshipCommand
 * 
 * @author Phillip Beauvoir
 */
public class DisconnectConnectionCommand extends ScriptCommand {

    public DisconnectConnectionCommand(IDiagramModelConnection eObject) {
        super("delete", eObject); //$NON-NLS-1$
    }

    @Override
    public IDiagramModelConnection getEObject() {
        return (IDiagramModelConnection)super.getEObject();
    }
    
    @Override
    public void undo() {
        getEObject().reconnect();
    }

    @Override
    public void perform() {
        getEObject().disconnect();
    }
    
}
