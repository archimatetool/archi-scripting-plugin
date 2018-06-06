/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelReference;
import com.archimatetool.model.util.ArchimateModelUtils;

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelProxyTests extends EObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DiagramModelProxyTests.class);
    }
    
    private DiagramModelProxy actualTestProxy;
    
    private ArchimateModelProxy testModelProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestModel();
        
        testEObject = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4056");
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (DiagramModelProxy)testProxy;
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof DiagramModelProxy);
    }

    @Override
    @Test
    public void parent() {
        EObjectProxy parent = actualTestProxy.parent();
        assertEquals("e64e9b49", parent.getId());
    }

    @Override
    @Test
    public void parents() {
        EObjectProxyCollection collection = actualTestProxy.parents();
        assertEquals(1, collection.size());
        assertEquals("e64e9b49", collection.get(0).getId());
    }
    
    @Override
    @Test
    public void find() {
        EObjectProxyCollection collection = actualTestProxy.find();
        assertEquals(65, collection.size());
    }
    
    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        EObjectProxyCollection collection = actualTestProxy.find("garbage");
        assertEquals(0, collection.size());

        collection = actualTestProxy.find("*");
        assertEquals(65, collection.size());
    }

    @Override
    @Test
    public void children() {
        EObjectProxyCollection collection = actualTestProxy.children();
        assertEquals(35, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelComponentProxy);
        }
    }
    
    @Test
    public void objectRefs() {
        EObjectProxyCollection collection = actualTestProxy.objectRefs();
        assertEquals(1, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelObjectProxy);
            assertTrue(eObjectProxy.getEObject() instanceof IDiagramModelReference);
        }
    }

    @Test
    public void viewRefs() {
        EObjectProxyCollection collection = actualTestProxy.viewRefs();
        assertEquals(1, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelProxy);
            assertTrue(eObjectProxy.getEObject() instanceof IDiagramModel);
        }
    }
}