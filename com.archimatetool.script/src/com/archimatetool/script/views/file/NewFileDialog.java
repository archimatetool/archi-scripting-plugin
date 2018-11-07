/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.file;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;


/**
 * "New File" Dialog
 */
public class NewFileDialog {
	
    /**
     * Owner Shell
     */
    private Shell fShell;
    
    /**
     * Parent Folder
     */
    private File fParentFolder;
    
    /**
     * The new file
     */
    private File fNewFile;
    
    /**
     * The default extension to use. May be null
     */
    private String fDefaultExtension;
    
	/**
	 * Constructor
	 * @param parentFolder Parent File to add to
	 */
	public NewFileDialog(Shell shell, File parentFolder) {
	    fShell = shell;
	    fParentFolder = parentFolder;
	}
	
	/**
	 * Set the default file extension to use in case user does not provide onw
	 * @param extension with the "." if one is required
	 */
	public void setDefaultExtension(String extension) {
	    fDefaultExtension = extension;
	}
	
    /**
     * @return The new File or null if not set
     */
    public File getFile() {
        return fNewFile;
    }
	
    /**
     * Throw up a dialog asking for a Resource Group name
     * @return True if the user entered valid input, false if cancelled
     */
    public boolean open() {
        InputDialog dialog = new InputDialog(fShell,
                Messages.NewFileDialog_0,
                Messages.NewFileDialog_1,
                "", //$NON-NLS-1$
                new InputValidator());
        
        int code = dialog.open();
        
        if(code == Window.OK) {
            String s = dialog.getValue();
            s = getNameWithDefaultExtension(s);
            fNewFile = new File(fParentFolder, s);
            return true;
        }
        else {
            return false;
        }
    }
    
    private String getNameWithDefaultExtension(String name) {
        if(fDefaultExtension != null && !name.contains(".") && !name.endsWith(fDefaultExtension)) { //$NON-NLS-1$
            name += fDefaultExtension;
        }
        
        return name;
    }
    
    /**
     * Validate user input
     */
    private class InputValidator implements IInputValidator {

        @Override
        public String isValid(String newText) {
            newText = getNameWithDefaultExtension(newText);
            
            if("".equals(newText.trim())) { //$NON-NLS-1$
                return Messages.NewFileDialog_2;
            }
            
            File file = new File(fParentFolder, newText);
            if(file.exists()) {
                return Messages.NewFileDialog_3;
            }
            
            // This will ensure non-legal filename characters are disallowed
            try {
                FileSystems.getDefault().getPath(newText);
            }
            catch(InvalidPathException ex) {
                return Messages.NewFileDialog_4;
            }

            return null;
        }
    }
}
