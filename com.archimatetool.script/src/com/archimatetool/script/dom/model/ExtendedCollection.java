/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Extended Collection
 * 
 * @author Phillip Beauvoir
 */
public class ExtendedCollection extends ArrayList<EObjectProxy> implements IModelConstants {
    
    public ExtendedCollection() {
        super();
    }
    
    public EObjectProxy val() {
        return isEmpty() ? null : get(0);
    }
    
    public List<?> getId() {
        return attr(ID);
    }

    public List<?> getName() {
        return attr(NAME);
    }
    
    public void setName(String name) {
        attr(NAME, name);
    }
    
    public List<?> getDocumentation() {
        return attr(DOCUMENTATION);
    }
    
    public void setDocumentation(String documentation) {
        attr(DOCUMENTATION, documentation);
    }
    
    public List<?> attr(String attribute) {
        List<Object> list = new ArrayList<>();
        
        for(EObjectProxy object : this) {
            Object attr = object.attr(attribute);
            if(attr != null) {
                list.add(attr);
            }
        }
        
        return list;
    }

    public void attr(String attribute, Object value) {
        for(EObjectProxy object : this) {
            object.attr(attribute, value);
        }
    }

}
