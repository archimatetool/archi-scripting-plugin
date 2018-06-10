/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;

/**
 * ScriptCommand
 * 
 * @author Phillip Beauvoir
 */
public abstract class ScriptCommand extends Command {
    private EObject fEObject;

    public ScriptCommand(String name, EObject eObject) {
        super(name);
        fEObject = eObject;
    }
    
    public EObject getEObject() {
        return fEObject;
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