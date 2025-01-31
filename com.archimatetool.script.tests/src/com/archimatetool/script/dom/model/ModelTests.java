/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.dom.DomExtensionFactory;


/**
 * Model Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ModelTests {
    
    private static Model model;
    
    @BeforeAll
    public static void runOnce() {
        model = new Model();
    }
    
    @Test
    public void getDOMroot_ReturnsCorrectObject() {
        Object domObject = DomExtensionFactory.getDOMExtensions().get("jArchiModel");
        assertTrue(domObject instanceof Model);
    }

    @Test
    public void load() throws Exception {
        ArchimateModelProxy proxy = model.load(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE.getAbsolutePath());
        assertNotNull(proxy.getEObject());
        
        // Ensure model loaded from same file is not the same model as before
        ArchimateModelProxy proxy2 = model.load(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE.getAbsolutePath());
        assertNotNull(proxy2.getEObject());
        assertNotSame(proxy.getEObject(), proxy2.getEObject());
        
        // And that loaded models is empty
        assertTrue(IEditorModelManager.INSTANCE.getModels().isEmpty());
    }

    @Test
    public void isModelLoaded() throws Exception {
        ArchimateModelProxy proxy = model.load(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE.getAbsolutePath());
        assertFalse(model.isModelLoaded(proxy));
        assertFalse(model.isModelLoaded(null));
    }    

    @Test
    public void getLoadedModels() {
        List<ArchimateModelProxy> modelProxies = model.getLoadedModels();
        assertTrue(modelProxies.isEmpty());
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

    @Test
    public void isAllowedRelationship_Exception() {
        assertThrows(ArchiScriptException.class, () -> {
            model.isAllowedRelationship("bogus", "business-actor", "business-role");
        });
    }
}
