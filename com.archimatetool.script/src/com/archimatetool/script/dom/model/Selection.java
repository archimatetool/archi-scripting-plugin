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

import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.dom.IArchiScriptDOMFactory;

/**
 * Selection dom object
 * 
 * Represents a collection of currently selected EObjects in the UI (models tree)
 * If Archi is not running an empty collection is returned
 * 
 * @author Phillip Beauvoir
 */
public class Selection implements IArchiScriptDOMFactory {
    
    @Override
    public Object getDOMroot() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(PlatformUI.isWorkbenchRunning()) {
            ISelection selection = ArchiScriptPlugin.INSTANCE.getCurrentSelection();
            
            if(selection instanceof IStructuredSelection) {
                for(Object o : ((IStructuredSelection)selection).toArray()) {
                    
                    if(o instanceof EditPart) {
                        o = ((EditPart)o).getModel();
                    }
                    else if(o instanceof IAdaptable) {
                        o = ((IAdaptable)o).getAdapter(EObject.class);
                    }
                    
                    if(o instanceof EObject) {
                        EObjectProxy proxy = EObjectProxy.get((EObject)o);
                        if(proxy != null) {
                            list.add(proxy);
                        }
                    }
                }
            }
        }
        
        return list;
    }

}
