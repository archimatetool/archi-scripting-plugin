/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;

/**
 * DeleteDiagramModelObjectCommand
 * 
 * @author Phillip Beauvoir
 */
public class DeleteDiagramModelObjectCommand extends ScriptCommand {
    
    private int index;
    private IDiagramModelContainer parent;
    private IDiagramModelObject eObject;

    public DeleteDiagramModelObjectCommand(IDiagramModelObject eObject) {
        super("delete", eObject); //$NON-NLS-1$
        parent = (IDiagramModelContainer)eObject.eContainer();
        this.eObject = eObject;
    }

    @Override
    public void undo() {
        // Add the Child at old index position
        if(index != -1) { // might have already been deleted by another process
            parent.getChildren().add(index, eObject);
        }
    }

    @Override
    public void perform() {
        // Ensure index is stored just before execute because if this is part of a composite delete action
        // then the index positions will have changed
        index = parent.getChildren().indexOf(eObject); 
        if(index != -1) { // might be already be deleted from Command in CompoundCommand
            parent.getChildren().remove(eObject);
        }
    }
    
    @Override
    public void dispose() {
        eObject = null;
        parent = null;
    }
}
