/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.ICompositionRelationship;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.TestFiles;


/**
 * DiagramModelConnectionProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelConnectionProxyTests extends DiagramModelComponentProxyTests {
    
    private IDiagramModelArchimateConnection testEObject;
    private DiagramModelConnectionProxy testProxy;
    
    @Override
    protected IDiagramModelArchimateConnection getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected DiagramModelConnectionProxy getTestProxy() {
        return testProxy;
    }

    @BeforeEach
    public void runOnceBeforeEachTest() {
        ArchimateModelProxy testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
        testEObject = (IDiagramModelArchimateConnection)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3847");
        testProxy = (DiagramModelConnectionProxy)EObjectProxy.get(testEObject);
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
        assertTrue(testProxy.getConcept().getEObject() instanceof ICompositionRelationship);
    }
    
    @Override
    @Test
    public void getReferencedEObject() {
        assertSame(testProxy.getConcept().getEObject(), testProxy.getReferencedEObject());
        
        IDiagramModelConnection connection = IArchimateFactory.eINSTANCE.createDiagramModelConnection();
        DiagramModelConnectionProxy connectionProxy = new DiagramModelConnectionProxy(connection);
        assertSame(connectionProxy.getEObject(), connectionProxy.getReferencedEObject());
    }
    
    @Test
    public void outRels() {
        EObjectProxyCollection collection = testProxy.outRels();
        assertEquals(0, collection.size());
    }

    @Test
    public void inRels() {
        EObjectProxyCollection collection = testProxy.inRels();
        assertEquals(0, collection.size());
    }
    
    @Override
    @Test
    public void children() {
        super.children();
        
        EObjectProxyCollection collection = testProxy.children();
        assertEquals(0, collection.size());
    }
    
    @Test
    public void getSource() {
        DiagramModelComponentProxy source = testProxy.getSource();
        assertTrue(source instanceof DiagramModelObjectProxy);
        assertEquals("3831", source.getId());
    }
    
    @Test
    public void getTarget() {
        DiagramModelComponentProxy source = testProxy.getTarget();
        assertTrue(source instanceof DiagramModelObjectProxy);
        assertEquals("3835", source.getId());
    }
    
    @Test
    public void attr_LabelVisible() {
        assertTrue((boolean)testProxy.attr(LABEL_VISIBLE));
        testProxy.attr(LABEL_VISIBLE, false);
        assertFalse((boolean)testProxy.attr(LABEL_VISIBLE));
    }
    
    @Test
    public void attr_TextPosition() {
        assertEquals(IDiagramModelConnection.CONNECTION_TEXT_POSITION_MIDDLE, testProxy.attr(TEXT_POSITION));
        testProxy.attr(TEXT_POSITION, 0);
        assertEquals(0, testProxy.attr(TEXT_POSITION));
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
        
        assertEquals(0, proxy.attr(STYLE));
        proxy.attr(STYLE, 4);
        assertEquals(4, proxy.attr(STYLE));
    }
    
    @Test
    public void attr_Style_NotArchiMateConnection( ) {
        assertEquals(0, testProxy.attr(STYLE));
        
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.attr(STYLE, 4);
        });
    }
    
    @Test
    public void attr_TextAlignment() {
        assertEquals(2, testProxy.attr(TEXT_ALIGNMENT));
        testProxy.attr(TEXT_ALIGNMENT, 4);
        assertEquals(4, testProxy.attr(TEXT_ALIGNMENT));
    }
    
    @Test
    public void attr_Source() {
        assertEquals("3831", ((EObjectProxy)testProxy.attr(SOURCE)).getId());
    }

    @Test
    public void attr_Target() {
        assertEquals("3835", ((EObjectProxy)testProxy.attr(TARGET)).getId());
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
        testProxy.addRelativeBendpoint(map, 0);
        testProxy.addRelativeBendpoint(map2, 1);
        
        List<Map<String, Object>> list = testProxy.getRelativeBendpoints();
        assertEquals(2, list.size());
        
        checkBendpoint(map, list.get(0));
        checkBendpoint(map2, list.get(1));
    }
    
    @Test
    public void addRelativeBendpoint() {
        Map<String, Object> map = createBendpoint(1, 2, 3, 4);
        testProxy.addRelativeBendpoint(map, 0);

        List<Map<String, Object>> list = testProxy.getRelativeBendpoints();
        assertEquals(1, list.size());
        checkBendpoint(map, list.get(0));
    }
    
    @Test
    public void setRelativeBendpoint() {
        Map<String, Object> map = createBendpoint(1, 2, 3, 4);
        testProxy.addRelativeBendpoint(map, 0);

        List<Map<String, Object>> list = testProxy.getRelativeBendpoints();
        assertEquals(1, list.size());
        checkBendpoint(map, list.get(0));
        
        Map<String, Object> map2 = createBendpoint(5, 6, 7, 8);
        testProxy.setRelativeBendpoint(map2, 0);
        assertEquals(1, list.size());
        checkBendpoint(map2, testProxy.getRelativeBendpoints().get(0));
    }
    
   @Test
    public void deleteAllBendpoints() {
        Map<String, Object> map = createBendpoint(1, 2, 3, 4);
        
        testProxy.addRelativeBendpoint(map, 0);
        testProxy.addRelativeBendpoint(map, 1);

        assertEquals(2, testProxy.getRelativeBendpoints().size());
        
        testProxy.deleteAllBendpoints();
        assertEquals(0, testProxy.getRelativeBendpoints().size());
    }
    
    @Test
    public void deleteBendpoint() {
        Map<String, Object> map1 = createBendpoint(1, 2, 3, 4);
        Map<String, Object> map2 = createBendpoint(5, 6, 7, 8);

        testProxy.addRelativeBendpoint(map1, 0);
        testProxy.addRelativeBendpoint(map2, 1);

        assertEquals(2, testProxy.getRelativeBendpoints().size());
        
        testProxy.deleteBendpoint(0);
        
        assertEquals(1, testProxy.getRelativeBendpoints().size());
        
        checkBendpoint(map2, testProxy.getRelativeBendpoints().get(0));
    }

    private Map<String, Object> createBendpoint(int startX, int endX, int startY, int endY) {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put(START_X, startX);
        map.put(END_X, endX);
        map.put(START_Y, startY);
        map.put(END_Y, endY);
        
        return map;
    }
    
    private void checkBendpoint(Map<String, Object> expected, Map<String, Object> actual) {
        assertEquals(expected.get(START_X), actual.get(START_X));
        assertEquals(expected.get(END_X), actual.get(END_X));
        assertEquals(expected.get(START_Y), actual.get(START_Y));
        assertEquals(expected.get(END_Y), actual.get(END_Y));
    }
}