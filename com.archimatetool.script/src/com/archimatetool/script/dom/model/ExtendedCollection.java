/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.INameable;


/**
 * Extended Collection
 * 
 * @author Phillip Beauvoir
 */
public class ExtendedCollection<T> extends ArrayList<T> {
    
    public ExtendedCollection() {
        super();
    }
    
    public T val() {
        return isEmpty() ? null : get(0);
    }
    
    public List<String> getName() {
        List<String> list = new ArrayList<>();
        
        for(T object : this) {
            if(object instanceof INameable) {
                list.add(((INameable)object).getName());
            }
        }
        
        return list;
    }
    
    public List<String> getDocumentation() {
        List<String> list = new ArrayList<>();
        
        for(T object : this) {
            if(object instanceof IDocumentable) {
                list.add(((IDocumentable)object).getDocumentation());
            }
        }
        
        return list;
    }

}
