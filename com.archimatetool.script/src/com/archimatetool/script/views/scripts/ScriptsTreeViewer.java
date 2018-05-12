/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.scripts;

import java.io.File;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.IArchiScriptImages;
import com.archimatetool.script.views.file.FileTreeViewer;


/**
 * Scripts File Tree Viewer
 */
public class ScriptsTreeViewer extends FileTreeViewer {

    /**
     * Constructor
     */
    public ScriptsTreeViewer(File rootFolder, Composite parent) {
        super(rootFolder, parent);
    }
    
    @Override
    protected IBaseLabelProvider getUserLabelProvider() {
        return new ScriptsTreeLabelProvider(); 
    }
    
    
    // ===============================================================================================
	// ===================================== Label Provider ==========================================
	// ===============================================================================================

    protected class ScriptsTreeLabelProvider extends FileTreeLabelProvider {
        @Override
        public Image getImage(Object obj) {
            if(isScriptFile(obj)) {
                return IArchiScriptImages.ImageFactory.getImage(IArchiScriptImages.ICON_SCRIPT);
            }
            
            return super.getImage(obj);
        }
        
        @Override
        public String getText(Object obj) {
            if(isScriptFile(obj)) {
                return FileUtils.getFileNameWithoutExtension((File)obj);
            }
            
            return super.getText(obj);
        }
        
        private boolean isScriptFile(Object obj) {
            return obj instanceof File &&
                    ((File)obj).isFile() &&
                    ((File)obj).getName().toLowerCase().endsWith(ArchiScriptPlugin.SCRIPT_EXTENSION);
        }
    }
}
