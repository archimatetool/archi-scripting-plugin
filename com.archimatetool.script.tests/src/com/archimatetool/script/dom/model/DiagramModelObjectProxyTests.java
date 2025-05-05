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

import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.script.ArchiScriptException;


/**
 * DiagramModelObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public abstract class DiagramModelObjectProxyTests extends DiagramModelComponentProxyTests {
    
    @Override
    protected abstract IDiagramModelObject getTestEObject();
    
    @Override
    protected abstract DiagramModelObjectProxy getTestProxy();
    
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(getTestEObject());
        assertTrue(proxy instanceof DiagramModelObjectProxy);
    }

    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        EObjectProxyCollection collection = getTestProxy().find("garbage");
        assertEquals(0, collection.size());

        collection = getTestProxy().find("*");
        assertEquals(0, collection.size());
    }

    @Test
    public void attr_Bounds() {
        Map<?, ?> bounds = (Map<?, ?>)getTestProxy().attr(BOUNDS);
        assertEquals(0, bounds.get(BOUNDS_X));
        assertEquals(0, bounds.get(BOUNDS_Y));
        assertEquals(100, bounds.get(BOUNDS_WIDTH));
        assertEquals(100, bounds.get(BOUNDS_HEIGHT));
    }
    
    @Test
    public void index() {
        assertEquals(0, getTestProxy().getIndex());
        assertEquals(0, getTestProxy().attr(INDEX));
    }

    @Override
    @Test
    public void getReferencedEObject() {
        assertSame(getTestProxy().getConcept().getEObject(), getTestProxy().getReferencedEObject());
    }
    
    @Test
    public void outRels() {
        EObjectProxyCollection collection = getTestProxy().outRels();
        assertEquals(0, collection.size());
    }

    @Test
    public void inRels() {
        EObjectProxyCollection collection = getTestProxy().inRels();
        assertEquals(0, collection.size());
    }
    
    @Test
    public void attr_Opacity() {
        assertEquals(255, getTestProxy().attr(OPACITY));
        getTestProxy().attr(OPACITY, 40);
        assertEquals(40, getTestProxy().attr(OPACITY));
    }

    @Test
    public void attr_OutlineOpacity() {
        assertEquals(255, getTestProxy().attr(OUTLINE_OPACITY));
        getTestProxy().attr(OUTLINE_OPACITY, 40);
        assertEquals(40, getTestProxy().attr(OUTLINE_OPACITY));
    }
    
    @Test
    public void attr_Gradient() {
        assertEquals(-1, getTestProxy().attr(GRADIENT));
        getTestProxy().attr(GRADIENT, 3);
        assertEquals(3, getTestProxy().attr(GRADIENT));
    }

    @Test
    public void attr_TextAlignment() {
        assertEquals(2, getTestProxy().attr(TEXT_ALIGNMENT));
        getTestProxy().attr(TEXT_ALIGNMENT, 4);
        assertEquals(4, getTestProxy().attr(TEXT_ALIGNMENT));
    }
    
    @Test
    public void attr_TextPosition() {
        assertEquals(0, getTestProxy().attr(TEXT_POSITION));
        getTestProxy().attr(TEXT_POSITION, 2);
        assertEquals(2, getTestProxy().attr(TEXT_POSITION));
    }

    @Test
    public void attr_ImagePosition() {
        assertEquals(2, getTestProxy().attr(IMAGE_POSITION));
        getTestProxy().attr(IMAGE_POSITION, 1);
        assertEquals(1, getTestProxy().attr(IMAGE_POSITION));
    }

    @Test
    public void attr_Image() {
        assertEquals(null, getTestProxy().attr(IMAGE));
        
        // Should throw an exception if image path references non existing image
        assertThrows(ArchiScriptException.class, () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", "imgpath");
            getTestProxy().attr(IMAGE, map);
        });
    }
    
    @Test
    public void attr_DeriveLineColor() {
        assertEquals(true, getTestProxy().attr(DERIVE_LINE_COLOR));
        getTestProxy().attr(DERIVE_LINE_COLOR, false);
        assertEquals(false, getTestProxy().attr(DERIVE_LINE_COLOR));
    }

    @Test
    public void attr_LineStyle() {
        assertEquals(IDiagramModelObject.LINE_STYLE_SOLID, getTestProxy().attr(LINE_STYLE));
        getTestProxy().attr(LINE_STYLE, IDiagramModelObject.LINE_STYLE_DOTTED);
        assertEquals(IDiagramModelObject.LINE_STYLE_DOTTED, getTestProxy().attr(LINE_STYLE));
    }
}