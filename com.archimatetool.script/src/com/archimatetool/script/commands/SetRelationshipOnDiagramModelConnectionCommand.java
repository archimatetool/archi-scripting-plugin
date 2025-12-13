/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.common.util.EList;

import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelConnection;

/**
 * Set the Diagram Model Connections's IArchimateRelationship to the given relationship
 * 
 * @author Phillip Beauvoir
 */
public class SetRelationshipOnDiagramModelConnectionCommand extends ScriptCommand {
   
    private IArchimateRelationship newRelationship;
    private IDiagramModelArchimateConnection dmc;
    private IArchimateRelationship oldRelationship;
    
    // Store the positions of the connection in their lists
    private int oldSourcePosition;
    private int oldTargetPosition;
    
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
        // Store these here
        oldSourcePosition = dmc.getSource().getSourceConnections().indexOf(dmc);
        oldTargetPosition = dmc.getTarget().getTargetConnections().indexOf(dmc);

        // Safety to deregister listeners on the concept and update the UI
        dmc.disconnect();
        
        // Set it
        dmc.setArchimateRelationship(newRelationship);
     
        // Reconnect and update UI
        dmc.reconnect();
        
        // Move these back
        restoreConnectionPositions();
    }
    
    @Override
    public void undo() {
        // Safety to deregister listeners on the concept and update the UI
        dmc.disconnect();
        
        // Set it back
        dmc.setArchimateRelationship(oldRelationship);
        
        // Reconnect and update UI
        dmc.reconnect();
        
        // Move these back
        restoreConnectionPositions();
    }

    // Restore the connection positions in their lists
    private void restoreConnectionPositions() {
        EList<IDiagramModelConnection> sources = dmc.getSource().getSourceConnections();
        if(oldSourcePosition >= 0 && oldSourcePosition < sources.size() && sources.contains(dmc)) {
            sources.move(oldSourcePosition, dmc);
        }
        
        EList<IDiagramModelConnection> targets = dmc.getTarget().getTargetConnections();
        if(oldTargetPosition >= 0 && oldTargetPosition < targets.size() && targets.contains(dmc)) {
            targets.move(oldTargetPosition, dmc);
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        newRelationship = null;
        dmc = null;
        oldRelationship = null;
    }
}
