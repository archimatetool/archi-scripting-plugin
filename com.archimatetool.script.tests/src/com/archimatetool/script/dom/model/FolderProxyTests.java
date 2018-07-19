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

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.Test;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;

import junit.framework.JUnit4TestAdapter;


/**
 * FolderProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class FolderProxyTests extends EObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(FolderProxyTests.class);
    }
    
    private ArchimateModelProxy testModelProxy;
    
    private FolderProxy actualTestProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        
        testEObject = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "74944b84"); // Relationships sub Folder
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (FolderProxy)testProxy;
    }
    
    @Test
    public void setName_NotSystemFolder() {
        testEObject = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "408ff6d3");
        testProxy = EObjectProxy.get(testEObject);
        
        testProxy.setName("test");
        assertEquals("Relations", testProxy.getName());
    }
    
    @Override
    @Test
    public void parent() {
        EObjectProxy object = testProxy.parent();
        assertTrue(object instanceof FolderProxy);
    }

    @Override
    @Test
    public void parents() {
        EObjectProxyCollection collection = testProxy.parents();
        assertEquals(2, collection.size());
    }

    @Override
    @Test
    public void find() {
        EObjectProxyCollection collection = testProxy.find();
        assertEquals(28, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }
    
    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        EObjectProxyCollection collection = testProxy.find("garbage");
        assertEquals(0, collection.size());

        collection = testProxy.find("*");
        assertEquals(28, collection.size());
        
        collection = testProxy.find("concept");
        assertEquals(28, collection.size());

        collection = testProxy.find("relation");
        assertEquals(28, collection.size());
    
        collection = testProxy.find("element");
        assertEquals(0, collection.size());
    }
    
    @Override
    @Test
    public void children() {
        EObjectProxyCollection collection = actualTestProxy.children();
        assertEquals(actualTestProxy.getEObject().getElements().size(), collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }
    
    @Override
    @Test
    public void delete() {
        assertEquals(actualTestProxy.getEObject().getElements().size(), actualTestProxy.children().size());
        
        // Store folder contents (relationships)
        ArrayList<EObject> children = new ArrayList<>(actualTestProxy.getEObject().getElements());
        
        for(EObject eObject : children) {
            IArchimateRelationship rel = (IArchimateRelationship)eObject;
            assertNotNull(rel.eContainer());
            assertNotNull(rel.getArchimateModel());
            assertTrue(rel.getReferencingDiagramConnections().size() > 0);
            assertTrue(rel.getSource().getSourceRelationships().contains(rel));
            assertTrue(rel.getTarget().getTargetRelationships().contains(rel));
        }

        actualTestProxy.delete();
        
        assertEquals(0, actualTestProxy.children().size());
        assertNull(actualTestProxy.getModel());
        assertNull(actualTestProxy.getEObject().eContainer());
        
        for(EObject eObject : children) {
            IArchimateRelationship rel = (IArchimateRelationship)eObject;
            assertNull(rel.eContainer());
            assertNull(rel.getArchimateModel());
            assertEquals(0, rel.getReferencingDiagramConnections().size());
            assertFalse(rel.getSource().getSourceRelationships().contains(rel));
            assertFalse(rel.getTarget().getTargetRelationships().contains(rel));
        }
    }
    
    @Test
    public void createFolder() {
        FolderProxy parent = (FolderProxy)testProxy.parent();
        FolderProxy newFolder = parent.createFolder("Fido");
        assertEquals(parent, newFolder.parent());
        assertEquals("Fido", newFolder.getName());
    }
    
    @Test
    public void add_Concept() {
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "1544"); // BusinessInterface
        ArchimateConceptProxy conceptProxy = (ArchimateConceptProxy)EObjectProxy.get(concept);
        
        IFolder folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "403e5717");
        FolderProxy folderProxy = (FolderProxy)EObjectProxy.get(folder);
        
        folderProxy.add(conceptProxy);
        assertEquals(folderProxy, conceptProxy.parent());
    }
    
}