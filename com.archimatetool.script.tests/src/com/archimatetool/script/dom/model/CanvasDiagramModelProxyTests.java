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

import com.archimatetool.canvas.model.ICanvasFactory;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;

import junit.framework.JUnit4TestAdapter;


/**
 * CanvasDiagramModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
public class CanvasDiagramModelProxyTests extends DiagramModelProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CanvasDiagramModelProxyTests.class);
    }
    
    //protected ArchimateModelProxy testModelProxy;
    
    //private CanvasDiagramModelProxy actualTestProxy;
    
    private IArchimateModel model;
    
    @Before
    public void runOnceBeforeEachTest() {
        model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        
        testEObject = ICanvasFactory.eINSTANCE.createCanvasModel();
        model.getFolder(FolderType.DIAGRAMS).getElements().add(testEObject);
        
        testProxy = EObjectProxy.get(testEObject);
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