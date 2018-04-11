/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.IOException;


/**
 * Archi Utils Class
 * 
 * @author Phillip Beauvoir
 */
public class A {

    /**
     * Open a model from file and return the AModel
     * @param file
     * @return The AModel
     * @throws IOException 
     */
    public AModel openModel(String file) throws IOException {
        return new AModel(new File(file));
    }
	
    
}
