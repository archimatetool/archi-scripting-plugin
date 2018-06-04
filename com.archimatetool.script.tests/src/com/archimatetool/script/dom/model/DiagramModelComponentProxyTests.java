/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
    
    @Test
    public void getDiagramModel() {
        DiagramModelProxy dmProxy = ((DiagramModelComponentProxy)testProxy).getDiagramModel();
        assertNotNull(dmProxy);
        assertNotNull(dmProxy.getEObject());
    }
    
    @Override
    @Test
    public void attr_Get() {
        super.attr_Get();
        
        assertEquals(((DiagramModelComponentProxy)testProxy).getDiagramModel(), testProxy.attr(IModelConstants.DIAGRAM_MODEL));
        assertEquals(((DiagramModelComponentProxy)testProxy).getArchimateConcept(), testProxy.attr(IModelConstants.ARCHIMATE_CONCEPT));
    }
}