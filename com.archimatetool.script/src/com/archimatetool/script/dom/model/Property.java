/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

/**
 * Key/Value pair
 * 
 * @author Phillip Beauvoir
 */
public class Property {
    private String key;
    private String value;

    public Property(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey() {
        return key;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return key + ": " + value; //$NON-NLS-1$
    }
}