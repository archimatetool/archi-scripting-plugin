/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IFolder;

/**
 * Duplicate an Archimate Element Command
 * 
 * @author Phillip Beauvoir
 */
public class DuplicateElementCommand extends ScriptCommand {
   
    private IFolder parentFolder;
    private IArchimateElement elementOriginal;
    private IArchimateElement elementCopy;

    public DuplicateElementCommand(IArchimateElement element, IFolder parentFolder) {
        super("duplicate", element.getArchimateModel()); //$NON-NLS-1$
        this.parentFolder = parentFolder;
        this.elementOriginal = element;
    }
    
    @Override
    public void undo() {
        parentFolder.getElements().remove(elementCopy);
    }
    
    @Override
    public void redo() {
        parentFolder.getElements().add(elementCopy);
    }

    @Override
    public void perform() {
        elementCopy = (IArchimateElement)elementOriginal.getCopy();
        elementCopy.setName(elementOriginal.getName() + " " + Messages.DuplicateElementCommand_0); //$NON-NLS-1$
        parentFolder.getElements().add(elementCopy);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        parentFolder = null;
        elementOriginal = null;
        elementCopy = null;
    }

    public IArchimateElement getDuplicate() {
        return elementCopy;
    }
}
