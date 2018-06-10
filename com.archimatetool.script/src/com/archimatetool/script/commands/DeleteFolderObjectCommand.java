/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IFolder;

/**
 * DeleteFolderObjectCommand
 * 
 * @author Phillip Beauvoir
 */
public class DeleteFolderObjectCommand extends ScriptCommand {

    private int index;
    private IFolder parent;

    public DeleteFolderObjectCommand(IArchimateModelObject eObject) {
        super("delete", eObject); //$NON-NLS-1$
        parent = (IFolder)eObject.eContainer();
    }

    @Override
    public void undo() {
        if(index != -1) { // might be already be deleted from Command in CompoundCommand
            if(getEObject() instanceof IFolder) {
                parent.getFolders().add(index, (IFolder)getEObject());
            }
            else {
                parent.getElements().add(index, getEObject());
            }
        }
    }

    @Override
    public void perform() {
        // Ensure index is stored just before execute because if this is part of a composite delete action
        // then the index positions will have changed
        
        if(getEObject() instanceof IFolder) {
            index = parent.getFolders().indexOf(getEObject());
            if(index != -1) { // might be already be deleted from Command in CompoundCommand
                parent.getFolders().remove(getEObject());
            }
        }
        else {
            index = parent.getElements().indexOf(getEObject()); 
            if(index != -1) { // might be already be deleted from Command in CompoundCommand
                parent.getElements().remove(getEObject());
            }
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        parent = null;
    }

}
