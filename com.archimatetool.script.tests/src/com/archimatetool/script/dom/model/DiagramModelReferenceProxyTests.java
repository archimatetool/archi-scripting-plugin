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

import com.archimatetool.model.IArchimateModelObject;


/**
 * DiagramModelGroupProxyTests Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelReferenceProxyTests extends DiagramModelObjectProxyTests {
    
    private ArchimateModelProxy modelProxy;
    private ArchimateDiagramModelProxy viewProxy1, viewProxy2;
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        modelProxy = TestsHelper.createTestModel();
        viewProxy1 = modelProxy.createArchimateView("view1");
        viewProxy2 = modelProxy.createArchimateView("view2");
        testProxy = viewProxy1.createViewReference(viewProxy2, 0, 0, 100, 100);
        testEObject = (IArchimateModelObject)testProxy.getEObject();
        actualTestProxy = (DiagramModelReferenceProxy)testProxy;
    }

    @Override
    @Test
    public void getType() {
        // The type should be the IDiagramModelReference, not the referenced diagram model
        assertEquals(ModelUtil.getKebabCase(testProxy.getEObject().eClass().getName()), testProxy.getType());
    }
    
    @Override
    public void attr_Type() {
        // The type should be the IDiagramModelReference, not the referenced diagram model        
        assertEquals(ModelUtil.getKebabCase(testProxy.getEObject().eClass().getName()), testProxy.getType());
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
        assertNull(actualTestProxy.getConcept());
    }
    
    @Test
    public void getRefView() {
        DiagramModelReferenceProxy dmRefProxy = (DiagramModelReferenceProxy)actualTestProxy;
        assertEquals(viewProxy2, dmRefProxy.getRefView());
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