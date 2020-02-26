/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.archimatetool.editor.model.commands.EObjectFeatureCommand;

/**
 * SetCommand
 * 
 * @author Phillip Beauvoir
 */
public class SetCommand extends ScriptCommandWrapper {

    public SetCommand(EObject eObject, EStructuralFeature feature, Object newValue) {
        super(new EObjectFeatureCommand("Script", eObject, feature, newValue), eObject); //$NON-NLS-1$
    }
}