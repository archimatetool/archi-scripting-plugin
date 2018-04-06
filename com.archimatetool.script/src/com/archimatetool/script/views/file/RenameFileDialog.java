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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;


/**
 * "Rename File" Dialog
 */
public class RenameFileDialog {
	
    /**
     * Owner Shell
     */
    private Shell fShell;
    
    /**
     * File to Rename
     */
    private File fFile;
    
    /**
     * The renamed File
     */
    private File fRenamedFile;
    
    /**
     * The old name
     */
    private String fOldName;
    

    /**
	 * Constructor
	 * @param file The File to rename
	 */
	public RenameFileDialog(Shell shell, File file) {
	    fShell = shell;
	    fFile = file;
	    fOldName = fFile.getName();
	}
	
    /**
     * Throw up a dialog asking for a new name.
     * This will *not* do the Rename operation!
     * @return True if the user entered valid input, false if cancelled
     */
    public boolean open() {
        String title = NLS.bind(Messages.RenameFileDialog_0, fFile.getName());
        
        InputDialog dialog = new InputDialog(fShell,
                title,
                Messages.RenameFileDialog_1,
                fFile.getName(),
                new InputValidator());
        
        int code = dialog.open();
        
        if(code == Window.OK) {
            String newName = dialog.getValue();
            fRenamedFile = new File(fFile.getParent(), newName);
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * @return The renamed file if successful, or null if not
     */
    public File getRenamedFile() {
        return fRenamedFile;
    }
    
    /**
     * Validate user input
     */
    protected class InputValidator implements IInputValidator {

        public String isValid(String newText) {
            if("".equals(newText.trim())) { //$NON-NLS-1$
                return Messages.RenameFileDialog_2;
            }
            
            // This will ensure non-legal filename characters are disallowed
            IStatus result = ResourcesPlugin.getWorkspace().validateName(newText, IResource.FILE);
            if(!result.isOK())  {
                return Messages.RenameFileDialog_3;
            }
            
            if(newText.equals(fOldName)) {
                return ""; //$NON-NLS-1$
            }
            
            if(newText.equalsIgnoreCase(fOldName)) {
                return null;
            }

            File newfile = new File(fFile.getParent(), newText);
            if(newfile.exists()) {
                return Messages.RenameFileDialog_4;
            }
            
            return null;
        }
    }
}
