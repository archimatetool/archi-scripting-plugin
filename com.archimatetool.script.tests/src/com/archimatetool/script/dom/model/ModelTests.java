/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.junit.BeforeClass;
import org.junit.Test;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.script.ArchiScriptException;
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

    private static Model model;
    
    @BeforeClass
    public static void runOnce() {
        model = new Model();
    }
    
    @Test
    public void getDOMroot_ReturnsCorrectObject() throws Exception {
        Object domObject = DomExtensionHelper.getDomObject("com.archimatetool.script.model"); //$NON-NLS-1$
        assertTrue(domObject instanceof Model);
    }

    @Test
    public void load() {
        ArchimateModelProxy proxy = model.load(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE.getAbsolutePath());
        assertNotNull(proxy.getEObject());
    }    

    @Test
    public void isModelLoaded() {
        ArchimateModelProxy proxy = model.load(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE.getAbsolutePath());
        assertFalse(model.isModelLoaded(proxy));
        assertFalse(model.isModelLoaded(null));
    }    

    @Test
    public void getLoadedModels() {
        List<ArchimateModelProxy> proxy = model.getLoadedModels();
        assertTrue(proxy.isEmpty());
    }    

    @Test
    public void create() {
        ArchimateModelProxy proxy = model.create("foo");
        assertNotNull(proxy.getEObject());
        assertEquals("foo", proxy.getName());
        
        assertNotNull(proxy.getEObject().getAdapter(IArchiveManager.class));
        assertNull(proxy.getEObject().getAdapter(CommandStack.class));
    }
    
    @Test
    public void isAllowedRelationship() {
        assertFalse(model.isAllowedRelationship("influence-relationship", "business-actor", "business-role"));
        assertTrue(model.isAllowedRelationship("association-relationship", "business-actor", "business-role"));
    }

    @Test(expected=ArchiScriptException.class)
    public void isAllowedRelationship_Exception() {
        model.isAllowedRelationship("bogus", "business-actor", "business-role");
    }
}
