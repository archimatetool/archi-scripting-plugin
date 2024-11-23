/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IFolder;

/**
 * AddElementCommand
 * 
 * @author Phillip Beauvoir
 */
public class AddElementCommand extends ScriptCommand {
   
    private IFolder parent;
    private IArchimateElement element;

    public AddElementCommand(IFolder parent, IArchimateElement element) {
        super("add", parent.getArchimateModel()); //$NON-NLS-1$
        this.element = element;
        this.parent = parent;
    }
    
    @Override
    public void undo() {
        parent.getElements().remove(element);
    }

    @Override
    public void perform() {
        parent.getElements().add(element);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        parent = null;
        element = null;
    }
}
