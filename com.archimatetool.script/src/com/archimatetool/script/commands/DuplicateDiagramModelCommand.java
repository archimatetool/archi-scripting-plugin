/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.UUIDFactory;

/**
 * Duplicate a Diagram Model Command
 * 
 * @author Phillip Beauvoir
 */
public class DuplicateDiagramModelCommand extends ScriptCommand {
   
    private IFolder parentFolder;
    private IDiagramModel dmOriginal;
    private IDiagramModel dmCopy;

    public DuplicateDiagramModelCommand(IDiagramModel dm, IFolder parentFolder) {
        super("duplicate", dm.getArchimateModel()); //$NON-NLS-1$
        this.parentFolder = parentFolder;
        dmOriginal = dm;
    }
    
    @Override
    public void undo() {
        if(PlatformUI.isWorkbenchRunning()) {
            EditorManager.closeDiagramEditor(dmCopy); // important!!
        }
        
        parentFolder.getElements().remove(dmCopy);
    }
    
    @Override
    public void redo() {
        parentFolder.getElements().add(dmCopy);
    }

    @Override
    public void perform() {
        // Copy
        dmCopy = EcoreUtil.copy(dmOriginal);
        // New IDs
        UUIDFactory.generateNewIDs(dmCopy);
        // Name
        dmCopy.setName(dmOriginal.getName() + " " + Messages.DuplicateDiagramModelCommand_0); //$NON-NLS-1$
        // Add to folder
        parentFolder.getElements().add(dmCopy);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        parentFolder = null;
        dmOriginal = null;
        dmCopy = null;
    }

    public IDiagramModel getDuplicate() {
        return dmCopy;
    }
}
