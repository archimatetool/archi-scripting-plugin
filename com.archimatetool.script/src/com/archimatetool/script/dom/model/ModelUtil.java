/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;

/**
 * Model Utils
 * 
 * @author Phillip Beauvoir
 */
public class ModelUtil {
    
    private ModelUtil() {
    }
    
    /**
     * @param folder
     * @param concept
     * @return true if the given parent folder is the correct folder to contain this concept
     */
    public static boolean isCorrectFolderForConcept(IFolder folder, IArchimateConcept concept) {
        IFolder topFolder = folder.getArchimateModel().getDefaultFolderForObject(concept);
        if(folder == topFolder) {
            return true;
        }
        
        EObject e = folder;
        while((e = e.eContainer()) != null) {
            if(e == topFolder) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * If a DiagramModelComponent needs a refresh in a View, this does the trick/
     * It simply deletes the model component and adds it again causing a MVC refresh
     * @param dmc
     */
    public static void refreshDiagramModelComponent(IDiagramModelComponent dmc) {
        if(PlatformUI.isWorkbenchRunning()) {
            IDiagramModelContainer parent = (IDiagramModelContainer)dmc.eContainer();
            if(parent != null) {
                if(dmc instanceof IDiagramModelObject) {
                    int index = parent.getChildren().indexOf(dmc);
                    parent.getChildren().remove(dmc);
                    parent.getChildren().add(index, (IDiagramModelObject)dmc);
                }
                else if(dmc instanceof IDiagramModelConnection) {
                    ((IDiagramModelConnection)dmc).disconnect();
                    ((IDiagramModelConnection)dmc).reconnect();
                }
            }
        }
    }
}