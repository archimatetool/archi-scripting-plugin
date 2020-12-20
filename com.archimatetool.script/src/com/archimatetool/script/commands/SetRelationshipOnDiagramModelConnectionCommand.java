/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModelArchimateConnection;

/**
 * Set the Diagram Model Connections's IArchimateRelationship to the given relationship
 * 
 * @author Phillip Beauvoir
 */
public class SetRelationshipOnDiagramModelConnectionCommand extends ScriptCommand {
   
    private IArchimateRelationship newRelationship;
    private IDiagramModelArchimateConnection dmc;
    private IArchimateRelationship oldRelationship;
    
    /**
     * @param relationship The relationship to set on the dmc
     * @param dmc The dmo to set the relationship on
     */
    public SetRelationshipOnDiagramModelConnectionCommand(IArchimateRelationship newRelationship, IDiagramModelArchimateConnection dmc) {
        super("setRelationship", newRelationship.getArchimateModel()); //$NON-NLS-1$
        
        this.newRelationship = newRelationship;
        this.dmc = dmc;
        oldRelationship = dmc.getArchimateRelationship();
    }
    
    @Override
    public void perform() {
        // Safety to deregister listeners on the concept and update the UI
        dmc.disconnect();
        
        // Set it
        dmc.setArchimateRelationship(newRelationship);
     
        // Reconnect and update UI
        dmc.reconnect();
    }
    
    @Override
    public void undo() {
        // Safety to deregister listeners on the concept and update the UI
        dmc.disconnect();
        
        // Set it back
        dmc.setArchimateRelationship(oldRelationship);
        
        // Reconnect and update UI
        dmc.reconnect();
    }

    @Override
    public void dispose() {
        newRelationship = null;
        dmc = null;
        oldRelationship = null;
    }
}
