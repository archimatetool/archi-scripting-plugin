/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IAssociationRelationship;
import com.archimatetool.model.IBusinessRole;

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
        assertFalse(collection.is("concepts"));
        assertFalse(collection.is("elements"));
        assertFalse(collection.is("relations"));
        assertFalse(collection.is("views"));
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
        
        
    }
    
}
