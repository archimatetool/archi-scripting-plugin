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

import com.archimatetool.model.IDiagramModelNote;


/**
 * DiagramModelNoteProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelNoteProxyTests extends DiagramModelObjectProxyTests {
    
    private ArchimateDiagramModelProxy viewProxy;
    private IDiagramModelNote testEObject;
    private DiagramModelNoteProxy testProxy;
    
    @Override
    protected IDiagramModelNote getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected DiagramModelNoteProxy getTestProxy() {
        return testProxy;
    }
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        ArchimateModelProxy modelProxy = TestsHelper.createTestArchimateModelProxy();
        viewProxy = modelProxy.createArchimateView("test");
        testProxy = (DiagramModelNoteProxy)viewProxy.createObject(DIAGRAM_MODEL_NOTE, 0, 0, 100, 100);
        testEObject = testProxy.getEObject();
    }

    @Override
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof DiagramModelNoteProxy);
    }

    @Override
    @Test
    public void getReferencedEObject() {
        assertNull(testProxy.getConcept());
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
        testProxy.attr(TEXT, "Hello");
        assertEquals("Hello", testProxy.attr(TEXT));
    }

    @Test
    public void setText() {
        testProxy.setText("Hello");
        assertEquals("Hello", testProxy.getText());
    }
    
    @Test
    public void attr_BorderType() {
        assertEquals(0, testProxy.attr(BORDER_TYPE));
        testProxy.attr(BORDER_TYPE, 1);
        assertEquals(1, testProxy.attr(BORDER_TYPE));
    }

    @Override
    @Test
    public void attr_TextAlignment() {
        assertEquals(1, testProxy.attr(TEXT_ALIGNMENT));
        testProxy.attr(TEXT_ALIGNMENT, 4);
        assertEquals(4, testProxy.attr(TEXT_ALIGNMENT));
    }

}