/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.DiagramEditorInput;
import com.archimatetool.editor.diagram.IDiagramModelEditor;
import com.archimatetool.model.IDiagramModel;

/**
 * Handles Models that are open in the UI
 * 
 * @author Phillip Beauvoir
 */
public class ModelHandler {
    
    private ModelHandler() {
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