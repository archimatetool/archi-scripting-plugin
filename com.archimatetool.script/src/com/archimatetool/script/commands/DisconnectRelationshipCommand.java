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

    public DisconnectRelationshipCommand(IArchimateRelationship eObject) {
        super("delete", eObject); //$NON-NLS-1$
    }

    @Override
    public IArchimateRelationship getEObject() {
        return (IArchimateRelationship)super.getEObject();
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
