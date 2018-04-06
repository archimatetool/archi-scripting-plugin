/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.file;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;


/**
 * "New Folder" Dialog
 */
public class NewFolderDialog {
	
    /**
     * Owner Shell
     */
    private Shell fShell;
    
    /**
     * Parent Folder
     */
    private File fParentFolder;
    
    /**
     * The new folder
     */
    private File fNewFolder;
    
	/**
	 * Constructor
	 * @param parentFolder Parent File to add to
	 */
	public NewFolderDialog(Shell shell, File parentFolder) {
	    fShell = shell;
	    fParentFolder = parentFolder;
	}
	
    /**
     * @return The new File or null if not set
     */
    public File getFolder() {
        return fNewFolder;
    }
	
    /**
     * Throw up a dialog asking for a Resource Group name
     * @return True if the user entered valid input, false if cancelled
     */
    public boolean open() {
        InputDialog dialog = new InputDialog(fShell,
                Messages.NewFolderDialog_0,
                Messages.NewFolderDialog_1,
                "", //$NON-NLS-1$
                new InputValidator());
        
        int code = dialog.open();
        
        if(code == Window.OK) {
            String s = dialog.getValue();
            fNewFolder = new File(fParentFolder, s);
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Validate user input
     */
    private class InputValidator implements IInputValidator {
        
        public String isValid(String newText) {
            if("".equals(newText.trim())) { //$NON-NLS-1$
                return Messages.NewFolderDialog_2;
            }
            
            File folder = new File(fParentFolder, newText);
            if(folder.exists()) {
                return Messages.NewFolderDialog_3;
            }

            // This will ensure non-legal filename characters are disallowed
            IStatus result = ResourcesPlugin.getWorkspace().validateName(newText, IResource.FOLDER);
            if(!result.isOK())  {
                return Messages.NewFolderDialog_4;
            }

            return null;
        }
    }
}
