/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.SetCommand;

/**
 * Diagram Model Note wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelGroupProxy extends DiagramModelObjectProxy {
    
    DiagramModelGroupProxy(IDiagramModelGroup object) {
        super(object);
    }
    
    @Override
    protected IDiagramModelGroup getEObject() {
        return (IDiagramModelGroup)super.getEObject();
    }
    
    public DiagramModelObjectProxy setBorderType(int type) {
        if(type != IDiagramModelGroup.BORDER_TABBED
                && type != IDiagramModelGroup.BORDER_RECTANGLE) {
            throw new ArchiScriptException(Messages.DiagramModelGroupProxy_0);
        }

        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.BORDER_TYPE__BORDER_TYPE, type));
        return this;
    }
    
    public Object getBorderType() {
        return getEObject().getBorderType();
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case BORDER_TYPE:
                return getBorderType();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case BORDER_TYPE:
                if(value instanceof Integer) {
                    setBorderType((int)value);
                }
                break;
        }
        
        return super.attr(attribute, value);
    }
    
    
}
