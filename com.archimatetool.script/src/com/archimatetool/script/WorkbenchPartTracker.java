/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.IDiagramModelEditor;
import com.archimatetool.editor.ui.components.PartListenerAdapter;
import com.archimatetool.editor.views.tree.ITreeModelView;

/**
 * Track current workbench part to get the current selection
 * We have to capture the latest selection before a script is run otherwise the Script Manager will have the focus
 * 
 * @author Phillip Beauvoir
 */
public class WorkbenchPartTracker {
    
    public static WorkbenchPartTracker INSTANCE = new WorkbenchPartTracker();
    
    private IWorkbenchPart currentPart;
    
    private WorkbenchPartTracker() {
        registerPartListener();
    }
    
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
    
    private void registerPartListener() {
        // Only if Platform UI is running
        if(PlatformUI.isWorkbenchRunning()) {
            IPartService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService();
            service.addPartListener(partListener);

            // Initialise with active part
            partListener.partActivated(service.getActivePart());
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
