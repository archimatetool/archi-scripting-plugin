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
        Map<?, ?> bounds = (Map<?, ?>)getTestProxy().attr(IModelConstants.BOUNDS);
        assertEquals(0, bounds.get("x"));
        assertEquals(0, bounds.get("y"));
        assertEquals(100, bounds.get("width"));
        assertEquals(100, bounds.get("height"));
    }
    
    @Test
    public void index() {
        assertEquals(0, getTestProxy().getIndex());
        assertEquals(0, getTestProxy().attr(IModelConstants.INDEX));
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
        assertEquals(255, getTestProxy().attr(IModelConstants.OPACITY));
        getTestProxy().attr(IModelConstants.OPACITY, 40);
        assertEquals(40, getTestProxy().attr(IModelConstants.OPACITY));
    }

    @Test
    public void attr_OutlineOpacity() {
        assertEquals(255, getTestProxy().attr(IModelConstants.OUTLINE_OPACITY));
        getTestProxy().attr(IModelConstants.OUTLINE_OPACITY, 40);
        assertEquals(40, getTestProxy().attr(IModelConstants.OUTLINE_OPACITY));
    }
    
    @Test
    public void attr_Gradient() {
        assertEquals(-1, getTestProxy().attr(IModelConstants.GRADIENT));
        getTestProxy().attr(IModelConstants.GRADIENT, 3);
        assertEquals(3, getTestProxy().attr(IModelConstants.GRADIENT));
    }

    @Test
    public void attr_TextAlignment() {
        assertEquals(2, getTestProxy().attr(IModelConstants.TEXT_ALIGNMENT));
        getTestProxy().attr(IModelConstants.TEXT_ALIGNMENT, 4);
        assertEquals(4, getTestProxy().attr(IModelConstants.TEXT_ALIGNMENT));
    }
    
    @Test
    public void attr_TextPosition() {
        assertEquals(0, getTestProxy().attr(IModelConstants.TEXT_POSITION));
        getTestProxy().attr(IModelConstants.TEXT_POSITION, 2);
        assertEquals(2, getTestProxy().attr(IModelConstants.TEXT_POSITION));
    }

    @Test
    public void attr_ImagePosition() {
        assertEquals(2, getTestProxy().attr(IModelConstants.IMAGE_POSITION));
        getTestProxy().attr(IModelConstants.IMAGE_POSITION, 1);
        assertEquals(1, getTestProxy().attr(IModelConstants.IMAGE_POSITION));
    }

    @Test
    public void attr_Image() {
        assertEquals(null, getTestProxy().attr(IModelConstants.IMAGE));
        
        // Should throw an exception if image path references non existing image
        assertThrows(ArchiScriptException.class, () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", "imgpath");
            getTestProxy().attr(IModelConstants.IMAGE, map);
        });
    }
    
    @Test
    public void attr_DeriveLineColor() {
        assertEquals(true, getTestProxy().attr(IModelConstants.DERIVE_LINE_COLOR));
        getTestProxy().attr(IModelConstants.DERIVE_LINE_COLOR, false);
        assertEquals(false, getTestProxy().attr(IModelConstants.DERIVE_LINE_COLOR));
    }

}