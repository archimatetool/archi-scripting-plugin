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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.DiagramEditorInput;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;

/**
 * Handles Models that are open in the UI
 * 
 * @author Phillip Beauvoir
 */
public class ModelHandler {
    
    private static List<File> closedModels;
    private static List<String> openEditorIDs;

    private ModelHandler() {
    }
    
    public static void init() {
        closedModels = new ArrayList<>();
        openEditorIDs = new ArrayList<>();
    }
    
    public static void openModels() {
    	if(closedModels != null) {
	        for(File file : closedModels) {
	            IArchimateModel model = IEditorModelManager.INSTANCE.openModel(file);
	            reopenEditors(model);
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
            
            // Store open editors (if any)
            openEditorIDs.addAll(getOpenDiagramModelIdentifiers(model));
            
            // Close open editors (if any)
            EditorManager.closeDiagramEditors(model);

            // Remove the model from the UI but retain it so we can use the model's ID Adapter and Archive Manager
            IEditorModelManager.INSTANCE.getModels().remove(model);
            IEditorModelManager.INSTANCE.firePropertyChange(IEditorModelManager.INSTANCE, IEditorModelManager.PROPERTY_MODEL_REMOVED, null, model);

            // And add to the list of files that will be re-opened in finalise()
            if(!closedModels.contains(model.getFile())) {
                closedModels.add(model.getFile());
            }
        }
    }
    
    /**
     * @param model
     * @return All open diagram models' ids so we can restore them
     */
    private static List<String> getOpenDiagramModelIdentifiers(IArchimateModel model) {
        List<String> list = new ArrayList<String>();
        
        for(IEditorReference ref : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences()) {
            try {
                IEditorInput input = ref.getEditorInput();
                if(input instanceof DiagramEditorInput) {
                    IDiagramModel dm = ((DiagramEditorInput)input).getDiagramModel();
                    if(dm.getArchimateModel() == model) {
                        list.add(dm.getId());
                    }
                }
            }
            catch(PartInitException ex) {
                ex.printStackTrace();
            }
        }
        
        return list;
    }
    
    /**
     * Re-open any diagram editors
     * @param model 
     */
    private static void reopenEditors(IArchimateModel model) {
        if(openEditorIDs != null) {
            for(String id : openEditorIDs) {
                EObject eObject = ArchimateModelUtils.getObjectByID(model, id);
                if(eObject instanceof IDiagramModel) {
                    EditorManager.openDiagramEditor((IDiagramModel)eObject);
                }
            }
        }
    }

}