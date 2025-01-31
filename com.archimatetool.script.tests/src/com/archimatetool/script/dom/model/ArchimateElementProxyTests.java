/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IBusinessActor;
import com.archimatetool.model.IBusinessRole;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IJunction;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.TestFiles;


/**
 * ArchimateElementProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateElementProxyTests extends ArchimateConceptProxyTests {
    
    private ArchimateModelProxy testModelProxy;
    private IArchimateElement testEObject;
    private ArchimateElementProxy testProxy;
    
    @Override
    protected IArchimateElement getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected ArchimateElementProxy getTestProxy() {
        return testProxy;
    }

    @BeforeEach
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
        testEObject = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "352");
        testProxy = (ArchimateElementProxy)EObjectProxy.get(testEObject);
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createBusinessActor());
        assertTrue(proxy instanceof ArchimateElementProxy);
    }

    @Override
    @Test
    public void getModel() {
        assertEquals(testModelProxy, testProxy.getModel());
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
    public void delete() {
        assertEquals(3, testProxy.objectRefs().size());
        assertEquals(6, testProxy.inRels().size());
        assertEquals(3, testProxy.outRels().size());
        
        testProxy.delete();

        assertNull(testProxy.getModel());
        assertEquals(0, testProxy.objectRefs().size());
        assertEquals(0, testProxy.inRels().size());
        assertEquals(0, testProxy.outRels().size());
    }

    @Test
    public void setType() {
        testProxy.setName("Type Test");
        testProxy.setDocumentation("Documentation");
        testProxy.prop("p1", "v1");
        testProxy.prop("p2", "v2");
        testProxy.getEObject().getFeatures().putString("f1", "v1");
        testProxy.getEObject().getFeatures().putString("f2", "v2");
        
        assertTrue(testProxy.getEObject() instanceof IBusinessActor);
        assertEquals(3, testProxy.outRels().size());
        assertEquals(6, testProxy.inRels().size());
        assertEquals(3, testProxy.objectRefs().size());

        ArchimateElementProxy newElementProxy = testProxy.setType("business-role");
        
        assertSame(newElementProxy, testProxy);
        assertTrue(newElementProxy.getEObject() instanceof IBusinessRole);
        
        assertEquals("Type Test", newElementProxy.getName());
        assertEquals("Documentation", newElementProxy.getDocumentation());
        assertEquals(2, newElementProxy.prop().size());
        assertEquals(2, newElementProxy.getEObject().getFeatures().size());
        
        assertEquals(3, newElementProxy.outRels().size());
        assertEquals(6, newElementProxy.inRels().size());
        assertEquals(3, newElementProxy.objectRefs().size());
    }

    @Test
    public void outRels() {
        EObjectProxyCollection collection = testProxy.outRels();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }

    @Test
    public void inRels() {
        EObjectProxyCollection collection = testProxy.inRels();
        assertEquals(6, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }
    
    @Test
    public void objectRefs() {
        EObjectProxyCollection collection = testProxy.objectRefs();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelObjectProxy);
        }
    }
    
    @Test
    public void viewRefs() {
        EObjectProxyCollection collection = testProxy.viewRefs();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelProxy);
        }
    }

    @Test
    public void merge() {
        // Set up
        testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_MERGE);
        
        IArchimateElement replacementElement = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(),
                "76aa9eb3-8cdd-471c-81e4-965d94e12dd9");
        ArchimateElementProxy replacementProxy = (ArchimateElementProxy)EObjectProxy.get(replacementElement);
        
        IArchimateElement otherElement = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(),
                "ad78af0a-24ce-44b2-b512-35f6f77204e3");
        ArchimateElementProxy otherProxy = (ArchimateElementProxy)EObjectProxy.get(otherElement);
        
        // Confirmation tests
        
        // Documentation
        assertEquals("Doc1", replacementProxy.getDocumentation());
        
        // Properties
        assertEquals(1, replacementElement.getProperties().size());
        
        // Diagram References
        EObjectProxyCollection refs = replacementProxy.objectRefs();
        assertEquals(2, refs.size());
        for(EObjectProxy eObjectProxy : refs) {
            DiagramModelObjectProxy dmoProxy = (DiagramModelObjectProxy)eObjectProxy;
            assertEquals(replacementElement, dmoProxy.getReferencedEObject());
        }
        
        refs = otherProxy.objectRefs();
        assertEquals(2, refs.size());
        for(EObjectProxy eObjectProxy : refs) {
            DiagramModelObjectProxy dmoProxy = (DiagramModelObjectProxy)eObjectProxy;
            assertEquals(otherElement, dmoProxy.getReferencedEObject());
        }
        
        // Relations
        assertEquals(0, replacementElement.getSourceRelationships().size());
        assertEquals(1, replacementElement.getTargetRelationships().size());

        // Merge
        replacementProxy.merge(otherProxy);
        
        // Post-operation tests

        // Documentation
        assertEquals("Doc1\nDoc2", replacementProxy.getDocumentation());
        
        // Properties
        assertEquals(3, replacementElement.getProperties().size());

        // Diagram References
        refs = replacementProxy.objectRefs();
        assertEquals(4, refs.size());
        for(EObjectProxy eObjectProxy : refs) {
            DiagramModelObjectProxy dmoProxy = (DiagramModelObjectProxy)eObjectProxy;
            assertEquals(replacementElement, dmoProxy.getReferencedEObject());
        }

        assertEquals(0, otherProxy.objectRefs().size());
        
        // Relations
        assertEquals(1, replacementElement.getSourceRelationships().size());
        assertEquals(2, replacementElement.getTargetRelationships().size());
    }
    
    @Test
    public void mergeThrowsExceptionOnWrongType() {
        testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_MERGE);
        
        IArchimateElement replacementElement = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(),
                "76aa9eb3-8cdd-471c-81e4-965d94e12dd9");
        ArchimateElementProxy replacementProxy = (ArchimateElementProxy)EObjectProxy.get(replacementElement);
        
        IArchimateElement otherElement = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(),
                "a3a16448-0760-4d5c-860e-d9f8826340a6");
        ArchimateElementProxy otherProxy = (ArchimateElementProxy)EObjectProxy.get(otherElement);
        
        assertThrows(ArchiScriptException.class, () -> {
            replacementProxy.merge(otherProxy);
        });
    }
    
    @Test
    public void setJunctionType() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        IJunction junction = IArchimateFactory.eINSTANCE.createJunction();
        model.getDefaultFolderForObject(junction).getElements().add(junction);
        ArchimateElementProxy proxy = (ArchimateElementProxy)EObjectProxy.get(junction);
        
        proxy.setJunctionType("or");
        assertEquals(IJunction.OR_JUNCTION_TYPE, proxy.getJunctionType());
        assertEquals(IJunction.OR_JUNCTION_TYPE, junction.getType());
        
        proxy.setJunctionType("and");
        assertEquals("and", proxy.getJunctionType());
        assertEquals(IJunction.AND_JUNCTION_TYPE, junction.getType());
        
        proxy.setJunctionType(null);
        assertEquals("and", proxy.getJunctionType());
        
        proxy.attr(IModelConstants.JUNCTION_TYPE, "AND");
        assertEquals("and", proxy.attr(IModelConstants.JUNCTION_TYPE));
        
        proxy.attr(IModelConstants.JUNCTION_TYPE, "OR");
        assertEquals(IJunction.OR_JUNCTION_TYPE, proxy.attr(IModelConstants.JUNCTION_TYPE));
    }
    
    @Test
    public void duplicate() {
        ArchimateElementProxy duplicate = testProxy.duplicate();
        
        assertNotNull(duplicate);
        assertNotEquals(testProxy.getId(), duplicate.getId());
        assertSame(testProxy.parent().getEObject(), duplicate.parent().getEObject());
    }
    
    @Test
    public void duplicateInFolder() {
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        IFolder parent = (IFolder)testProxy.getEObject().eContainer();
        parent.getFolders().add(folder);
        FolderProxy folderProxy = (FolderProxy)EObjectProxy.get(folder);
        
        ArchimateElementProxy duplicate = testProxy.duplicate(folderProxy);
        assertSame(folder, duplicate.parent().getEObject());
    }
    
    @Test
    public void duplicateInWrongFolderShouldThrowException() {
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        IFolder parent = testProxy.getArchimateModel().getFolder(FolderType.APPLICATION);
        parent.getFolders().add(folder);
        FolderProxy folderProxy = (FolderProxy)EObjectProxy.get(folder);
        
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.duplicate(folderProxy);
        });
    }
    
    @Test
    public void duplicateInFolderInDifferentModelsShouldThrowException() {
        // Create another model and get its Business folder
        ArchimateModelProxy modelProxy = TestsHelper.createTestArchimateModelProxy();
        IFolder folder = modelProxy.getArchimateModel().getFolder(FolderType.BUSINESS);
        FolderProxy folderProxy = (FolderProxy)EObjectProxy.get(folder);
        
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.duplicate(folderProxy);
        });
    }

}