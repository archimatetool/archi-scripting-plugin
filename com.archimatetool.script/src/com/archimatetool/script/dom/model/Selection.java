/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.script.WorkbenchPartTracker;
import com.archimatetool.script.dom.IArchiScriptBinding;

/**
 * The "selection" dom object
 * 
 * Represents a collection of currently selected EObjects in the UI (Models Tree or a Diagram)
 * If Archi is not running, an empty collection is returned
 * 
 * @author Phillip Beauvoir
 */
public class Selection extends EObjectProxyCollection implements IArchiScriptBinding {
    
    public Selection() {
        if(PlatformUI.isWorkbenchRunning()) {
            ISelection selection = WorkbenchPartTracker.INSTANCE.getCurrentSelection();
            
            if(selection instanceof IStructuredSelection structuredSelection) {
                for(Object o : structuredSelection.toArray()) {
                    
                    // Check this first!
                    // If it's an EditPart then get the diagram model component
                    if(o instanceof EditPart editPart) {
                        o = editPart.getModel();
                    }
                    // Else this...which I don't think will ever happen
                    else if(o instanceof IAdaptable adaptable) {
                        o = adaptable.getAdapter(EObject.class);
                    }
                    
                    if(o instanceof EObject eObject) {
                        EObjectProxy proxy = EObjectProxy.get(eObject);
                        if(proxy != null) {
                            add(proxy);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void dispose() {
        clear(); // Set this to null because of possible Nashorn memory leak. GraalVM doesn't need it.
    }
}
