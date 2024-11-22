/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.canvas.model.ICanvasFactory;
import com.archimatetool.canvas.model.ICanvasModel;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;


/**
 * CanvasDiagramModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
public class CanvasDiagramModelProxyTests extends DiagramModelProxyTests {
    
    private IArchimateModel model;
    private ICanvasModel testEObject;
    private CanvasDiagramModelProxy testProxy;
    
    @Override
    protected ICanvasModel getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected CanvasDiagramModelProxy getTestProxy() {
        return testProxy;
    }
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        
        testEObject = ICanvasFactory.eINSTANCE.createCanvasModel();
        model.getFolder(FolderType.DIAGRAMS).getElements().add(testEObject);
        
        testProxy = (CanvasDiagramModelProxy)EObjectProxy.get(testEObject);
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof CanvasDiagramModelProxy);
    }

    @Override
    @Test
    public void parent() {
        EObjectProxy parent = testProxy.parent();
        assertSame(model.getFolder(FolderType.DIAGRAMS), parent.getEObject());
    }

    @Override
    @Test
    public void parents() {
        EObjectProxyCollection collection = testProxy.parents();
        assertEquals(1, collection.size());
        assertSame(model.getFolder(FolderType.DIAGRAMS), collection.get(0).getEObject());
    }

    @Override
    @Test
    public void delete() {
        testProxy.delete();
        assertEquals(0, testProxy.children().size());
        assertNull(testProxy.getModel());
    }

}