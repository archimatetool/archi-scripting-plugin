/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IDiagramModelNote;

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
        getEObject().setContent(StringUtils.safeString(text));
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
