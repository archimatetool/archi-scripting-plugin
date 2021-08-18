/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.ICompositionRelationship;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelConnectionProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelConnectionProxyTests extends DiagramModelComponentProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DiagramModelConnectionProxyTests.class);
    }
    
    private DiagramModelConnectionProxy actualTestProxy;
    
    private ArchimateModelProxy testModelProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        
        testEObject = (IDiagramModelArchimateConnection)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3847");
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (DiagramModelConnectionProxy)testProxy;
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof DiagramModelConnectionProxy);
        
        proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createDiagramModelConnection());
        assertTrue(proxy instanceof DiagramModelConnectionProxy);
    }

    @Override
    @Test
    public void parent() {
        EObjectProxy parent = testProxy.parent();
        assertEquals("3831", parent.getId());
    }

    @Override
    @Test
    public void parents() {
        // Find a diagram object
        EObjectProxyCollection collection = testProxy.parents();
        
        assertEquals(3, collection.size());
        assertEquals("3831", collection.get(0).getId());
        assertEquals("3821", collection.get(1).getId());
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
    public void find_Selector() {
        super.find_Selector();
        
        EObjectProxyCollection collection = testProxy.find("garbage");
        assertEquals(0, collection.size());

        collection = testProxy.find("*");
        assertEquals(0, collection.size());
    }

    @Override
    @Test
    public void getConcept() {
        assertTrue(actualTestProxy.getConcept().getEObject() instanceof ICompositionRelationship);
    }
    
    @Override
    @Test
    public void getReferencedConcept() {
        assertSame(actualTestProxy.getConcept().getEObject(), actualTestProxy.getReferencedConcept());
        
        IDiagramModelConnection connection = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        DiagramModelConnectionProxy connectionProxy = new DiagramModelConnectionProxy(connection);
        assertSame(connectionProxy.getEObject(), connectionProxy.getReferencedConcept());
    }
    
    @Test
    public void outRels() {
        EObjectProxyCollection collection = actualTestProxy.outRels();
        assertEquals(0, collection.size());
    }

    @Test
    public void inRels() {
        EObjectProxyCollection collection = actualTestProxy.inRels();
        assertEquals(0, collection.size());
    }
    
    @Override
    @Test
    public void children() {
        super.children();
        
        EObjectProxyCollection collection = actualTestProxy.children();
        assertEquals(0, collection.size());
    }
    
    @Test
    public void getSource() {
        DiagramModelComponentProxy source = actualTestProxy.getSource();
        assertTrue(source instanceof DiagramModelObjectProxy);
        assertEquals("3831", source.getId());
    }
    
    @Test
    public void getTarget() {
        DiagramModelComponentProxy source = actualTestProxy.getTarget();
        assertTrue(source instanceof DiagramModelObjectProxy);
        assertEquals("3835", source.getId());
    }
    
    @Test
    public void attr_LineWidth() {
        assertEquals(1, actualTestProxy.attr(IModelConstants.LINE_WIDTH));
        actualTestProxy.attr(IModelConstants.LINE_WIDTH, 4);
        assertEquals(3, actualTestProxy.attr(IModelConstants.LINE_WIDTH));
    }
    
    @Test
    public void attr_LabelVisible() {
        assertTrue((boolean)actualTestProxy.attr(IModelConstants.LABEL_VISIBLE));
        actualTestProxy.attr(IModelConstants.LABEL_VISIBLE, false);
        assertFalse((boolean)actualTestProxy.attr(IModelConstants.LABEL_VISIBLE));
    }
    
    @Test
    public void attr_TextPosition() {
        assertEquals(IDiagramModelConnection.CONNECTION_TEXT_POSITION_MIDDLE, actualTestProxy.attr(IModelConstants.TEXT_POSITION));
        actualTestProxy.attr(IModelConstants.TEXT_POSITION, 0);
        assertEquals(0, actualTestProxy.attr(IModelConstants.TEXT_POSITION));
    }

    @Test
    public void attr_Style( ) {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        IArchimateDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        model.getDefaultFolderForObject(dm).getElements().add(dm);

        IDiagramModelGroup group1 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        IDiagramModelGroup group2 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        
        dm.getChildren().add(group1);
        dm.getChildren().add(group2);

        IDiagramModelConnection dmc = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        dmc.connect(group1, group2);
        DiagramModelConnectionProxy proxy = (DiagramModelConnectionProxy)EObjectProxy.get(dmc);
        
        assertEquals(0, proxy.attr(IModelConstants.STYLE));
        proxy.attr(IModelConstants.STYLE, 4);
        assertEquals(4, proxy.attr(IModelConstants.STYLE));
    }
    
    @Test(expected=ArchiScriptException.class)
    public void attr_Style_NotArchiMateConnection( ) {
        assertEquals(0, actualTestProxy.attr(IModelConstants.STYLE));
        actualTestProxy.attr(IModelConstants.STYLE, 4);
        assertEquals(4, actualTestProxy.attr(IModelConstants.STYLE));
    }

    @Test
    public void attr_Source() {
        assertEquals("3831", ((EObjectProxy)actualTestProxy.attr(IModelConstants.SOURCE)).getId());
    }

    @Test
    public void attr_Target() {
        assertEquals("3835", ((EObjectProxy)actualTestProxy.attr(IModelConstants.TARGET)).getId());
    }

    @Override
    @Test
    public void delete() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        IArchimateDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        model.getDefaultFolderForObject(dm).getElements().add(dm);

        IDiagramModelGroup group1 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        IDiagramModelGroup group2 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        IDiagramModelGroup group3 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        IDiagramModelGroup group4 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();

        dm.getChildren().add(group1);
        dm.getChildren().add(group2);
        dm.getChildren().add(group3);
        dm.getChildren().add(group4);
        
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

        DiagramModelConnectionProxy proxy = (DiagramModelConnectionProxy)EObjectProxy.get(connection1);
        proxy.delete();
        
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
    public void getRelativeBendpoints() {
        Map<String, Object> map = createBendpoint(1, 2, 3, 4);
        Map<String, Object> map2 = createBendpoint(5, 6, 7, 8);
        actualTestProxy.addRelativeBendpoint(map, 0);
        actualTestProxy.addRelativeBendpoint(map2, 1);
        
        List<Map<String, Object>> list = actualTestProxy.getRelativeBendpoints();
        assertEquals(2, list.size());
        
        checkBendpoint(map, list.get(0));
        checkBendpoint(map2, list.get(1));
    }
    
    @Test
    public void addRelativeBendpoint() {
        Map<String, Object> map = createBendpoint(1, 2, 3, 4);
        actualTestProxy.addRelativeBendpoint(map, 0);

        List<Map<String, Object>> list = actualTestProxy.getRelativeBendpoints();
        assertEquals(1, list.size());
        checkBendpoint(map, list.get(0));
    }
    
    @Test
    public void setRelativeBendpoint() {
        Map<String, Object> map = createBendpoint(1, 2, 3, 4);
        actualTestProxy.addRelativeBendpoint(map, 0);

        List<Map<String, Object>> list = actualTestProxy.getRelativeBendpoints();
        assertEquals(1, list.size());
        checkBendpoint(map, list.get(0));
        
        Map<String, Object> map2 = createBendpoint(5, 6, 7, 8);
        actualTestProxy.setRelativeBendpoint(map2, 0);
        assertEquals(1, list.size());
        checkBendpoint(map2, actualTestProxy.getRelativeBendpoints().get(0));
    }
    
   @Test
    public void deleteAllBendpoints() {
        Map<String, Object> map = createBendpoint(1, 2, 3, 4);
        
        actualTestProxy.addRelativeBendpoint(map, 0);
        actualTestProxy.addRelativeBendpoint(map, 1);

        assertEquals(2, actualTestProxy.getRelativeBendpoints().size());
        
        actualTestProxy.deleteAllBendpoints();
        assertEquals(0, actualTestProxy.getRelativeBendpoints().size());
    }
    
    @Test
    public void deleteBendpoint() {
        Map<String, Object> map1 = createBendpoint(1, 2, 3, 4);
        Map<String, Object> map2 = createBendpoint(5, 6, 7, 8);

        actualTestProxy.addRelativeBendpoint(map1, 0);
        actualTestProxy.addRelativeBendpoint(map2, 1);

        assertEquals(2, actualTestProxy.getRelativeBendpoints().size());
        
        actualTestProxy.deleteBendpoint(0);
        
        assertEquals(1, actualTestProxy.getRelativeBendpoints().size());
        
        checkBendpoint(map2, actualTestProxy.getRelativeBendpoints().get(0));
    }

    private Map<String, Object> createBendpoint(int startX, int endX, int startY, int endY) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put(IModelConstants.START_X, startX);
        map.put(IModelConstants.END_X, endX);
        map.put(IModelConstants.START_Y, startY);
        map.put(IModelConstants.END_Y, endY);
        
        return map;
    }
    
    private void checkBendpoint(Map<String, Object> expected, Map<String, Object> actual) {
        assertEquals(expected.get(IModelConstants.START_X), actual.get(IModelConstants.START_X));
        assertEquals(expected.get(IModelConstants.END_X), actual.get(IModelConstants.END_X));
        assertEquals(expected.get(IModelConstants.START_Y), actual.get(IModelConstants.START_Y));
        assertEquals(expected.get(IModelConstants.END_Y), actual.get(IModelConstants.END_Y));
    }
}