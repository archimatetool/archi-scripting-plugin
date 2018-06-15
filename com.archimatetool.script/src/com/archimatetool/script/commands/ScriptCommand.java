/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IArchimateModel;

/**
 * ScriptCommand
 * 
 * @author Phillip Beauvoir
 */
public abstract class ScriptCommand extends Command {
    private IArchimateModel model;

    protected ScriptCommand(String name, IArchimateModel model) {
        super(name);
        this.model = model;
    }

    protected ScriptCommand(String name, EObject eObject) {
        super(name);
        setModel(eObject);
    }

    protected ScriptCommand(String name) {
        super(name);
    }
    
    protected void setModel(EObject eObject) {
        while(!(eObject instanceof IArchimateModel) && eObject != null) {
            eObject = eObject.eContainer();
        }
        model = (IArchimateModel)eObject;
    }
    
    public IArchimateModel getModel() {
        return model;
    }

    public abstract void perform();
    
    @Override
    public final void execute() {
        // Do nothing. Do not use! Use perform() instead.
    }
    
    @Override
    public void redo() {
        perform();
    }
    
    @Override
    public void dispose() {
        model = null;
    }
}