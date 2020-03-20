/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * AddPropertyCommand
 * 
 * @author Phillip Beauvoir
 */
public class AddPropertyCommand extends ScriptCommand {
   
    private IProperties eObject;
    private IProperty property;

    public AddPropertyCommand(IProperties eObject, String key, String value) {
        super("property", eObject); //$NON-NLS-1$
        this.eObject = eObject;
        property = IArchimateFactory.eINSTANCE.createProperty(key, value);
    }

    @Override
    public void undo() {
        eObject.getProperties().remove(property);
    }

    @Override
    public void perform() {
        eObject.getProperties().add(property);
    }
    
    @Override
    public void dispose() {
        eObject = null;
        property = null;
    }

}
