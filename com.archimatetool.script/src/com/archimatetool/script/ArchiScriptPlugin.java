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
import org.eclipse.ui.IPartService;
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
                @Override
                public void run() {
                    IPartService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService();
                    service.addPartListener(ArchiScriptPlugin.this);
                    
                    // Initialise with active part
                    partActivated(service.getActivePart());
                }
            });
        }
    }
    
    
    
    // Track current workbench part to get the current selection
    // We have to do it this way because if the Script is run from the Scripts Manager View that will be the selection
    
    private IWorkbenchPart currentPart;

    @Override
    public void partActivated(IWorkbenchPart part) {
        if(part instanceof ITreeModelView || part instanceof IDiagramModelEditor) {
            currentPart = part;
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        // Tricky logic.
        // Only set this to null if the part being closed is the current part
        if(part == currentPart) {
            currentPart = null;
        }
    }

    @Override
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
