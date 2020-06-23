/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;

import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.script.ScriptFiles;

/**
 * Mark Folder As Hidden in menu Action
 */
public class MarkFolderHiddenAction extends Action {

    private List<File> selectedFolders;
    
    MarkFolderHiddenAction() {
    }
    
    void setSelection(Object[] selection) {
        selectedFolders = new ArrayList<File>();
        
        for(Object object : selection) {
            if(object instanceof File && ((File)object).isDirectory() && ((File)object).exists()) {
                selectedFolders.add((File)object);
            }
        }
        
        setEnabled(!selectedFolders.isEmpty());
        updateText();
    }
    
    boolean shouldShow(Object[] objects) {
        // If we have a file selected, return false
        for(Object object : objects) {
            if(object instanceof File && !((File)object).isDirectory()) {
                return false;
            }
        }
        
        return true;
    }
    
    private void updateText() {
        setText(canSetHidden() ? Messages.MarkFolderHiddenAction_1 : Messages.MarkFolderHiddenAction_0);
    }
    
    private boolean canSetHidden() {
        for(File folder : selectedFolders) {
            File hiddenFile = getHiddenFile(folder);
            if(hiddenFile.exists()) {
                return false;
            }
        }
        
        return true;
    }
    
    private File getHiddenFile(File folder) {
        return new File(folder, ScriptFiles.HIDDEN_MARKER_FILE);
    }
    
    @Override
    public void run() {
        boolean setHidden = canSetHidden();
        
        for(File folder : selectedFolders) {
            File hiddenFile = getHiddenFile(folder);

            if(setHidden) {
                if(!hiddenFile.exists()) {
                    try {
                        hiddenFile.createNewFile();
                        if(PlatformUtils.isWindows()) { // Windows, set hidden attribute
                            Files.setAttribute(hiddenFile.toPath(), "dos:hidden", true); //$NON-NLS-1$
                        }
                    }
                    catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else if(hiddenFile.exists()) {
                hiddenFile.delete();
            }
        }

        updateText();
    }
}