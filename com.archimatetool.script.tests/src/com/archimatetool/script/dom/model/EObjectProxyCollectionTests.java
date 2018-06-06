/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.archimatetool.model.IArchimateFactory;

import junit.framework.JUnit4TestAdapter;


/**
 * EObjectProxyCollection Tests
 * 
 * @author Phillip Beauvoir
 */
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
        assertEquals(proxy1, collection.first());
    }
    
    
}
