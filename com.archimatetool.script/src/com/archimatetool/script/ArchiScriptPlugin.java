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

import com.archimatetool.editor.diagram.IDiagramModelEditor;
import com.archimatetool.editor.ui.components.PartListenerAdapter;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.editor.views.tree.ITreeModelView;
import com.archimatetool.script.preferences.IPreferenceConstants;



/**
 * Activitor
 * 
 * @author Phillip Beauvoir
 */
public class ArchiScriptPlugin extends AbstractUIPlugin {

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
    
    // Track current workbench part to get the current selection
    // We have to capture the latest selection before a script is run otherwise the Script Manager will have the focus
    
    private IWorkbenchPart currentPart;
    
    private IPartListener partListener = new PartListenerAdapter() {
        @Override
        public void partActivated(IWorkbenchPart part) {
            if(part instanceof ITreeModelView || part instanceof IDiagramModelEditor) {
                currentPart = part;
            }
        }

        @Override
        public void partClosed(IWorkbenchPart part) {
            // Only set this to null if the part being closed is the current part
            if(part == currentPart) {
                currentPart = null;
            }
        }
    };
    
    void registerPartListener() {
        // Only if Platform UI is running
        if(PlatformUI.isWorkbenchRunning()) {
            // This has to run in the UI thread
            Display.getDefault().syncExec(() -> {
                IPartService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService();
                service.addPartListener(partListener);

                // Initialise with active part
                partListener.partActivated(service.getActivePart());
            });
        }
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
