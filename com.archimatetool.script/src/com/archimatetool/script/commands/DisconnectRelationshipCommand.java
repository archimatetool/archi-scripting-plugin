/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateRelationship;

/**
 * DisconnectRelationshipCommand
 * 
 * @author Phillip Beauvoir
 */
public class DisconnectRelationshipCommand extends ScriptCommand {
    
    private IArchimateRelationship eObject;

    public DisconnectRelationshipCommand(IArchimateRelationship eObject) {
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
        super.dispose();
        eObject = null;
    }
}
