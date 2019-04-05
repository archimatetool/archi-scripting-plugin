/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.script.ArchiScriptException;

import junit.framework.JUnit4TestAdapter;


/**
 * ArchimateModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateModelProxyTests extends EObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ArchimateModelProxyTests.class);
    }
    
    private ArchimateModelProxy actualTestProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        testEObject = IArchimateFactory.eINSTANCE.createArchimateModel();
        ((IArchimateModel)testEObject).setDefaults();
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (ArchimateModelProxy)testProxy;
    }
    
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createArchimateModel());
        assertTrue(proxy instanceof ArchimateModelProxy);
    }

    @Override
    @Test
    public void getModel() {
        assertEquals(actualTestProxy, testProxy.getModel());
    }
    
    @Test
    public void setAsCurrent() {
        IArchimateModel model = null;
        
        // Initially this will be null and so will throw an ArchiScriptException
        try {
            model = CurrentModel.INSTANCE.getEObject();
        }
        catch(ArchiScriptException ex) {
        }
        
        assertNull(model);
        
        actualTestProxy.setAsCurrent();
        assertEquals(actualTestProxy, CurrentModel.INSTANCE);
    }

    @Override
    @Test
    public void find() {
        ArchimateModelProxy testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        
        EObjectProxyCollection collection = testModelProxy.find();
        assertEquals(788, collection.size());

        for(EObjectProxy eObjectProxy : collection) {
            assertNotNull(eObjectProxy.getEObject());
        }
    }
    
    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        ArchimateModelProxy testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        
        EObjectProxyCollection collection = testModelProxy.find("garbage");
        assertEquals(0, collection.size());

        collection = testModelProxy.find("*");
        assertEquals(340, collection.size());
        
        collection = testModelProxy.find("concept");
        assertEquals(298, collection.size());

        collection = testModelProxy.find("element");
        assertEquals(120, collection.size());

        collection = testModelProxy.find("relation");
        assertEquals(178, collection.size());

        collection = testModelProxy.find("folder");
        assertEquals(25, collection.size());

        collection = testModelProxy.find("view");
        assertEquals(17, collection.size());

        collection = testModelProxy.find("#66a2171b");
        assertEquals(1, collection.size());
        assertEquals("66a2171b", collection.get(0).getId());

        collection = testModelProxy.find(".Business");
        assertEquals(2, collection.size());
        
        collection = testModelProxy.find("folder.Business");
        assertEquals(2, collection.size());
        
        collection = testModelProxy.find("business-role");
        assertEquals(5, collection.size());
    }

    @Test
    public void attr_Purpose() {
        assertEquals("", actualTestProxy.attr(IModelConstants.PURPOSE));
        actualTestProxy.attr(IModelConstants.PURPOSE, "p");
        assertEquals("p", actualTestProxy.attr(IModelConstants.PURPOSE));
    }

    @Test
    public void setPurpose() {
        actualTestProxy.setPurpose("purpose");
        assertEquals("purpose", actualTestProxy.getPurpose());
    }

    @Test
    public void copy() {
        assertSame(testEObject, actualTestProxy.getEObject());
        
        ArchimateModelProxy proxy = actualTestProxy.copy();
        assertSame(actualTestProxy.getEObject(), proxy.getEObject());
        assertEquals(actualTestProxy, proxy);
    }
    
    @Test
    public void save() throws IOException {
        File file = File.createTempFile("~temp", ".archimate");
        file.deleteOnExit();
        
        actualTestProxy.getEObject().setAdapter(IArchiveManager.class, IArchiveManager.FACTORY.createArchiveManager(actualTestProxy.getEObject()));
        actualTestProxy.save(file.getAbsolutePath());
        
        assertTrue(file.exists());
        assertTrue(file.length() > 100);
    }
    
    @Test
    public void getPath() {
        assertNull(actualTestProxy.getPath());
        
        File file = new File("/path/test.archimate");
        actualTestProxy.getEObject().setFile(file);
        assertEquals(file.getAbsolutePath(), actualTestProxy.getPath());
    }
    
    @Test
    public void createElement() {
        ArchimateElementProxy proxy = actualTestProxy.createElement("business-actor", "Fido");
        assertNotNull(proxy);
        
        IArchimateElement element = proxy.getEObject();
        assertEquals("Fido", element.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getBusinessActor(), element.eClass());
    }
    
    @Test(expected = ArchiScriptException.class)
    public void createElement_Bogus() {
        actualTestProxy.createElement("access-relationship", "Fido");
    }

    @Test
    public void createRelationship() {
        ArchimateElementProxy source = actualTestProxy.createElement("business-actor", "Fido");
        ArchimateElementProxy target = actualTestProxy.createElement("business-role", "Role");
        
        ArchimateRelationshipProxy proxy = actualTestProxy.createRelationship("assignment-relationship", "Fido", source, target);
        assertNotNull(proxy);
        
        IArchimateRelationship relation = proxy.getEObject();
        assertEquals("Fido", relation.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getAssignmentRelationship(), relation.eClass());
    }
    
    @Test(expected = ArchiScriptException.class)
    public void addRelationship_Bogus() {
        ArchimateElementProxy source = actualTestProxy.createElement("BusinessActor", "Fido");
        ArchimateElementProxy target = actualTestProxy.createElement("BusinessRole", "Role");
        actualTestProxy.createRelationship("BusinessActor", "Fido", source, target);
    }
    
    @Test(expected = ArchiScriptException.class)
    public void addRelationship_BogusType() {
        ArchimateElementProxy source = actualTestProxy.createElement("BusinessActor", "Fido");
        ArchimateElementProxy target = actualTestProxy.createElement("BusinessRole", "Role");
        actualTestProxy.createRelationship("AccessRelationship", "Fido", source, target);
    }
}