/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.archimatetool.model.IArchimateModelObject;

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelNoteProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelNoteProxyTests extends DiagramModelObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DiagramModelNoteProxyTests.class);
    }
    
    private ArchimateModelProxy modelProxy;
    private ArchimateDiagramModelProxy viewProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        modelProxy = TestsHelper.createTestModel();
        viewProxy = modelProxy.createArchimateView("test");
        testProxy = viewProxy.createObject("note", 0, 0, 100, 100);
        testEObject = (IArchimateModelObject)testProxy.getEObject();
        actualTestProxy = (DiagramModelNoteProxy)testProxy;
    }

    @Override
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof DiagramModelNoteProxy);
    }

    @Override
    @Test
    public void getReferencedConcept() {
        assertNull(actualTestProxy.getConcept());
    }
    
    @Override
    @Test
    public void children() {
        EObjectProxyCollection collection = testProxy.children();
        assertTrue(collection.isEmpty());
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
    public void attr_Text() {
        testProxy.attr(IModelConstants.TEXT, "Hello");
        assertEquals("Hello", testProxy.attr(IModelConstants.TEXT));
    }

    @Test
    public void setText() {
        ((DiagramModelNoteProxy)testProxy).setText("Hello");
        assertEquals("Hello", ((DiagramModelNoteProxy)testProxy).getText());
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