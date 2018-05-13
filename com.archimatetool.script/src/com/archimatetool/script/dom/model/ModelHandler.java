/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.script.ArchiScriptException;

/**
 * Handles Models that are open in the UI
 * 
 * @author Phillip Beauvoir
 */
public class ModelHandler {
    
    private static List<File> closedModels;

    private ModelHandler() {
    }
    
    public static void init() {
        closedModels = new ArrayList<>();
    }
    
    public static void openModels() {
    	if(closedModels != null) {
	        for(File file : closedModels) {
	            IEditorModelManager.INSTANCE.openModel(file);
	        }
    	}
    }
    
    static void checkModelAccess(EObject eObject) {
        // Only check if running in UI
        if(!PlatformUI.isWorkbenchRunning() || !(eObject instanceof IArchimateModelObject)) {
            return;
        }
        
        IArchimateModel model = ((IArchimateModelObject)eObject).getArchimateModel();
        
        // If the model is loaded in the UI...
        if(model != null && IEditorModelManager.INSTANCE.isModelLoaded(model.getFile())) {
            // It's Dirty so no access
            if(IEditorModelManager.INSTANCE.isModelDirty(model)) {
                throw new ArchiScriptException(Messages.ModelHandler_0 + ": " + model.getFile()); //$NON-NLS-1$
            }
            
            // (Partially) close the model so we can retain the ID Adapter and Archive Manager
            EditorManager.closeDiagramEditors(model);
            IEditorModelManager.INSTANCE.getModels().remove(model);
            IEditorModelManager.INSTANCE.firePropertyChange(IEditorModelManager.INSTANCE, IEditorModelManager.PROPERTY_MODEL_REMOVED, null, model);

            // And add to the list of files that will be re-opened in finalise()
            if(!closedModels.contains(model.getFile())) {
                closedModels.add(model.getFile());
            }
        }
    }
}