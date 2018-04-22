/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.commandline.CommandLineState;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.dom.IArchiScriptDOMFactory;

/**
 * Current model object
 * 
 * Represents the current model
 * If Archi is not running an empty collection is returned
 * 
 * @author Phillip Beauvoir
 */
public class Model implements IArchiScriptDOMFactory {
    
    /*
     * Use a static instance and bind it to "model".
     * Note that this cannot be re-bound during JS execution, but the underlying IArchimateModel can be changed.
     */
    static ArchimateModelProxy MODEL_INSTANCE;
    
    public Object getDOMroot() {
        // Default is null, no current model
        IArchimateModel currentModel = null;
        
        // Get and wrap the currently selected model in the UI if there is one
        if(PlatformUI.isWorkbenchRunning()) {
            IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart();
            if(part != null) {
                currentModel = part.getAdapter(IArchimateModel.class);
            }
        }
        // Else, if we are running in CLI mode, get the Current Model if there is one
        else {
            currentModel = CommandLineState.getModel();
        }
        
        MODEL_INSTANCE = new ArchimateModelProxy(currentModel);
        return MODEL_INSTANCE;
    }

}
