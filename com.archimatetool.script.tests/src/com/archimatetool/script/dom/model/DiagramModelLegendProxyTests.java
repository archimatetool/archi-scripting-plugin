/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.model.ILegendOptions;


/**
 * DiagramModelLegendProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelLegendProxyTests extends DiagramModelNoteProxyTests {
    
    @Override
    @BeforeEach
    public void runOnceBeforeEachTest() {
        ArchimateModelProxy modelProxy = TestsHelper.createTestArchimateModelProxy();
        viewProxy = modelProxy.createArchimateView("test");
        testProxy = (DiagramModelLegendProxy)viewProxy.createObject(DIAGRAM_MODEL_LEGEND, 0, 0, 100, 100);
    }

    @Override
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(getTestEObject());
        assertTrue(proxy instanceof DiagramModelLegendProxy);
    }

    @Override
    @Test
    public void attr_Text() {
        testProxy.attr(TEXT, "Hello");
        assertEquals("", testProxy.attr(TEXT));
    }

    @Override
    @Test
    public void setText() {
        testProxy.setText("Hello");
        assertEquals("", testProxy.getText());
    }
    
    @Override
    @Test
    public void attr_BorderType() {
        assertEquals(0, testProxy.attr(BORDER_TYPE));
        testProxy.attr(BORDER_TYPE, 1);
        assertEquals(0, testProxy.attr(BORDER_TYPE));
    }

    @Override
    @Test
    public void getType() {
        assertEquals(DIAGRAM_MODEL_LEGEND, getTestProxy().getType());
    }
    
    @Override
    @Test
    public void attr_Type() {
        assertEquals(DIAGRAM_MODEL_LEGEND, getTestProxy().getType());
    }
    
    @Override
    @Test
    public void attr_ImagePosition() {
        assertEquals(-1, getTestProxy().attr(IMAGE_POSITION));
        getTestProxy().attr(IMAGE_POSITION, 1);
        assertEquals(-1, getTestProxy().attr(IMAGE_POSITION));
    }
    
    @Override
    @Test
    public void attr_Image() {
        assertEquals(null, getTestProxy().attr(IMAGE));
    }
    
    @Override
    @Test
    public void attr_TextPosition() {
        assertEquals(0, getTestProxy().attr(TEXT_POSITION));
        getTestProxy().attr(TEXT_POSITION, 2);
        assertEquals(0, getTestProxy().attr(TEXT_POSITION));
    }
    
    @Override
    @Test
    public void attr_TextAlignment() {
        assertEquals(1, testProxy.attr(TEXT_ALIGNMENT));
        testProxy.attr(TEXT_ALIGNMENT, 4);
        assertEquals(1, testProxy.attr(TEXT_ALIGNMENT));
    }
    
    @Test
    public void attr_Options() {
        Map<?, ?> options = (Map<?, ?>)getTestProxy().attr(LEGEND_OPTIONS);
        
        assertEquals(true, options.get(LEGEND_DISPLAY_ELEMENTS));
        assertEquals(true, options.get(LEGEND_DISPLAY_RELATIONS));
        assertEquals(true, options.get(LEGEND_DISPLAY_SPECIALIZATION_ELEMENTS));
        assertEquals(true, options.get(LEGEND_DISPLAY_SPECIALIZATION_RELATIONS));
        
        assertEquals(ILegendOptions.ROWS_PER_COLUMN_DEFAULT, options.get(LEGEND_ROWS_PER_COLUMN));
        assertEquals(0, options.get(LEGEND_WIDTH_OFFSET));
        assertEquals(ILegendOptions.COLORS_DEFAULT, options.get(LEGEND_COLOR_SCHEME));
        assertEquals(ILegendOptions.SORT_DEFAULT, options.get(LEGEND_SORT_METHOD));
    }
}