/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.SetCommand;

/**
 * Diagram Model Note wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelNoteProxy extends DiagramModelObjectProxy {
    
    DiagramModelNoteProxy(IDiagramModelNote object) {
        super(object);
    }
    
    @Override
    protected IDiagramModelNote getEObject() {
        return (IDiagramModelNote)super.getEObject();
    }
    
    public void setText(String text) {
        CommandHandler.executeCommand(new SetCommand(getEObject(),
                IArchimatePackage.Literals.TEXT_CONTENT__CONTENT, StringUtils.safeString(text)));
    }
    
    public String getText() {
        return getEObject().getContent();
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case TEXT:
                return getText();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case TEXT:
                if(value instanceof String) {
                    setText((String)value);
                }
                break;
        }
        
        return super.attr(attribute, value);
    }
    
    
}
