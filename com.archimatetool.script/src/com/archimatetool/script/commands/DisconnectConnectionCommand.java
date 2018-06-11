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
    
    private IDiagramModelConnection eObject;

    public DisconnectConnectionCommand(IDiagramModelConnection eObject) {
        super("delete", eObject); //$NON-NLS-1$
        this.eObject = eObject;
    }

    @Override
    public void undo() {
        eObject.reconnect();
    }

    @Override
    public void perform() {
        eObject.disconnect();
    }
    
    @Override
    public void dispose() {
        eObject = null;
    }
}
