/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
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
import com.archimatetool.model.IDiagramModelBendpoint;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.util.ArchimateModelUtils;

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
        IDiagramModelBendpoint bp1 = IArchimateFactory.eINSTANCE.createDiagramModelBendpoint();
        bp1.setStartX(1);
        bp1.setEndX(2);
        bp1.setStartY(3);
        bp1.setEndY(4);
        
        IDiagramModelBendpoint bp2 = IArchimateFactory.eINSTANCE.createDiagramModelBendpoint();
        bp2.setStartX(5);
        bp2.setEndX(6);
        bp2.setStartY(7);
        bp2.setEndY(8);
        
        actualTestProxy.getEObject().getBendpoints().add(bp1);
        actualTestProxy.getEObject().getBendpoints().add(bp2);
        
        List<Map<String, Integer>> list = actualTestProxy.getRelativeBendpoints();
        assertEquals(2, list.size());
        
        Map<String, Integer> bpp1 = list.get(0);
        assertEquals(1, (int)bpp1.get(IModelConstants.START_X));
        assertEquals(2, (int)bpp1.get(IModelConstants.END_X));
        assertEquals(3, (int)bpp1.get(IModelConstants.START_Y));
        assertEquals(4, (int)bpp1.get(IModelConstants.END_Y));
        
        Map<String, Integer> bpp2 = list.get(1);
        assertEquals(5, (int)bpp2.get(IModelConstants.START_X));
        assertEquals(6, (int)bpp2.get(IModelConstants.END_X));
        assertEquals(7, (int)bpp2.get(IModelConstants.START_Y));
        assertEquals(8, (int)bpp2.get(IModelConstants.END_Y));
    }
    
    @Test
    public void addRelativeBendpoint() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(IModelConstants.START_X, 1);
        map.put(IModelConstants.END_X, 2);
        map.put(IModelConstants.START_Y, 3);
        map.put(IModelConstants.END_Y, 4);
        
        actualTestProxy.addRelativeBendpoint(map, 0);

        List<Map<String, Integer>> list = actualTestProxy.getRelativeBendpoints();
        assertEquals(1, list.size());
        
        Map<String, Integer> bpp1 = list.get(0);
        assertEquals(1, (int)bpp1.get(IModelConstants.START_X));
        assertEquals(2, (int)bpp1.get(IModelConstants.END_X));
        assertEquals(3, (int)bpp1.get(IModelConstants.START_Y));
        assertEquals(4, (int)bpp1.get(IModelConstants.END_Y));
    }
    
    @Test
    public void deleteAllBendpoints() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(IModelConstants.START_X, 1);
        map.put(IModelConstants.END_X, 2);
        map.put(IModelConstants.START_Y, 3);
        map.put(IModelConstants.END_Y, 4);
        
        actualTestProxy.addRelativeBendpoint(map, 0);
        actualTestProxy.addRelativeBendpoint(map, 1);

        assertEquals(2, actualTestProxy.getRelativeBendpoints().size());
        
        actualTestProxy.deleteAllBendpoints();
        assertEquals(0, actualTestProxy.getRelativeBendpoints().size());
    }
    
    @Test
    public void deleteBendpoint() {
        Map<String, Integer> map1 = new HashMap<String, Integer>();
        map1.put(IModelConstants.START_X, 1);
        map1.put(IModelConstants.END_X, 2);
        map1.put(IModelConstants.START_Y, 3);
        map1.put(IModelConstants.END_Y, 4);
        
        Map<String, Integer> map2 = new HashMap<String, Integer>();
        map2.put(IModelConstants.START_X, 5);
        map2.put(IModelConstants.END_X, 6);
        map2.put(IModelConstants.START_Y, 7);
        map2.put(IModelConstants.END_Y, 8);

        actualTestProxy.addRelativeBendpoint(map1, 0);
        actualTestProxy.addRelativeBendpoint(map2, 1);

        assertEquals(2, actualTestProxy.getRelativeBendpoints().size());
        
        actualTestProxy.deleteBendpoint(0);
        assertEquals(1, actualTestProxy.getRelativeBendpoints().size());
        
        Map<String, Integer> bpp1 = actualTestProxy.getRelativeBendpoints().get(0);
        assertEquals(5, (int)bpp1.get(IModelConstants.START_X));
        assertEquals(6, (int)bpp1.get(IModelConstants.END_X));
        assertEquals(7, (int)bpp1.get(IModelConstants.START_Y));
        assertEquals(8, (int)bpp1.get(IModelConstants.END_Y));
    }

}