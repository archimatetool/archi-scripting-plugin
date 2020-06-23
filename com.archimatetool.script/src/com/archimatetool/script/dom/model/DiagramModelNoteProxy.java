/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.script.ArchiScriptException;
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
    
    public DiagramModelObjectProxy setBorderType(int type) {
        if(type != IDiagramModelNote.BORDER_DOGEAR
                && type != IDiagramModelNote.BORDER_RECTANGLE
                && type != IDiagramModelNote.BORDER_NONE) {
            throw new ArchiScriptException(Messages.DiagramModelNoteProxy_0);
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
            case TEXT:
                return getText();
            case BORDER_TYPE:
                return getBorderType();
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
            case BORDER_TYPE:
                if(value instanceof Integer) {
                    setBorderType((int)value);
                }
                break;
        }
        
        return super.attr(attribute, value);
    }
    
    
}
