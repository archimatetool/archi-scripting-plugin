/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModelObject;
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
        super("property", (IArchimateModelObject)eObject); //$NON-NLS-1$
        this.eObject = eObject;

        // TODO use IArchimateFactory.eINSTANCE.createProperty(key, value);
        property = IArchimateFactory.eINSTANCE.createProperty();
        property.setKey(key);
        property.setValue(value);
    }

    @Override
    public void undo() {
        eObject.getProperties().remove(property);
    }

    @Override
    public void perform() {
        eObject.getProperties().add(property);
    }
}
