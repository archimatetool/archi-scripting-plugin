/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;


/**
 * ArchimateModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
public abstract class ArchimateConceptProxyTests extends EObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ArchimateConceptProxyTests.class);
    }
    
    @Override
    @Test
    public void isConcept() {
        assertTrue(testProxy.isConcept());
    }

}