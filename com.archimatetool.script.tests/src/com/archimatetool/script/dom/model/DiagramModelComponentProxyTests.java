/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.archimatetool.model.IDiagramModelArchimateComponent;

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelComponentProxy Tests
 * 
 * @author Phillip Beauvoir
 */
public abstract class DiagramModelComponentProxyTests extends EObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DiagramModelComponentProxyTests.class);
    }
    
    @Override
    @Test
    public void getReferencedConcept_IsExpectedObject() {
        assertSame(((IDiagramModelArchimateComponent)testProxy.getEObject()).getArchimateConcept(), testProxy.getReferencedConcept());
    }
    
}