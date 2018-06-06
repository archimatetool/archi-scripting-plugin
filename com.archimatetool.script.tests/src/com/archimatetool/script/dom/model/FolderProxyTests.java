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
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestModel();
        
        testEObject = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "74944b84");
        testProxy = EObjectProxy.get(testEObject);
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
        
        collection = testProxy.find("concepts");
        assertEquals(28, collection.size());

        collection = testProxy.find("relations");
        assertEquals(28, collection.size());
    
        collection = testProxy.find("elements");
        assertEquals(0, collection.size());
    }
    
    @Override
    @Test
    public void children() {
        EObjectProxyCollection collection = testProxy.children();
        assertEquals(28, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }
    
}