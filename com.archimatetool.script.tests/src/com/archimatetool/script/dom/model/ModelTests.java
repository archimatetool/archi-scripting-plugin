/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.commands.CommandStack;
import org.junit.Test;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.script.dom.DomExtensionHelper;

import junit.framework.JUnit4TestAdapter;


/**
 * Model Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ModelTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ModelTests.class);
    }
    
    @Test
    public void getDOMroot_ReturnsCorrectObject() throws Exception {
        Object domObject = DomExtensionHelper.getDomObject("com.archimatetool.script.model"); //$NON-NLS-1$
        assertTrue(domObject instanceof Model);
    }

    @Test
    public void load() {
        ArchimateModelProxy proxy = new Model().load(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE.getAbsolutePath());
        assertNotNull(proxy.getEObject());
    }    

    @Test
    public void create() {
        ArchimateModelProxy proxy = new Model().create("foo");
        assertNotNull(proxy.getEObject());
        assertEquals("foo", proxy.getName());
        
        assertNotNull(proxy.getEObject().getAdapter(IArchiveManager.class));
        assertNull(proxy.getEObject().getAdapter(CommandStack.class));
    }
}
