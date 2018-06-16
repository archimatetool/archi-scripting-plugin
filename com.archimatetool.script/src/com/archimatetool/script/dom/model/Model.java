/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.commandline.CommandLineState;
import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.editor.views.tree.ITreeModelView;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.ArchiScriptPlugin;

/**
 * Current model object
 * 
 * Represents the current model
 * This can be the selected model in focus if run in the UI, or the current model if run from the ACLI
 * It can be loaded from file, or created anew.
 * 
 * @author Phillip Beauvoir
 */
public class Model extends ArchimateModelProxy {
    
    public Model() {
        // Get and wrap the currently selected model in the UI if there is one
        if(PlatformUI.isWorkbenchRunning()) {
            IWorkbenchPart activePart = ArchiScriptPlugin.INSTANCE.getActivePart();
            
            // Fallback to tree
            if(activePart == null) {
                activePart = ViewManager.findViewPart(ITreeModelView.ID);
            }
            
            if(activePart != null) {
                setEObject(activePart.getAdapter(IArchimateModel.class));
            }
        }
        // Else, if we are running in CLI mode, get the Current Model if there is one
        else {
            setEObject(CommandLineState.getModel());
        }
    }
    
    public ArchimateModelProxy create(String modelName) {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        model.setName(modelName);
        
        IArchiveManager archiveManager = IArchiveManager.FACTORY.createArchiveManager(model);
        model.setAdapter(IArchiveManager.class, archiveManager);
        
        // Don't add a CommandStack or other adapters. These will be created if openInUI() is called
        
        return new ArchimateModelProxy(model);
    }
    
    public ArchimateModelProxy load(String path) {
        File file = new File(path);
        
        // Already open in UI
        if(PlatformUI.isWorkbenchRunning()) {
            for(IArchimateModel model : IEditorModelManager.INSTANCE.getModels()) {
                if(file.equals(model.getFile())) {
                    return new ArchimateModelProxy(model);
                }
            }
        }
        
        // Else load from file
        IArchimateModel model = IEditorModelManager.INSTANCE.loadModel(file);
        if(model != null) {
            return new ArchimateModelProxy(model);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_2, path));
    }
    
    public ArchimateModelProxy setCurrent(ArchimateModelProxy current) {
        setEObject(current.getEObject());
        return this;
    }
}
