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
        testModelProxy = loadTestModel();
        
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
    public void getModel_IsExpectedObject() {
        assertEquals(testModelProxy, testProxy.getModel());
    }
    
    @Override
    @Test
    public void isElement() {
        assertTrue(testProxy.isElement());
    }

    @Override
    @Test
    public void parent_Expected() {
        EObjectProxy object = testProxy.parent();
        assertTrue(object instanceof FolderProxy);
    }

    @Override
    @Test
    public void parents_Expected() {
        EObjectProxyCollection collection = testProxy.parents();
        assertEquals(2, collection.size());
    }

    @Test
    public void invoke_Delete() {
        ArchimateElementProxy proxy = testModelProxy.addElement("BusinessActor", "Fred");
        assertTrue(proxy.getEObject().eContainer() instanceof IFolder);
        
        proxy.invoke("delete");
        assertNull(proxy.getEObject().eContainer());
    }
    
    @Test
    public void invoke_SetType() {
        Object newElementProxy = testProxy.invoke("setType", "BusinessActor");
        assertTrue(newElementProxy instanceof ArchimateElementProxy);
        assertSame(newElementProxy, testProxy);
    }

    @Test
    public void delete() {
        ArchimateElementProxy proxy = testModelProxy.addElement("BusinessActor", "Fred");
        assertTrue(proxy.getEObject().eContainer() instanceof IFolder);
        
        proxy.delete();
        assertNull(proxy.getEObject().eContainer());
    }

    @Test
    public void setType() {
        assertTrue(actualTestProxy.getEObject() instanceof IBusinessActor);
        assertEquals(3, actualTestProxy.getSourceRelationships().size());
        assertEquals(6, actualTestProxy.getTargetRelationships().size());
        assertEquals(3, actualTestProxy.getDiagramComponentInstances().size());

        ArchimateElementProxy newElementProxy = actualTestProxy.setType("BusinessRole");
        
        assertSame(newElementProxy, testProxy);
        assertTrue(newElementProxy.getEObject() instanceof IBusinessRole);
        assertEquals(3, actualTestProxy.getSourceRelationships().size());
        assertEquals(6, actualTestProxy.getTargetRelationships().size());
        assertEquals(3, actualTestProxy.getDiagramComponentInstances().size());
    }

    @Test
    public void getSourceRelationships() {
        EObjectProxyCollection collection = actualTestProxy.getSourceRelationships();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }

    @Test
    public void getTargetRelationships() {
        EObjectProxyCollection collection = actualTestProxy.getTargetRelationships();
        assertEquals(6, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof ArchimateRelationshipProxy);
        }
    }
    
    @Test
    public void getDiagramComponentInstances() {
        EObjectProxyCollection collection = actualTestProxy.getDiagramComponentInstances();
        assertEquals(3, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelObjectProxy);
        }
    }
}