/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.archimatetool.script.ArchiScriptException;


/**
 * DiagramModelObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public abstract class DiagramModelObjectProxyTests extends DiagramModelComponentProxyTests {
    
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

    @Test
    public void attr_Bounds() {
        Map<?, ?> bounds = (Map<?, ?>)testProxy.attr(IModelConstants.BOUNDS);
        assertEquals(0, bounds.get("x"));
        assertEquals(0, bounds.get("y"));
        assertEquals(100, bounds.get("width"));
        assertEquals(100, bounds.get("height"));
    }

    @Override
    @Test
    public void getReferencedEObject() {
        assertSame(actualTestProxy.getConcept().getEObject(), actualTestProxy.getReferencedEObject());
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
    
    @Test
    public void attr_Gradient() {
        assertEquals(-1, actualTestProxy.attr(IModelConstants.GRADIENT));
        actualTestProxy.attr(IModelConstants.GRADIENT, 3);
        assertEquals(3, actualTestProxy.attr(IModelConstants.GRADIENT));
    }

    @Test
    public void attr_TextAlignment() {
        assertEquals(2, actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT));
        actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT, 4);
        assertEquals(4, actualTestProxy.attr(IModelConstants.TEXT_ALIGNMENT));
    }
    
    @Test
    public void attr_TextPosition() {
        assertEquals(0, actualTestProxy.attr(IModelConstants.TEXT_POSITION));
        actualTestProxy.attr(IModelConstants.TEXT_POSITION, 2);
        assertEquals(2, actualTestProxy.attr(IModelConstants.TEXT_POSITION));
    }

    @Test
    public void attr_ImagePosition() {
        assertEquals(2, actualTestProxy.attr(IModelConstants.IMAGE_POSITION));
        actualTestProxy.attr(IModelConstants.IMAGE_POSITION, 1);
        assertEquals(1, actualTestProxy.attr(IModelConstants.IMAGE_POSITION));
    }

    @Test
    public void attr_Image() {
        assertEquals(null, actualTestProxy.attr(IModelConstants.IMAGE));
        
        // Should throw an exception if image path references non existing image
        assertThrows(ArchiScriptException.class, () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", "imgpath");
            actualTestProxy.attr(IModelConstants.IMAGE, map);
        });
    }
    
    @Test
    public void attr_DeriveLineColor() {
        assertEquals(true, actualTestProxy.attr(IModelConstants.DERIVE_LINE_COLOR));
        testProxy.attr(IModelConstants.DERIVE_LINE_COLOR, false);
        assertEquals(false, testProxy.attr(IModelConstants.DERIVE_LINE_COLOR));
    }

}