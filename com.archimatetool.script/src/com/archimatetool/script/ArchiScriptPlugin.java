/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.archimatetool.editor.diagram.IDiagramModelEditor;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.editor.views.tree.ITreeModelView;
import com.archimatetool.script.preferences.IPreferenceConstants;



/**
 * Activitor
 * 
 * @author Phillip Beauvoir
 */
public class ArchiScriptPlugin extends AbstractUIPlugin implements IPartListener {

    public static final String PLUGIN_ID = "com.archimatetool.script"; //$NON-NLS-1$
    
    /**
     * The shared instance
     */
    public static ArchiScriptPlugin INSTANCE;

    public ArchiScriptPlugin() {
        INSTANCE = this;
    }

    /**
     * @return The folder where we store user scripts
     */
    public File getUserScriptsFolder() {
        // Get from preferences
        String path = getPreferenceStore().getString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER);
        
        if(StringUtils.isSet(path)) {
            return new File(path);
        }
        
        // Default
        path = getPreferenceStore().getDefaultString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER);
        return new File(path);
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        if(PlatformUI.isWorkbenchRunning()) {
            // This needs to be on a thread
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(ArchiScriptPlugin.this);
                }
            });
        }
    }
    
    
    
    // Track current workbench parts
    private IWorkbenchPart currentPart;

    public void partActivated(IWorkbenchPart part) {
        if(part instanceof ITreeModelView || part instanceof IDiagramModelEditor) {
            currentPart = part;
        }
    }

    public void partDeactivated(IWorkbenchPart part) {
    }

    public void partBroughtToTop(IWorkbenchPart part) {
    }

    public void partClosed(IWorkbenchPart part) {
        if(part instanceof ITreeModelView || part instanceof IDiagramModelEditor) {
            currentPart = null;
        }
    }

    public void partOpened(IWorkbenchPart part) {
    }

    public ISelection getCurrentSelection() {
        if(currentPart != null) {
            return currentPart.getSite().getSelectionProvider().getSelection();
        }
        
        return null;
    }
    
    public IWorkbenchPart getActivePart() {
        return currentPart;
    }
}
