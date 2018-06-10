/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;

/**
 * SetCommand
 * 
 * @author Phillip Beauvoir
 */
public class SetCommand extends ScriptCommand {
    protected EStructuralFeature fFeature;
    protected Object fOldValue;
    protected Object fNewValue;

    public SetCommand(IArchimateModelObject eObject, EStructuralFeature feature, Object newValue) {
        this(eObject.getArchimateModel(), eObject, feature, newValue);
    }

    public SetCommand(IArchimateModel model, EObject eObject, EStructuralFeature feature, Object newValue) {
        super("Script", model, eObject); //$NON-NLS-1$
        fFeature = feature;
        fOldValue = getEObject().eGet(feature);
        fNewValue = newValue;
    }
    
    @Override
    public void perform() {
        getEObject().eSet(fFeature, fNewValue);
    }
    
    @Override
    public void undo() {
        getEObject().eSet(fFeature, fOldValue);
    }
    
}