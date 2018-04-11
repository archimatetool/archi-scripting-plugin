/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.dom.model.AModel;

/**
 * Represents the UI dom object
 */
public class UI {
    
    public UI() {
    }
    
    /**
     * @return An array of currently selected EObjects in the UI
     * The array can be empty.
     */
    public EObject[] getSelectedEObjects() {
        ISelection selection = null;
        
        if(PlatformUI.isWorkbenchRunning()) {
            selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
        }
        
        List<EObject> list = new ArrayList<EObject>();
        
        if(selection instanceof IStructuredSelection) {
            for(Object o : ((IStructuredSelection)selection).toArray()) {
                
                if(o instanceof IAdaptable) {
                    o = ((IAdaptable)o).getAdapter(EObject.class);
                }
                
                if(o instanceof EObject) {
                    list.add((EObject)o);
                }
            }
        }
        
        return list.toArray(new EObject[list.size()]);
    }
    
    /**
     * Open a model from file in the UI and return the AModel
     * @param file
     * @return The AModel
     */
    public AModel openModel(String file) {
        if(PlatformUI.isWorkbenchRunning()) {
            return new AModel(IEditorModelManager.INSTANCE.openModel(new File(file)));
        }
        
        return null;
    }
    
    /**
     * Open a given model in the UI and return the AModel
     * @param model
     * @return The AModel
     */
    public AModel openModel(IArchimateModel model) {
        if(PlatformUI.isWorkbenchRunning()) {
            IEditorModelManager.INSTANCE.openModel(model);
            return new AModel(model);
        }
        
        return null;
    }

}
