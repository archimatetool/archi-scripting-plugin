/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.List;


/**
 * Archi global binding Class to bind "$" functions to JS global "this" object
 * 
 * @author Phillip Beauvoir
 */
public class GlobalBinding {
    
    public List<?> $() {
        return Model.MODEL_INSTANCE.$("all"); //$NON-NLS-1$
    }
    
    public Object $(Object object) {
        // String is a selector on the model, so return a collection
        if(object instanceof String) {
            return Model.MODEL_INSTANCE.$((String)object);
        }
        
        // Else just return the same object
        return object;
    }
}
