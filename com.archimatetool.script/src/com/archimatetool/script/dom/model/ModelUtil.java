/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.DiagramEditorInput;
import com.archimatetool.editor.diagram.IDiagramModelEditor;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IDiagramModel;
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
    
    public static void refreshEditor(IDiagramModel dm) {
        if(PlatformUI.isWorkbenchRunning()) {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            for(IEditorReference ref : page.getEditorReferences()) {
                try {
                    IEditorInput input = ref.getEditorInput();
                    if(input instanceof DiagramEditorInput && ((DiagramEditorInput)input).getDiagramModel() == dm) {
                        IDiagramModelEditor editor = (IDiagramModelEditor)ref.getEditor(false);
                        if(editor != null) {
                            editor.getGraphicalViewer().setContents(editor.getModel());
                        }
                    }
                }
                catch(PartInitException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}