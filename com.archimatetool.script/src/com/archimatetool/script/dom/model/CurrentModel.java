/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.commandline.CommandLineState;
import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.editor.views.tree.ITreeModelView;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.dom.IArchiScriptDOMFactory;

/**
 * Current model object
 * 
 * Represents the current model
 * This can be the selected model in focus if run in the UI, or the current model if run from the ACLI
 * It can be loaded from file, or created anew.
 * 
 * @author Phillip Beauvoir
 */
public class CurrentModel implements IArchiScriptDOMFactory {
    
    static ArchimateModelProxy INSTANCE = new ArchimateModelProxy(null) {
        @Override
        protected IArchimateModel getEObject() {
            // Throw this exception rather than a NPE if current model has not been set
            IArchimateModel model = super.getEObject();
            if(model == null) {
                throw new ArchiScriptException(Messages.CurrentModel_0);
            }
            return model;
        }
    };
    
    @Override
    public Object getDOMroot() {
        // Get and wrap the currently selected model in the UI if there is one
        // Note that this *can* be null as we need to initialise the CurrentModel instance in all cases
        if(PlatformUI.isWorkbenchRunning()) {
            IWorkbenchPart activePart = ArchiScriptPlugin.INSTANCE.getActivePart();
            
            // Fallback to tree
            if(activePart == null) {
                activePart = ViewManager.findViewPart(ITreeModelView.ID);
            }
            
            if(activePart != null) {
                INSTANCE.setEObject(activePart.getAdapter(IArchimateModel.class));
            }
        }
        // Else, if we are running in CLI mode, get the Current Model if there is one
        else {
            INSTANCE.setEObject(CommandLineState.getModel());
        }
        
        return INSTANCE;
    }
    
}
