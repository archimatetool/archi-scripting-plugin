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
public class DiagramModelGroupProxyTests extends DiagramModelObjectProxyTests {
    
    private ArchimateModelProxy modelProxy;
    private ArchimateDiagramModelProxy viewProxy;
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        modelProxy = TestsHelper.createTestModel();
        viewProxy = modelProxy.createArchimateView("test");
        testProxy = viewProxy.createObject(IModelConstants.DIAGRAM_MODEL_GROUP, 0, 0, 100, 100);
        testEObject = (IArchimateModelObject)testProxy.getEObject();
        actualTestProxy = (DiagramModelGroupProxy)testProxy;
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
        assertNull(actualTestProxy.getConcept());
    }
    
    @Override
    @Test
    public void children() {
        assertTrue(testProxy.children().isEmpty());
        
        DiagramModelObjectProxy noteProxy = ((DiagramModelObjectProxy)testProxy).createObject(IModelConstants.DIAGRAM_MODEL_NOTE, 0, 0, 20, 20);
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
        assertEquals(0, actualTestProxy.attr(IModelConstants.BORDER_TYPE));
        actualTestProxy.attr(IModelConstants.BORDER_TYPE, 1);
        assertEquals(1, actualTestProxy.attr(IModelConstants.BORDER_TYPE));
    }

    @Override
    @Test
    public void attr_TextAlignment() {
        assertEquals(1, actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT));
        actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT, 4);
        assertEquals(4, actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT));
    }

}