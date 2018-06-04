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

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.util.ArchimateModelUtils;

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelObjectProxyTests extends DiagramModelComponentProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DiagramModelObjectProxyTests.class);
    }
    
    private DiagramModelObjectProxy actualTestProxy;
    
    private ArchimateModelProxy testModelProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = loadTestModel();
        
        testEObject = (IDiagramModelArchimateObject)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4104");
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (DiagramModelObjectProxy)testProxy;
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject());
        assertTrue(proxy instanceof DiagramModelObjectProxy);
        
        proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createDiagramModelNote());
        assertTrue(proxy instanceof DiagramModelObjectProxy);
    }

    @Override
    @Test
    public void parent_Expected() {
        // Find a diagram object
        EObjectProxyCollection c = testModelProxy.find("#4104");
        EObjectProxy object = c.first().parent();
        assertEquals("4096", object.getId());
    }

    @Override
    @Test
    public void parents_Expected() {
        // Find a diagram object
        EObjectProxyCollection c = testModelProxy.find("#4104");
        EObjectProxyCollection collection = c.first().parents();
        
        assertEquals(3, collection.size());
        assertEquals("4096", collection.get(0).getId());
        assertEquals("4056", collection.get(1).getId());
        assertEquals("e64e9b49", collection.get(2).getId());
    }
    
    @Override
    @Test
    public void find_All() {
        EObjectProxyCollection collection = testProxy.find();
        assertEquals(1, collection.size());
    }
    
    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        EObjectProxyCollection collection = testProxy.find("garbage");
        assertEquals(0, collection.size());

        collection = testProxy.find("*");
        assertEquals(1, collection.size());
    }

}