/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IFolder;

/**
 * DeleteFolderObjectCommand
 * 
 * @author Phillip Beauvoir
 */
public class DeleteFolderObjectCommand extends ScriptCommand {

    private int index;
    private IFolder parent;
    private EObject eObject;

    public DeleteFolderObjectCommand(EObject eObject) {
        super("delete", eObject); //$NON-NLS-1$
        parent = (IFolder)eObject.eContainer();
        this.eObject = eObject;
    }

    @Override
    public void undo() {
        if(index != -1) { // might be already be deleted from Command in CompoundCommand
            if(eObject instanceof IFolder) {
                parent.getFolders().add(index, (IFolder)eObject);
            }
            else {
                parent.getElements().add(index, eObject);
            }
        }
    }

    @Override
    public void perform() {
        // Ensure index is stored just before execute because if this is part of a composite delete action
        // then the index positions will have changed
        
        if(eObject instanceof IFolder) {
            index = parent.getFolders().indexOf(eObject);
            if(index != -1) { // might be already be deleted from Command in CompoundCommand
                parent.getFolders().remove(eObject);
            }
        }
        else {
            index = parent.getElements().indexOf(eObject); 
            if(index != -1) { // might be already be deleted from Command in CompoundCommand
                parent.getElements().remove(eObject);
            }
        }
    }
    
    @Override
    public void dispose() {
        super.dispose();
        eObject = null;
        parent = null;
    }

}
