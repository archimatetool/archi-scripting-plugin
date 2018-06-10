/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EObject;
import org.junit.Test;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IAssociationRelationship;
import com.archimatetool.model.IBusinessRole;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;

import junit.framework.JUnit4TestAdapter;


/**
 * EObjectProxyCollection Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class EObjectProxyCollectionTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(EObjectProxyCollectionTests.class);
    }
    
    @Test
    public void first() {
        // Empty
        EObjectProxyCollection collection = new EObjectProxyCollection();
        assertNull(collection.first());
        
        // First
        EObjectProxy proxy1 = EObjectProxy.get(IArchimateFactory.eINSTANCE.createBusinessRole());
        EObjectProxy proxy2 = EObjectProxy.get(IArchimateFactory.eINSTANCE.createBusinessRole());
        collection.add(proxy1);
        collection.add(proxy2);
        assertSame(proxy1, collection.first());
    }
    
    @Test
    public void is() {
        // Empty
        EObjectProxyCollection collection = new EObjectProxyCollection();
        assertFalse(collection.is("concept"));
        assertFalse(collection.is("element"));
        assertFalse(collection.is("relation"));
        assertFalse(collection.is("view"));
        assertFalse(collection.is("folder"));
        assertFalse(collection.is("#123"));
        assertFalse(collection.is(".fred"));
        assertFalse(collection.is("business-role.fred"));
        assertFalse(collection.is("business-role"));
        
        IBusinessRole element = IArchimateFactory.eINSTANCE.createBusinessRole();
        element.setName("fred");
        element.setId("123");
        collection.add(EObjectProxy.get(element));
        assertTrue(collection.is("concept"));
        assertTrue(collection.is("element"));
        assertFalse(collection.is("relation"));
        assertTrue(collection.is("#123"));
        assertTrue(collection.is(".fred"));
        assertTrue(collection.is("business-role.fred"));
        assertTrue(collection.is("business-role"));

        IAssociationRelationship relation = IArchimateFactory.eINSTANCE.createAssociationRelationship();
        relation.setName("freda");
        relation.setId("1234");
        collection.add(EObjectProxy.get(relation));
        assertTrue(collection.is("concept"));
        assertTrue(collection.is("element"));
        assertTrue(collection.is("relation"));
        assertTrue(collection.is("#1234"));
        assertTrue(collection.is(".freda"));
        assertTrue(collection.is("association-relationship"));
        assertTrue(collection.is("association-relationship.freda"));
        
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        folder.setName("f");
        relation.setId("f1");
        collection.add(EObjectProxy.get(folder));
        assertTrue(collection.is("concept"));
        assertTrue(collection.is("element"));
        assertTrue(collection.is("relation"));
        assertTrue(collection.is("folder"));
        assertTrue(collection.is("#f1"));
        assertTrue(collection.is(".f"));
        assertTrue(collection.is("folder.f"));
        assertTrue(collection.is("association-relationship"));
        assertTrue(collection.is("association-relationship.freda"));
    }
    
    @Test
    public void find() {
        ArchimateModelProxy modelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        EObjectProxyCollection collection = new EObjectProxyCollection();
        
        // Get an Archimate Diagram
        EObject eObject1 = ArchimateModelUtils.getObjectByID(modelProxy.getEObject(), "4056");
        collection.add(EObjectProxy.get(eObject1));
        
        EObjectProxyCollection c = collection.find();
        assertEquals(65, c.size());

        // Add a Group inside the Diagram
        EObject eObject2 = ArchimateModelUtils.getObjectByID(modelProxy.getEObject(), "4116");
        collection.add(EObjectProxy.get(eObject2));
        
        // Should be the same number
        c = collection.find();
        assertEquals(65, c.size());
    }
    
    @Test
    public void cloneTest() {
        EObjectProxyCollection collection = new EObjectProxyCollection();
        collection.addUnique(EObjectProxy.get(IArchimateFactory.eINSTANCE.createBusinessRole()));
        collection.addUnique(EObjectProxy.get(IArchimateFactory.eINSTANCE.createAssignmentRelationship()));
        
        EObjectProxyCollection clone = (EObjectProxyCollection)collection.clone();
        for(int i = 0; i < collection.size(); i++) {
            assertEquals(collection.get(i), clone.get(i));
        }
    }    
    
    @Test
    public void addUnique() {
        EObjectProxyCollection collection = new EObjectProxyCollection();
        IBusinessRole element = IArchimateFactory.eINSTANCE.createBusinessRole();
        collection.addUnique(EObjectProxy.get(element));
        collection.addUnique(EObjectProxy.get(element));
        collection.addUnique(null);
        assertEquals(1, collection.size());
        assertEquals(EObjectProxy.get(element), collection.get(0));
    }
}
