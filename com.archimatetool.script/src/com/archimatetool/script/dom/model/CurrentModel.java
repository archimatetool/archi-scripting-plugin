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
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.WorkbenchPartTracker;
import com.archimatetool.script.dom.IArchiScriptBinding;

/**
 * This represents the current model, or "model" dom object.
 * 
 * This can be the selected model in focus if run in the UI
 * or just the current model in the script
 * or the current model if run from the ACLI
 * 
 * @author Phillip Beauvoir
 */
public class CurrentModel extends ArchimateModelProxy implements IArchiScriptBinding {
    
    private static CurrentModel instance;
    
    /**
     * Set the singleton instance's underlying model to the one in modelProxy
     */
    static void setAsCurrentModel(ArchimateModelProxy modelProxy) {
        instance.setEObject(modelProxy.getEObject());
    }
    
    public CurrentModel() {
        super(null);
        
        instance = this;
        
        // If the workbench is running determine if there is an active part containing an IArchimateModel we can set this to
        if(PlatformUI.isWorkbenchRunning()) {
            IWorkbenchPart activePart = WorkbenchPartTracker.INSTANCE.getActivePart();
            
            // Set model
            if(activePart != null) {
                setEObject(activePart.getAdapter(IArchimateModel.class));
            }
        }
        // Else, if we are running in CLI mode, get the Current Model if there is one
        else {
            setEObject(CommandLineState.getModel());
        }
    }
    
    /**
     * @return true if the current model is set
     */
    public boolean isSet() {
        return super.getEObject() != null;
    }
    
    @Override
    protected IArchimateModel getEObject() {
        // Throw this exception rather than a NPE if the current model has not been set
        IArchimateModel model = super.getEObject();
        if(model == null) {
            throw new ArchiScriptException(Messages.CurrentModel_0);
        }
        return model;
    }
    
    @Override
    public void dispose() {
        instance = null;    // Definitely need to set this to null for both Nashorn and GraalVM
        setEObject(null);   // Set this to null because of a Nashorn memory leak
    }
}
