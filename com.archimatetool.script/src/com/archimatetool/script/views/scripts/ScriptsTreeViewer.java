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

import com.archimatetool.script.views.FileLabelProvider;
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
        
        // Drag support
        new ScriptsTreeViewerDragDropHandler(this);
    }
    
    @Override
    protected IBaseLabelProvider getUserLabelProvider() {
        return new FileTreeLabelProvider() {
            FileLabelProvider fileLabelProvider = new FileLabelProvider();
            
            @Override
            public Image getImage(File file) {
                return fileLabelProvider.getImage(file);
            }
            
            @Override
            public String getText(File file) {
                return fileLabelProvider.getText(file);
            }
            
            @Override
            public String getToolTipText(Object element) {
                return fileLabelProvider.getToolTipText(element);
            }
        };
    }
}
