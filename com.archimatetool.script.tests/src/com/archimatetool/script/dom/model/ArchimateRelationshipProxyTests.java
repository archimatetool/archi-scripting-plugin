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
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IAssociationRelationship;
import com.archimatetool.model.IInfluenceRelationship;
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
        testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        
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

    @Override
    @Test
    public void delete() {
        assertEquals(1, actualTestProxy.objectRefs().size());
        assertEquals(0, actualTestProxy.inRels().size());
        assertEquals(0, actualTestProxy.outRels().size());
        
        actualTestProxy.delete();

        assertNull(testProxy.getModel());
        assertEquals(0, actualTestProxy.objectRefs().size());
        assertEquals(0, actualTestProxy.inRels().size());
        assertEquals(0, actualTestProxy.outRels().size());
    }

    @Test
    public void setType() {
        // TODO a proper test on object in test model

        assertTrue(actualTestProxy.getEObject() instanceof IAccessRelationship);
        assertEquals(0, actualTestProxy.outRels().size());
        assertEquals(0, actualTestProxy.inRels().size());
        assertEquals(1, actualTestProxy.objectRefs().size());

        ArchimateRelationshipProxy newElementProxy = actualTestProxy.setType("association-relationship");
        
        assertSame(newElementProxy, testProxy);
        assertTrue(newElementProxy.getEObject() instanceof IAssociationRelationship);
        assertEquals(0, actualTestProxy.outRels().size());
        assertEquals(0, actualTestProxy.inRels().size());
        assertEquals(1, actualTestProxy.objectRefs().size());
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
    
    @Test
    public void objectRefs() {
        EObjectProxyCollection collection = actualTestProxy.objectRefs();
        assertEquals(1, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelConnectionProxy);
        }
    }
    
    @Test
    public void viewRefs() {
        EObjectProxyCollection collection = actualTestProxy.viewRefs();
        assertEquals(1, collection.size());
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelProxy);
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

    @Test
    public void setAccessType() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        IAccessRelationship relationship = IArchimateFactory.eINSTANCE.createAccessRelationship();
        model.getDefaultFolderForObject(relationship).getElements().add(relationship);
        ArchimateRelationshipProxy proxy = (ArchimateRelationshipProxy)EObjectProxy.get(relationship);
        
        proxy.setAccessType("write");
        assertEquals("write", proxy.getAccessType());
        assertEquals(IAccessRelationship.WRITE_ACCESS, relationship.getAccessType());
        
        proxy.setAccessType("read");
        assertEquals("read", proxy.getAccessType());
        assertEquals(IAccessRelationship.READ_ACCESS, relationship.getAccessType());
        
        proxy.setAccessType("access");
        assertEquals("access", proxy.getAccessType());
        assertEquals(IAccessRelationship.UNSPECIFIED_ACCESS, relationship.getAccessType());
        
        proxy.setAccessType("readwrite");
        assertEquals("readwrite", proxy.getAccessType());
        assertEquals(IAccessRelationship.READ_WRITE_ACCESS, relationship.getAccessType());
        
        proxy.setAccessType("rubbish");
        assertEquals("readwrite", proxy.getAccessType());
        assertEquals(IAccessRelationship.READ_WRITE_ACCESS, relationship.getAccessType());
    }

    @Test
    public void setInfluenceStrength() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        IInfluenceRelationship relationship = IArchimateFactory.eINSTANCE.createInfluenceRelationship();
        model.getDefaultFolderForObject(relationship).getElements().add(relationship);
        ArchimateRelationshipProxy proxy = (ArchimateRelationshipProxy)EObjectProxy.get(relationship);
        
        proxy.setInfluenceStrength("+++");
        assertEquals("+++", relationship.getStrength());
        assertEquals("+++", proxy.getInfluenceStrength());
    }
}