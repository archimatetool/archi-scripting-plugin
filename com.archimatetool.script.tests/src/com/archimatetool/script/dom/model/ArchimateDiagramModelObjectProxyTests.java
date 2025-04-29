/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.preferences.IPreferenceConstants;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IBusinessService;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.TestFiles;


/**
 * DiagramModelObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateDiagramModelObjectProxyTests extends DiagramModelObjectProxyTests {
    
    private ArchimateModelProxy testModelProxy;
    private IDiagramModelArchimateObject testEObject;
    private DiagramModelObjectProxy testProxy;
    
    @Override
    protected IDiagramModelArchimateObject getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected DiagramModelObjectProxy getTestProxy() {
        return testProxy;
    }

    @BeforeEach
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
        testEObject = (IDiagramModelArchimateObject)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4104");
        testProxy = (DiagramModelObjectProxy)EObjectProxy.get(testEObject);
    }

    @Override
    @Test
    public void parent() {
        EObjectProxy parent = testProxy.parent();
        assertEquals("4096", parent.getId());
    }

    @Override
    @Test
    public void parents() {
        EObjectProxyCollection collection = testProxy.parents();
        
        assertEquals(3, collection.size());
        assertEquals("4096", collection.get(0).getId());
        assertEquals("4056", collection.get(1).getId());
        assertEquals("e64e9b49", collection.get(2).getId());
    }
    
    @Override
    @Test
    public void find() {
        EObjectProxyCollection collection = testProxy.find();
        assertEquals(0, collection.size());
    }
    
    @Override
    @Test
    public void getConcept() {
        assertTrue(testProxy.getConcept().getEObject() instanceof IBusinessService);
        IDiagramModelGroup group = (IDiagramModelGroup)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4096");
        DiagramModelObjectProxy groupProxy = new DiagramModelObjectProxy(group);
        assertNull(groupProxy.getConcept());
    }
    
    @Override
    @Test
    public void getReferencedEObject() {
        assertSame(testProxy.getConcept().getEObject(), testProxy.getReferencedEObject());
        
        // Group has none
        IDiagramModelGroup group = (IDiagramModelGroup)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4096");
        DiagramModelObjectProxy groupProxy = new DiagramModelObjectProxy(group);
        assertSame(groupProxy.getEObject(), groupProxy.getReferencedEObject());
    }
    
    @Override
    @Test
    public void outRels() {
        EObjectProxyCollection collection = testProxy.outRels();
        assertEquals(1, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelConnectionProxy);
        }
    }

    @Override
    @Test
    public void inRels() {
        EObjectProxyCollection collection = testProxy.inRels();
        assertEquals(1, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelConnectionProxy);
        }
    }
    
    @Test
    public void getBounds() {
        Map<String, Object> bounds = testProxy.getBounds();
        assertEquals(20, bounds.get("x"));
        assertEquals(43, bounds.get("y"));
        assertEquals(101, bounds.get("width"));
        assertEquals(60, bounds.get("height"));
    }
    
    @Test
    public void setBoundsUsesPreferencesWidthAndHeight() {
        Map<String, Object> bounds = Map.of("x", 10, "y", 20, "width", -1, "height", -1);
        testProxy.setBounds(bounds);
        
        bounds = testProxy.getBounds();
        assertEquals(10, bounds.get("x"));
        assertEquals(20, bounds.get("y"));
        assertEquals(ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH), bounds.get("width"));
        assertEquals(ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT), bounds.get("height"));
    }
    
    @Test
    public void setBoundsThrowsExceptionIfZeroOrLess() {
        assertThrows(ArchiScriptException.class, () -> {
            Map<String, Object> bounds = Map.of("x", 10, "y", 20, "width", 10, "height", 0);
            testProxy.setBounds(bounds);
        });

        assertThrows(ArchiScriptException.class, () -> {
            Map<String, Object> bounds = Map.of("x", 10, "y", 20, "width", -2, "height", 10);
            testProxy.setBounds(bounds);
        });
    }
    
    @Test
    public void sendToBack() {
        assertEquals(1, testProxy.getIndex());
        testProxy.sendToBack();
        assertEquals(0, testProxy.getIndex());
    }
    
    @Test
    public void sendBackward() {
        assertEquals(1, testProxy.getIndex());
        testProxy.sendBackward();
        assertEquals(0, testProxy.getIndex());
    }
    
    @Test
    public void bringToFront() {
        assertEquals(1, testProxy.getIndex());
        testProxy.bringToFront();
        assertEquals(2, testProxy.getIndex());
    }

    @Test
    public void bringForward() {
        assertEquals(1, testProxy.getIndex());
        testProxy.bringToFront();
        assertEquals(2, testProxy.getIndex());
    }

    @Override
    @Test
    public void index() {
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.setIndex(-2);
        });
        
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.setIndex(testProxy.parent().children().size() + 1);
        });
        
        assertEquals(1, testProxy.getIndex());
        assertEquals(1, testProxy.attr(IModelConstants.INDEX));
        
        testProxy.setIndex(0);
        assertEquals(0, testProxy.getIndex());
        assertEquals(0, testProxy.attr(IModelConstants.INDEX));
        
        testProxy.attr(IModelConstants.INDEX, 2);
        assertEquals(2, testProxy.getIndex());
        assertEquals(2, testProxy.attr(IModelConstants.INDEX));
        
        // -1 is end of list
        testProxy.setIndex(-1);
        assertEquals(2, testProxy.getIndex());
    }
    
    @Override
    @Test
    public void children() {
        super.children();
        
        IDiagramModelObject dmo = (IDiagramModelObject)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3707");
        DiagramModelObjectProxy proxy = new DiagramModelObjectProxy(dmo);
        
        EObjectProxyCollection collection = proxy.children();
        assertEquals(6, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelObjectProxy);
        }
    }
    
    @Test
    public void attr_FillColor() {
        assertEquals("#0080c0", testProxy.attr(IModelConstants.FILL_COLOR));
        testProxy.attr(IModelConstants.FILL_COLOR, "#ffff80");
        assertEquals("#ffff80", testProxy.attr(IModelConstants.FILL_COLOR));
    }
    
    @Test
    public void attr_FillColorNull() {
        assertEquals("#0080c0", testProxy.attr(IModelConstants.FILL_COLOR));
        testProxy.attr(IModelConstants.FILL_COLOR, null);
        assertEquals(null, testProxy.attr(IModelConstants.FILL_COLOR));
    }

    @Override
    @Test
    public void attr_Bounds() {
        IDiagramModelObject dmo = (IDiagramModelObject)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3707");
        DiagramModelObjectProxy proxy = new DiagramModelObjectProxy(dmo);

        Map<?, ?> bounds = (Map<?, ?>)proxy.attr(IModelConstants.BOUNDS);
        assertEquals(20, bounds.get("x"));
        assertEquals(20, bounds.get("y"));
        assertEquals(440, bounds.get("width"));
        assertEquals(500, bounds.get("height"));
    }
    
    @Test
    public void attr_FigureTypeCanSet() {
        assertEquals(0, testProxy.attr(IModelConstants.FIGURE_TYPE));
        testProxy.attr(IModelConstants.FIGURE_TYPE, 1);
        assertEquals(1, testProxy.attr(IModelConstants.FIGURE_TYPE));
        
        testProxy.attr(IModelConstants.FIGURE_TYPE, -1);
        assertEquals(0, testProxy.attr(IModelConstants.FIGURE_TYPE));
        
        testProxy.attr(IModelConstants.FIGURE_TYPE, 2);
        assertEquals(1, testProxy.attr(IModelConstants.FIGURE_TYPE));
    }
    
    @Override
    @Test
    public void attr_TextAlignment() {
        assertEquals(2, testProxy.attr(IModelConstants.TEXT_ALIGNMENT));
        testProxy.attr(IModelConstants.TEXT_ALIGNMENT, 4);
        assertEquals(4, testProxy.attr(IModelConstants.TEXT_ALIGNMENT));
    }
    
    @Override
    @Test
    public void delete() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        IArchimateDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        model.getDefaultFolderForObject(dm).getElements().add(dm);
        
        IDiagramModelGroup group1 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dm.getChildren().add(group1);
        IDiagramModelGroup group2 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dm.getChildren().add(group2);
        IDiagramModelGroup group3 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        group2.getChildren().add(group3);
        IDiagramModelGroup group4 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        group3.getChildren().add(group4);

        IDiagramModelConnection connection1 = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        IDiagramModelConnection connection2 = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        IDiagramModelConnection connection3 = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        IDiagramModelConnection connection4 = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        IDiagramModelConnection connection5 = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        IDiagramModelConnection connection6 = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        IDiagramModelConnection connection7 = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        
        connection1.connect(group1, group2);
        connection2.connect(group3, connection1);
        connection3.connect(connection1, group4);
        connection4.connect(connection2, group4);
        connection5.connect(group3, connection3);
        connection6.connect(group3, connection4);
        connection7.connect(connection5, group4);
        
        assertSame(connection1, group1.getSourceConnections().get(0));
        assertEquals(1, connection1.getSourceConnections().size());
        assertEquals(1, connection1.getTargetConnections().size());

        DiagramModelObjectProxy proxy1 = (DiagramModelObjectProxy)EObjectProxy.get(group1);
        proxy1.delete();
        
        DiagramModelObjectProxy proxy2 = (DiagramModelObjectProxy)EObjectProxy.get(group2);
        proxy2.delete();
        
        assertNull(group1.eContainer());
        assertNull(group2.eContainer());
        assertNull(group3.eContainer());
        assertNull(group4.eContainer());
        
        assertTrue(group1.getSourceConnections().isEmpty());
        assertTrue(group1.getTargetConnections().isEmpty());
        assertTrue(group2.getSourceConnections().isEmpty());
        assertTrue(group2.getTargetConnections().isEmpty());
        assertTrue(group3.getSourceConnections().isEmpty());
        assertTrue(group3.getTargetConnections().isEmpty());
        assertTrue(group4.getSourceConnections().isEmpty());
        assertTrue(group4.getTargetConnections().isEmpty());

        assertTrue(connection1.getSourceConnections().isEmpty());
        assertTrue(connection1.getTargetConnections().isEmpty());
        assertTrue(connection2.getSourceConnections().isEmpty());
        assertTrue(connection2.getTargetConnections().isEmpty());
        assertTrue(connection3.getSourceConnections().isEmpty());
        assertTrue(connection3.getTargetConnections().isEmpty());
        assertTrue(connection4.getSourceConnections().isEmpty());
        assertTrue(connection4.getTargetConnections().isEmpty());
        assertTrue(connection5.getSourceConnections().isEmpty());
        assertTrue(connection5.getTargetConnections().isEmpty());
        assertTrue(connection6.getSourceConnections().isEmpty());
        assertTrue(connection6.getTargetConnections().isEmpty());
        assertTrue(connection7.getSourceConnections().isEmpty());
        assertTrue(connection7.getTargetConnections().isEmpty());
    }
    
    @Test
    public void attr_ShowIcon() {
        assertEquals(0, testProxy.attr(IModelConstants.SHOW_ICON));
        testProxy.attr(IModelConstants.SHOW_ICON, 2);
        assertEquals(2, testProxy.attr(IModelConstants.SHOW_ICON));
    }
    
    @Test
    public void attr_ImageSource() {
        assertEquals(0, testProxy.attr(IModelConstants.IMAGE_SOURCE));
        testProxy.attr(IModelConstants.IMAGE_SOURCE, 1);
        assertEquals(1, testProxy.attr(IModelConstants.IMAGE_SOURCE));
    }

    @Test
    public void attr_IconColor() {
        assertEquals("", testProxy.attr(IModelConstants.ICON_COLOR));
        testProxy.attr(IModelConstants.ICON_COLOR, "#121212");
        assertEquals("#121212", testProxy.attr(IModelConstants.ICON_COLOR));
    }
    
    @Test
    public void attr_IconColor_NotForJunction() {
        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateElement(IArchimateFactory.eINSTANCE.createJunction());
        EObjectProxy proxy = EObjectProxy.get(dmo);
        proxy.attr(IModelConstants.ICON_COLOR, "#121212");
        assertEquals("", proxy.attr(IModelConstants.ICON_COLOR));
    }
}