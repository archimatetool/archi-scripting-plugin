/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public abstract class DiagramModelObjectProxyTests extends DiagramModelComponentProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DiagramModelObjectProxyTests.class);
    }
    
    protected DiagramModelObjectProxy actualTestProxy;
    protected ArchimateModelProxy testModelProxy;
    
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof DiagramModelObjectProxy);
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
    public void getReferencedConcept() {
        assertSame(actualTestProxy.getConcept().getEObject(), actualTestProxy.getReferencedConcept());
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
    public void attr_Opacity() {
        assertEquals(255, actualTestProxy.attr(IModelConstants.OPACITY));
        actualTestProxy.attr(IModelConstants.OPACITY, 40);
        assertEquals(40, actualTestProxy.attr(IModelConstants.OPACITY));
    }

    @Test
    public void attr_OutlineOpacity() {
        assertEquals(255, actualTestProxy.attr(IModelConstants.OUTLINE_OPACITY));
        actualTestProxy.attr(IModelConstants.OUTLINE_OPACITY, 40);
        assertEquals(40, actualTestProxy.attr(IModelConstants.OUTLINE_OPACITY));
    }
}