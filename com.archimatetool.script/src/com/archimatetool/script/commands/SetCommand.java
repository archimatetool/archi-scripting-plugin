/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.archimatetool.editor.model.commands.EObjectFeatureCommand;
import com.archimatetool.editor.model.commands.FeatureCommand;
import com.archimatetool.model.IFeatures;

/**
 * Set Command is a ScriptCommandWrapper wrapping two types of Command: EObjectFeatureCommand and FeatureCommand
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class SetCommand extends ScriptCommandWrapper {

    public SetCommand(EObject eObject, EStructuralFeature feature, Object newValue) {
        super(new EObjectFeatureCommand("Script", eObject, feature, newValue), eObject);
    }
    
    public SetCommand(IFeatures featuresObject, String name, Object value, Object defaultValue) {
        super(new FeatureCommand("Script", featuresObject, name, value == null ? "" : value, defaultValue == null ? "" : defaultValue), featuresObject);
    }
}