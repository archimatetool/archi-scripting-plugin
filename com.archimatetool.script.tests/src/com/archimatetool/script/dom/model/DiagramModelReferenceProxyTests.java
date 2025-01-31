/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.model.IDiagramModelReference;


/**
 * DiagramModelGroupProxyTests Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelReferenceProxyTests extends DiagramModelObjectProxyTests {
    
    private ArchimateDiagramModelProxy viewProxy1, viewProxy2;
    private IDiagramModelReference testEObject;
    private DiagramModelReferenceProxy testProxy;
    
    @Override
    protected IDiagramModelReference getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected DiagramModelReferenceProxy getTestProxy() {
        return testProxy;
    }
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        ArchimateModelProxy modelProxy = TestsHelper.createTestArchimateModelProxy();
        viewProxy1 = modelProxy.createArchimateView("view1");
        viewProxy2 = modelProxy.createArchimateView("view2");
        testProxy = viewProxy1.createViewReference(viewProxy2, 0, 0, 100, 100);
        testEObject = testProxy.getEObject();
    }

    @Override
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof DiagramModelReferenceProxy);
    }

    @Override
    @Test
    public void getReferencedEObject() {
        assertNull(testProxy.getConcept());
    }
    
    @Test
    public void getRefView() {
        assertEquals(viewProxy2, testProxy.getRefView());
    }
    
    @Override
    @Test
    public void delete() {
        assertEquals(viewProxy1, testProxy.parent());
        testProxy.delete();
        assertNull(testProxy.parent());
    }
    
    @Override
    @Test
    public void parent() {
        assertEquals(viewProxy1, testProxy.parent());
    }

    @Override
    @Test
    public void parents() {
        assertNotNull(testProxy.parents());
    }
}