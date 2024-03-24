/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.dom.DomExtensionFactory;


/**
 * Current Model Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class CurrentModelTests {
    
    @Test
    public void getDOMroot_ReturnsCorrectObject() throws Exception {
        Object domObject = DomExtensionFactory.getDOMExtensions().get("model");
        assertTrue(domObject instanceof CurrentModel);
        
        // Initially this will be null and so should throw an ArchiScriptException
        assertThrows(ArchiScriptException.class, () -> {
            ((CurrentModel)domObject).getEObject();
        });
    }

}
