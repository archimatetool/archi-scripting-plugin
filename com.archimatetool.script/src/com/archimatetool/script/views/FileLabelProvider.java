/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views;

import java.io.File;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.script.IArchiScriptImages;
import com.archimatetool.script.IScriptEngineProvider;
import com.archimatetool.script.ScriptFiles;


/**
 * Label Provider for files and folders
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class FileLabelProvider implements ILabelProvider {
    
    @Override
    public Image getImage(Object element) {
        File file = getFile(element);
        if(file == null) {
            return null;
        }
        
        if(file.isDirectory()) {
            return IArchiScriptImages.ImageFactory.getImage(IArchiScriptImages.ICON_FOLDER);
        }

        // If we have a provider get the provider's image or use default file image
        Image image = IScriptEngineProvider.INSTANCE.getProviderForFile(file)
                                                    .map(IScriptEngineProvider::getImage)
                                                    .orElse(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));

        // If it's a linked file...
        if(ScriptFiles.isLinkedFile(file)) {
            // If the linked file exists add the link overlay
            if(ScriptFiles.resolveLinkFile(file).exists()) {
                return IArchiScriptImages.ImageFactory.getOverlayImage(image,
                        IArchiScriptImages.ICON_LINK_OVERLAY, IDecoration.BOTTOM_RIGHT);
            }

            // Else add the warning overlay
            return IArchiScriptImages.ImageFactory.getOverlayImage(image,
                    IArchiScriptImages.ICON_LINK_WARN_OVERLAY, IDecoration.BOTTOM_RIGHT);
        }

        return image;
    }

    @Override
    public String getText(Object element) {
        File file = getFile(element);
        if(file == null) {
            return " ";
        }
        
        // If this is a script file or a linked file get file name without extension
        if(ScriptFiles.isScriptFile(file) || ScriptFiles.isLinkedFile(file)) {
            return FileUtils.getFileNameWithoutExtension(file);
        }

        return file.getName();
    }
    
    public String getToolTipText(Object element) {
        File file = getFile(element);
        if(file == null) {
            return null;
        }
        
        if(ScriptFiles.isLinkedFile(file)) {
            file = ScriptFiles.resolveLinkFile(file);
        }
        
        return file.getAbsolutePath();
    }
    
    private File getFile(Object element) {
        return element instanceof File file ? file
                      : element instanceof IStructuredSelection selection && selection.getFirstElement() instanceof File file ? file
                      : null;
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }
    
    @Override
    public void dispose() {
    }
}
