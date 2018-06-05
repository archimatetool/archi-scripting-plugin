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

import com.archimatetool.model.IAccessRelationship;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IAssociationRelationship;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;

import junit.framework.JUnit4TestAdapter;


/**
 * ArchimateRelationshipProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateRelationshipProxyTests extends ArchimateConceptProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ArchimateRelationshipProxyTests.class);
    }
    
    private ArchimateRelationshipProxy actualTestProxy;
    
    private ArchimateModelProxy testModelProxy;
    
    @Before
    public void runOnceBeforeEachTest() {
        testModelProxy = loadTestModel();
        
        testEObject = (IArchimateRelationship)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "882");
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (ArchimateRelationshipProxy)testProxy;
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createAccessRelationship());
        assertTrue(proxy instanceof ArchimateRelationshipProxy);
    }

    @Override
    @Test
    public void getModel() {
        assertEquals(testModelProxy, testProxy.getModel());
    }
    
    @Override
    @Test
    public void parent() {
        EObjectProxy object = testProxy.parent();
        assertTrue(object instanceof FolderProxy);
    }

    @Override
    @Test
    public void parents() {
        EObjectProxyCollection collection = testProxy.parents();
        assertEquals(3, collection.size());
    }

    @Test
    public void delete() {
        ArchimateElementProxy proxy = testModelProxy.addElement("BusinessActor", "Fred");
        assertTrue(proxy.getEObject().eContainer() instanceof IFolder);
        
        proxy.delete();
        assertNull(proxy.getEObject().eContainer());
    }

    @Test
    public void setType() {
        assertTrue(actualTestProxy.getEObject() instanceof IAccessRelationship);
        assertEquals(0, actualTestProxy.getSourceRelationships().size());
        assertEquals(0, actualTestProxy.getTargetRelationships().size());
        assertEquals(1, actualTestProxy.getDiagramComponentInstances().size());

        ArchimateRelationshipProxy newElementProxy = actualTestProxy.setType("AssociationRelationship");
        
        assertSame(newElementProxy, testProxy);
        assertTrue(newElementProxy.getEObject() instanceof IAssociationRelationship);
        assertEquals(0, actualTestProxy.getSourceRelationships().size());
        assertEquals(0, actualTestProxy.getTargetRelationships().size());
        assertEquals(1, actualTestProxy.getDiagramComponentInstances().size());
    }

    @Test
    public void getSourceRelationships() {
        EObjectProxyCollection collection = actualTestProxy.getSourceRelationships();
        assertEquals(0, collection.size());
    }

    @Test
    public void getTargetRelationships() {
        EObjectProxyCollection collection = actualTestProxy.getTargetRelationships();
        assertEquals(0, collection.size());
    }
    
    @Test
    public void getDiagramComponentInstances() {
        EObjectProxyCollection collection = actualTestProxy.getDiagramComponentInstances();
        assertEquals(1, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelConnectionProxy);
        }
    }
    
    @Test
    public void setSource() {
        ArchimateConceptProxy proxy = (ArchimateConceptProxy)EObjectProxy.get(IArchimateFactory.eINSTANCE.createApplicationComponent());
        actualTestProxy.setSource(proxy);
        assertEquals(proxy.getEObject(), actualTestProxy.getSource().getEObject());
    }

    @Test
    public void setTarget() {
        ArchimateConceptProxy proxy = (ArchimateConceptProxy)EObjectProxy.get(IArchimateFactory.eINSTANCE.createArtifact());
        actualTestProxy.setTarget(proxy);
        assertEquals(proxy.getEObject(), actualTestProxy.getTarget().getEObject());
    }
    
    @Test(expected = ArchiScriptException.class)
    public void setSource_Bogus() {
        ArchimateConceptProxy proxy = (ArchimateConceptProxy)EObjectProxy.get(IArchimateFactory.eINSTANCE.createRequirement());
        actualTestProxy.setSource(proxy);
    }

    @Test(expected = ArchiScriptException.class)
    public void setTarget_Bogus() {
        ArchimateConceptProxy proxy = (ArchimateConceptProxy)EObjectProxy.get(IArchimateFactory.eINSTANCE.createRequirement());
        actualTestProxy.setTarget(proxy);
    }

}