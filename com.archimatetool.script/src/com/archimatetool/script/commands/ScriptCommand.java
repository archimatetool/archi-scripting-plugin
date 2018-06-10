/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;

/**
 * ScriptCommand
 * 
 * @author Phillip Beauvoir
 */
public abstract class ScriptCommand extends Command {
    private EObject fEObject;
    private IArchimateModel fModel;

    public ScriptCommand(String name, EObject eObject) {
        super(name);
        fEObject = eObject;
        
        if(eObject instanceof IArchimateModelObject) {
            fModel = ((IArchimateModelObject)eObject).getArchimateModel();
        }
        else {
            while(fModel == null) {
                eObject = eObject.eContainer();
                if(eObject instanceof IArchimateModelObject) {
                    fModel = ((IArchimateModelObject)eObject).getArchimateModel();
                }
            }
        }
    }
    
    public EObject getEObject() {
        return fEObject;
    }
    
    public IArchimateModel getModel() {
        return fModel;
    }

    public abstract void perform();
    
    @Override
    public void execute() {
    }
    
    @Override
    public void redo() {
        perform();
    }
    
    @Override
    public void dispose() {
        fEObject = null;
    }
}