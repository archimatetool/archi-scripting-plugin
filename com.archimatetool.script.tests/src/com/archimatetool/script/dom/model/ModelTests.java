/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.archimatetool.script.dom.DomExtensionHelper;

import junit.framework.JUnit4TestAdapter;


/**
 * Tests
 * 
 * @author Phillip Beauvoir
 */
public class ModelTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ModelTests.class);
    }
    
    @Before
    public void runOnceBeforeEachTest() {
    }
    
    @After
    public void runOnceAfterEachTest() {
    }
    
    @Test
    public void getDOMroot_ReturnsCorrectObject() throws Exception {
        Object domObject = DomExtensionHelper.getDomObject("com.archimatetool.script.model"); //$NON-NLS-1$
        assertNotNull(domObject);
        assertTrue(domObject instanceof Model);
    }

}
