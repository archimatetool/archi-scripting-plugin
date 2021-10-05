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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

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

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateDiagramModelObjectProxyTests extends DiagramModelObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ArchimateDiagramModelObjectProxyTests.class);
    }
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        
        testEObject = (IDiagramModelArchimateObject)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4104");
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (DiagramModelObjectProxy)testProxy;
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
        assertTrue(actualTestProxy.getConcept().getEObject() instanceof IBusinessService);
        IDiagramModelGroup group = (IDiagramModelGroup)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4096");
        DiagramModelObjectProxy groupProxy = new DiagramModelObjectProxy(group);
        assertNull(groupProxy.getConcept());
    }
    
    @Override
    @Test
    public void getReferencedConcept() {
        assertSame(actualTestProxy.getConcept().getEObject(), actualTestProxy.getReferencedConcept());
        
        // Group has none
        IDiagramModelGroup group = (IDiagramModelGroup)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4096");
        DiagramModelObjectProxy groupProxy = new DiagramModelObjectProxy(group);
        assertSame(groupProxy.getEObject(), groupProxy.getReferencedConcept());
    }
    
    @Override
    @Test
    public void outRels() {
        EObjectProxyCollection collection = actualTestProxy.outRels();
        assertEquals(1, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelConnectionProxy);
        }
    }

    @Override
    @Test
    public void inRels() {
        EObjectProxyCollection collection = actualTestProxy.inRels();
        assertEquals(1, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelConnectionProxy);
        }
    }
    
    @Test
    public void getBounds() {
        Map<String, Object> bounds = actualTestProxy.getBounds();
        assertEquals(20, bounds.get("x"));
        assertEquals(25, bounds.get("y"));
        assertEquals(101, bounds.get("width"));
        assertEquals(60, bounds.get("height"));
    }
    
    @Test
    public void setBoundsUsesPreferencesWidthAndHeight() {
        Map<String, Object> bounds = new HashMap<>();
        bounds.put("x", 10);
        bounds.put("y", 20);
        bounds.put("width", -1);
        bounds.put("height", -1);
        actualTestProxy.setBounds(bounds);
        
        bounds = actualTestProxy.getBounds();
        assertEquals(10, bounds.get("x"));
        assertEquals(20, bounds.get("y"));
        assertEquals(ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH), bounds.get("width"));
        assertEquals(ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT), bounds.get("height"));
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
        assertEquals("#0080c0", actualTestProxy.attr(IModelConstants.FILL_COLOR));
        actualTestProxy.attr(IModelConstants.FILL_COLOR, "#ffff80");
        assertEquals("#ffff80", actualTestProxy.attr(IModelConstants.FILL_COLOR));
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
        assertEquals(0, actualTestProxy.attr(IModelConstants.FIGURE_TYPE));
        actualTestProxy.attr(IModelConstants.FIGURE_TYPE, 1);
        assertEquals(1, actualTestProxy.attr(IModelConstants.FIGURE_TYPE));
        
        actualTestProxy.attr(IModelConstants.FIGURE_TYPE, -1);
        assertEquals(0, actualTestProxy.attr(IModelConstants.FIGURE_TYPE));
        
        actualTestProxy.attr(IModelConstants.FIGURE_TYPE, 2);
        assertEquals(1, actualTestProxy.attr(IModelConstants.FIGURE_TYPE));
    }
    
    @Test
    public void attr_FigureTypeCannotSet() {
        testEObject = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        ((IDiagramModelArchimateObject)testEObject).setArchimateElement(IArchimateFactory.eINSTANCE.createBusinessObject());
        actualTestProxy = (DiagramModelObjectProxy)EObjectProxy.get(testEObject);
        
        assertEquals(0, actualTestProxy.attr(IModelConstants.FIGURE_TYPE));
        actualTestProxy.attr(IModelConstants.FIGURE_TYPE, 1);
        assertEquals(0, actualTestProxy.attr(IModelConstants.FIGURE_TYPE));
    }
    
    @Override
    @Test
    public void attr_TextAlignment() {
        assertEquals(2, actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT));
        actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT, 4);
        assertEquals(4, actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT));
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
        assertEquals(0, actualTestProxy.attr(IModelConstants.SHOW_ICON));
        actualTestProxy.attr(IModelConstants.SHOW_ICON, 2);
        assertEquals(2, actualTestProxy.attr(IModelConstants.SHOW_ICON));
    }
    
    @Test
    public void attr_ImageSource() {
        assertEquals(0, actualTestProxy.attr(IModelConstants.IMAGE_SOURCE));
        actualTestProxy.attr(IModelConstants.IMAGE_SOURCE, 1);
        assertEquals(1, actualTestProxy.attr(IModelConstants.IMAGE_SOURCE));
    }

}