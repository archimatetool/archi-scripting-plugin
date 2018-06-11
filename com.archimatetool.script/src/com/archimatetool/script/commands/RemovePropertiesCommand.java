/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.commands;

import java.util.List;

import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;

/**
 * RemovePropertiesCommand
 * 
 * @author Phillip Beauvoir
 */
public class RemovePropertiesCommand extends ScriptCommand {
   
    private IProperties eObject;
    private List<IProperty> toRemove;

    public RemovePropertiesCommand(IProperties eObject, List<IProperty> toRemove) {
        super("properties", eObject); //$NON-NLS-1$
        this.eObject = eObject;
        this.toRemove = toRemove;
    }

    @Override
    public void undo() {
        eObject.getProperties().addAll(toRemove);
    }

    @Override
    public void perform() {
        eObject.getProperties().removeAll(toRemove);
    }
    
    @Override
    public void dispose() {
        toRemove = null;
        eObject = null;
    }
}
