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

import com.archimatetool.model.IDiagramModelGroup;


/**
 * DiagramModelGroupProxyTests Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelGroupProxyTests extends DiagramModelObjectProxyTests {
    
    private ArchimateDiagramModelProxy viewProxy;
    private IDiagramModelGroup testEObject;
    private DiagramModelGroupProxy testProxy;
    
    @Override
    protected IDiagramModelGroup getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected DiagramModelGroupProxy getTestProxy() {
        return testProxy;
    }
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        ArchimateModelProxy modelProxy = TestsHelper.createTestArchimateModelProxy();
        viewProxy = modelProxy.createArchimateView("test");
        testProxy = (DiagramModelGroupProxy)viewProxy.createObject(IModelConstants.DIAGRAM_MODEL_GROUP, 0, 0, 100, 100);
        testEObject = testProxy.getEObject();
    }

    @Override
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof DiagramModelGroupProxy);
    }

    @Override
    @Test
    public void getReferencedEObject() {
        assertNull(testProxy.getConcept());
    }
    
    @Override
    @Test
    public void children() {
        assertTrue(testProxy.children().isEmpty());
        
        DiagramModelObjectProxy noteProxy = testProxy.createObject(IModelConstants.DIAGRAM_MODEL_NOTE, 0, 0, 20, 20);
        assertEquals(1, testProxy.children().size());
        assertEquals(noteProxy, testProxy.children().first());
    }
    
    @Override
    @Test
    public void delete() {
        assertEquals(viewProxy, testProxy.parent());
        testProxy.delete();
        assertNull(testProxy.parent());
    }
    
    @Test
    public void deleteKeepChildren() {
        testProxy.getEObject().setBounds(100, 100, 100, 100);
        
        DiagramModelObjectProxy childProxy1 = testProxy.createObject(IModelConstants.DIAGRAM_MODEL_GROUP, 10, 20, 100, 100);
        DiagramModelObjectProxy childProxy2 = testProxy.createObject(IModelConstants.DIAGRAM_MODEL_GROUP, 30, 40, 100, 100);
        DiagramModelObjectProxy childProxy3 = childProxy2.createObject(IModelConstants.DIAGRAM_MODEL_GROUP, 10, 20, 100, 100);
        
        testProxy.delete(false);
        
        assertEquals(viewProxy, childProxy1.parent());
        assertEquals(viewProxy, childProxy2.parent());
        assertEquals(childProxy2, childProxy3.parent());
        
        assertEquals(110, childProxy1.getEObject().getBounds().getX());
        assertEquals(120, childProxy1.getEObject().getBounds().getY());
        assertEquals(100, childProxy1.getEObject().getBounds().getWidth());
        assertEquals(100, childProxy1.getEObject().getBounds().getHeight());
        
        assertEquals(10, childProxy3.getEObject().getBounds().getX());
        assertEquals(20, childProxy3.getEObject().getBounds().getY());
        assertEquals(100, childProxy3.getEObject().getBounds().getWidth());
        assertEquals(100, childProxy3.getEObject().getBounds().getHeight());
    }
    
    @Override
    @Test
    public void parent() {
        assertEquals(viewProxy, testProxy.parent());
    }

    @Override
    @Test
    public void parents() {
        assertNotNull(testProxy.parents());
    }

    @Test
    public void attr_BorderType() {
        assertEquals(0, testProxy.attr(IModelConstants.BORDER_TYPE));
        testProxy.attr(IModelConstants.BORDER_TYPE, 1);
        assertEquals(1, testProxy.attr(IModelConstants.BORDER_TYPE));
    }

    @Override
    @Test
    public void attr_TextAlignment() {
        assertEquals(1, testProxy.attr(IModelConstants.TEXT_ALIGNMENT));
        testProxy.attr(IModelConstants.TEXT_ALIGNMENT, 4);
        assertEquals(4, testProxy.attr(IModelConstants.TEXT_ALIGNMENT));
    }

}