/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.archimatetool.editor.ui.ColorFactory;
import com.archimatetool.editor.ui.FontFactory;
import com.archimatetool.script.ArchiScriptException;


/**
 * DiagramModelComponentProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public abstract class DiagramModelComponentProxyTests extends EObjectProxyTests {
    
    @Test
    public void getView() {
        DiagramModelProxy dmProxy = ((DiagramModelComponentProxy)testProxy).getView();
        assertNotNull(dmProxy);
        assertNotNull(dmProxy.getEObject());
    }
    
    @Test
    public void attr_View() {
        assertEquals(((DiagramModelComponentProxy)testProxy).getView(), testProxy.attr(IModelConstants.VIEW));
    }
    
    @Test
    public void attr_ArchimateConcept() {
        assertEquals(((DiagramModelComponentProxy)testProxy).getConcept(), testProxy.attr(IModelConstants.CONCEPT));
    }
    
    @Test
    public void getConcept() {
        assertNull(((DiagramModelComponentProxy)testProxy).getConcept());
    }

    @Test(expected=ArchiScriptException.class)
    public void attr_Set_FontColorThrowsException() {
        testProxy.attr(IModelConstants.FONT_COLOR, "123456");
    }
    
    @Test
    public void attr_FontColor() {
        assertEquals("#000000", testProxy.attr(IModelConstants.FONT_COLOR));
        testProxy.attr(IModelConstants.FONT_COLOR, "#121212");
        assertEquals("#121212", testProxy.attr(IModelConstants.FONT_COLOR));
    }

    @Test
    public void attr_FontColorNull() {
        assertEquals("#000000", testProxy.attr(IModelConstants.FONT_COLOR));
        testProxy.attr(IModelConstants.FONT_COLOR, null);
        assertEquals("#000000", testProxy.attr(IModelConstants.FONT_COLOR));
    }

    @Test
    public void attr_FontName() {
        assertEquals(FontFactory.getDefaultUserViewFontData().getName(), testProxy.attr(IModelConstants.FONT_NAME));
        testProxy.attr(IModelConstants.FONT_NAME, "Comic Sans");
        assertEquals("Comic Sans", testProxy.attr(IModelConstants.FONT_NAME));
    }

    @Test
    public void attr_FontSize() {
        assertEquals(FontFactory.getDefaultUserViewFontData().getHeight(), testProxy.attr(IModelConstants.FONT_SIZE));
        testProxy.attr(IModelConstants.FONT_SIZE, 34);
        assertEquals(34, testProxy.attr(IModelConstants.FONT_SIZE));
    }

    @Test
    public void attr_FontStyle() {
        assertEquals("normal", testProxy.attr(IModelConstants.FONT_STYLE));
        testProxy.attr(IModelConstants.FONT_STYLE, "bold");
        assertEquals("bold", testProxy.attr(IModelConstants.FONT_STYLE));
        testProxy.attr(IModelConstants.FONT_STYLE, "italic");
        assertEquals("italic", testProxy.attr(IModelConstants.FONT_STYLE));
        testProxy.attr(IModelConstants.FONT_STYLE, "bolditalic");
        assertEquals("bolditalic", testProxy.attr(IModelConstants.FONT_STYLE));
    }

    @Test
    public void attr_LineColor() {
        assertEquals(ColorFactory.convertRGBToString(ColorFactory.getDefaultLineColor(testProxy.getEObject()).getRGB()),
                testProxy.attr(IModelConstants.LINE_COLOR));
        testProxy.attr(IModelConstants.LINE_COLOR, "#232398");
        assertEquals("#232398", testProxy.attr(IModelConstants.LINE_COLOR));
    }
    
    @Test
    public void attr_LineColorNull() {
        String defaultLineColor = ColorFactory.convertRGBToString(ColorFactory.getDefaultLineColor(testProxy.getEObject()).getRGB());
        
        assertEquals(defaultLineColor, testProxy.attr(IModelConstants.LINE_COLOR));
        testProxy.attr(IModelConstants.LINE_COLOR, null);
        assertEquals(defaultLineColor, testProxy.attr(IModelConstants.LINE_COLOR));
    }

    @Test
    public void attr_LineWidth() {
        assertEquals(1, testProxy.attr(IModelConstants.LINE_WIDTH));
        testProxy.attr(IModelConstants.LINE_WIDTH, 4);
        assertEquals(3, testProxy.attr(IModelConstants.LINE_WIDTH));
    }
    

}