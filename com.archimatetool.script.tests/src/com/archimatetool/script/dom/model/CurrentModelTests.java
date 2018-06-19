/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.archimatetool.script.dom.DomExtensionHelper;

import junit.framework.JUnit4TestAdapter;


/**
 * Current Model Tests
 * 
 * @author Phillip Beauvoir
 */
public class CurrentModelTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CurrentModelTests.class);
    }
    
    @Test
    public void getDOMroot_ReturnsCorrectObject() throws Exception {
        Object domObject = DomExtensionHelper.getDomObject("com.archimatetool.script.currentmodel"); //$NON-NLS-1$
        assertTrue(domObject instanceof CurrentModel);
    }

}
