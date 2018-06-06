/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IBusinessActor;
import com.archimatetool.model.IBusinessRole;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;

import junit.framework.JUnit4TestAdapter;


/**
 * ArchimateElementProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateElementProxyTests extends ArchimateConceptProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ArchimateElementProxyTests.class);
    }
    
    private ArchimateElementProxy actualTestProxy;
    
    private ArchimateModelProxy testModelProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestModel();
        
        testEObject = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "352");
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (ArchimateElementProxy)testProxy;
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

    @Test
    public void delete() {
        ArchimateElementProxy proxy = testModelProxy.addElement("business-actor", "Fred");
        assertTrue(proxy.getEObject().eContainer() instanceof IFolder);
        
        proxy.delete();
        assertNull(proxy.getEObject().eContainer());
    }

    @Test
    public void setType() {
        assertTrue(actualTestProxy.getEObject() instanceof IBusinessActor);
        assertEquals(3, actualTestProxy.outRels().size());
        assertEquals(6, actualTestProxy.inRels().size());
        assertEquals(3, actualTestProxy.objectRefs().size());

        ArchimateElementProxy newElementProxy = actualTestProxy.setType("business-role");
        
        assertSame(newElementProxy, testProxy);
        assertTrue(newElementProxy.getEObject() instanceof IBusinessRole);
        assertEquals(3, actualTestProxy.outRels().size());
        assertEquals(6, actualTestProxy.inRels().size());
        assertEquals(3, actualTestProxy.objectRefs().size());
    }

    @Test
    public void outRels() {
        EObjectProxyCollection collection = actualTestProxy.outRels();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }

    @Test
    public void inRels() {
        EObjectProxyCollection collection = actualTestProxy.inRels();
        assertEquals(6, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }
    
    @Test
    public void objectRefs() {
        EObjectProxyCollection collection = actualTestProxy.objectRefs();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelObjectProxy);
        }
    }
    
    @Test
    public void viewRefs() {
        EObjectProxyCollection collection = actualTestProxy.viewRefs();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelProxy);
        }
    }

}