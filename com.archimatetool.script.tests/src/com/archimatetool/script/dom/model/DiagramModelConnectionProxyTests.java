/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IAssociationRelationship;
import com.archimatetool.model.ICompositionRelationship;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelConnection;
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
        testModelProxy = TestsHelper.loadTestModel();
        
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
    public void setArchimateConcept() {
        assertTrue(actualTestProxy.getArchimateConcept().getEObject() instanceof ICompositionRelationship);
        
        IAssociationRelationship relation = IArchimateFactory.eINSTANCE.createAssociationRelationship();
        ArchimateRelationshipProxy relationProxy = new ArchimateRelationshipProxy(relation);
        actualTestProxy.setArchimateConcept(relationProxy);
        
        assertTrue(actualTestProxy.getArchimateConcept().getEObject() == relation);
    }

    @Test
    public void getArchimateConcept() {
        assertTrue(actualTestProxy.getArchimateConcept().getEObject() instanceof ICompositionRelationship);
    }
    
    @Override
    @Test
    public void getReferencedConcept() {
        assertSame(actualTestProxy.getArchimateConcept().getEObject(), actualTestProxy.getReferencedConcept());
        
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
    
    @Override
    @Test
    public void attr_Get() {
        super.attr_Get();
        
        assertEquals(null, actualTestProxy.attr(IModelConstants.FONT_COLOR));
        assertEquals(null, actualTestProxy.attr(IModelConstants.FONT));
        assertEquals(null, actualTestProxy.attr(IModelConstants.LINE_COLOR));
        assertEquals(1, actualTestProxy.attr(IModelConstants.LINE_WIDTH));
        
        assertEquals("3831", ((EObjectProxy)actualTestProxy.attr(IModelConstants.SOURCE)).getId());
        assertEquals("3835", ((EObjectProxy)actualTestProxy.attr(IModelConstants.TARGET)).getId());
    }

}