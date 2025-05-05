/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.TestFiles;


/**
 * ArchimateModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateModelProxyTests extends EObjectProxyTests {
    
    private IArchimateModel testEObject;
    private ArchimateModelProxy testProxy;
    
    @Override
    protected IArchimateModel getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected ArchimateModelProxy getTestProxy() {
        return testProxy;
    }
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        testEObject = IArchimateFactory.eINSTANCE.createArchimateModel();
        testEObject.setDefaults();
        testProxy = (ArchimateModelProxy)EObjectProxy.get(testEObject);
    }
    
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createArchimateModel());
        assertTrue(proxy instanceof ArchimateModelProxy);
    }

    @Override
    @Test
    public void getModel() {
        assertEquals(testProxy, testProxy.getModel());
    }
    
    @Test
    public void setAsCurrent() {
        // Create our own instance as the singleton won't be loaded
        CurrentModel currentModel = new CurrentModel();
        
        // Initially this will be null and so will throw an ArchiScriptException
        assertThrows(ArchiScriptException.class, () -> {
            currentModel.getEObject();
        });
        
        testProxy.setAsCurrent();
        assertEquals(testProxy, currentModel);
        assertEquals(testProxy.getEObject(), currentModel.getEObject());
    }
    
    @Override
    @Test
    public void children() {
        EObjectProxyCollection collection = testProxy.children();
        assertEquals(testProxy.getEObject().getFolders().size(), collection.size());
        
        // Should be top-level folders
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof FolderProxy);
        }
    }

    @Override
    @Test
    public void find() {
        ArchimateModelProxy testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
        
        EObjectProxyCollection collection = testModelProxy.find();
        assertEquals(787, collection.size());

        for(EObjectProxy eObjectProxy : collection) {
            assertNotNull(eObjectProxy.getEObject());
        }
    }
    
    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        ArchimateModelProxy testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
        
        EObjectProxyCollection collection = testModelProxy.find("garbage");
        assertEquals(0, collection.size());
        
        collection = testModelProxy.find("diagram-model-group");
        assertEquals(0, collection.size());

        collection = testModelProxy.find("*");
        assertEquals(339, collection.size());
        
        collection = testModelProxy.find("concept");
        assertEquals(296, collection.size());

        collection = testModelProxy.find("element");
        assertEquals(120, collection.size());

        collection = testModelProxy.find("relation");
        assertEquals(176, collection.size());

        collection = testModelProxy.find("folder");
        assertEquals(26, collection.size());

        collection = testModelProxy.find("view");
        assertEquals(17, collection.size());
        
        collection = testModelProxy.find(".Business");
        assertEquals(2, collection.size());
        
        collection = testModelProxy.find("folder.Business");
        assertEquals(2, collection.size());
        
        collection = testModelProxy.find("business-role");
        assertEquals(5, collection.size());
    }
    
    @Test
    public void find_Selector_IDs() {
        ArchimateModelProxy testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
        
        // Test we can find every object by its ID
        for(Iterator<EObject> iter = testModelProxy.getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            if(eObject instanceof IIdentifier) {
                String id = ((IIdentifier)eObject).getId();
                EObjectProxyCollection collection = testModelProxy.find("#" + id);
                assertEquals(1, collection.size());
                assertEquals(id, collection.get(0).getId());
            }
        }
    }

    @Test
    public void attr_Purpose() {
        assertEquals("", testProxy.attr(PURPOSE));
        testProxy.attr(PURPOSE, "p");
        assertEquals("p", testProxy.attr(PURPOSE));
    }

    @Test
    public void setPurpose() {
        testProxy.setPurpose("purpose");
        assertEquals("purpose", testProxy.getPurpose());
    }

    @Test
    public void copy() {
        assertSame(testEObject, testProxy.getEObject());
        
        ArchimateModelProxy proxy = testProxy.copy();
        assertSame(testProxy.getEObject(), proxy.getEObject());
        assertEquals(testProxy, proxy);
    }
    
    @Test
    public void save() throws IOException {
        File file = File.createTempFile("~temp", ".archimate");
        file.deleteOnExit();
        
        testProxy.getEObject().setAdapter(IArchiveManager.class, IArchiveManager.FACTORY.createArchiveManager(testProxy.getEObject()));
        testProxy.save(file.getAbsolutePath());
        
        assertTrue(file.exists());
        assertTrue(file.length() > 100);
    }
    
    @Test
    public void getPath() {
        assertNull(testProxy.getPath());
        
        File file = new File("/path/test.archimate");
        testProxy.getEObject().setFile(file);
        assertEquals(file.getAbsolutePath(), testProxy.getPath());
    }
    
    @Test
    public void createElement() {
        ArchimateElementProxy proxy = testProxy.createElement("business-actor", "Fido");
        assertNotNull(proxy);
        
        IArchimateElement element = proxy.getEObject();
        assertEquals("Fido", element.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getBusinessActor(), element.eClass());
    }
    
    @Test
    public void createElement_Bogus() {
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.createElement("access-relationship", "Fido");
        });
    }

    @Test
    public void createRelationship() {
        ArchimateElementProxy source = testProxy.createElement("business-actor", "Fido");
        ArchimateElementProxy target = testProxy.createElement("business-role", "Role");
        
        ArchimateRelationshipProxy proxy = testProxy.createRelationship("assignment-relationship", "Fido", source, target);
        assertNotNull(proxy);
        
        IArchimateRelationship relation = proxy.getEObject();
        assertEquals("Fido", relation.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getAssignmentRelationship(), relation.eClass());
    }
    
    @Test
    public void addRelationship_Bogus() {
        ArchimateElementProxy source = testProxy.createElement("business-actor", "Fido");
        ArchimateElementProxy target = testProxy.createElement("business-role", "Role");
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.createRelationship("BusinessActor", "Fido", source, target);
        });
    }
    
    @Test
    public void addRelationship_BogusType() {
        ArchimateElementProxy source = testProxy.createElement("business-actor", "Fido");
        ArchimateElementProxy target = testProxy.createElement("business-role", "Role");
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.createRelationship("access-relationship", "Fido", source, target);
        });
    }
    
    @Test
    public void getSpecializations() {
        ProfileProxy proxy1 = testProxy.createSpecialization("Spec", "business-actor", null);
        ProfileProxy proxy2 = testProxy.createSpecialization("Spec2", "business-object", null);
        
        assertEquals(2, testProxy.getSpecializations().size());
        assertEquals(proxy1, testProxy.getSpecializations().get(0));
        assertEquals(proxy2, testProxy.getSpecializations().get(1));
    }
    
    @Test
    public void findSpecialization() {
        ProfileProxy proxy1 = testProxy.createSpecialization("Spec", "business-actor", null);
        ProfileProxy proxy2 = testProxy.createSpecialization("Spec2", "business-object", null);
        
        assertEquals(proxy1, testProxy.findSpecialization("Spec", "business-actor"));
        assertEquals(proxy2, testProxy.findSpecialization("Spec2", "business-object"));
        assertNull(testProxy.findSpecialization("Spec2", "business-actor"));
    }
}