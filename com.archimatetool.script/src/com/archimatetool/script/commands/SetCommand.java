/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * SetCommand
 * 
 * @author Phillip Beauvoir
 */
public class SetCommand extends ScriptCommand {
    private EStructuralFeature feature;
    private Object oldValue;
    private Object newValue;
    private EObject eObject;

    public SetCommand(EObject eObject, EStructuralFeature feature, Object newValue) {
        super("Script", eObject); //$NON-NLS-1$
        this.feature = feature;
        this.eObject = eObject;
        oldValue = eObject.eGet(feature);
        this.newValue = newValue;
    }
    
    @Override
    public void perform() {
        eObject.eSet(feature, newValue);
    }
    
    @Override
    public void undo() {
        eObject.eSet(feature, oldValue);
    }
    
    @Override
    public boolean canExecute() {
        return (newValue != null) ? !newValue.equals(oldValue)
                : (oldValue != null) ? !oldValue.equals(newValue)
                : false;
    }

    @Override
    public void dispose() {
        eObject = null;
    }
}