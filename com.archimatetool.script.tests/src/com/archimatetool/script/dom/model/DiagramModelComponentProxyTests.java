/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
    
    @Override
    protected abstract DiagramModelComponentProxy getTestProxy();
    
    @Test
    public void getView() {
        DiagramModelProxy dmProxy = getTestProxy().getView();
        assertNotNull(dmProxy);
        assertNotNull(dmProxy.getEObject());
    }
    
    @Test
    public void attr_View() {
        assertEquals(getTestProxy().getView(), getTestProxy().attr(VIEW));
    }
    
    @Test
    public void attr_ArchimateConcept() {
        assertEquals(getTestProxy().getConcept(), getTestProxy().attr(CONCEPT));
    }
    
    @Test
    public void getConcept() {
        assertNull(getTestProxy().getConcept());
    }

    @Test
    public void attr_Set_FontColorThrowsException() {
        assertThrows(ArchiScriptException.class, () -> {
            getTestProxy().attr(FONT_COLOR, "123456");
        });
    }
    
    @Test
    public void attr_FontColor() {
        assertEquals("#000000", getTestProxy().attr(FONT_COLOR));
        getTestProxy().attr(FONT_COLOR, "#121212");
        assertEquals("#121212", getTestProxy().attr(FONT_COLOR));
    }

    @Test
    public void attr_FontColorNull() {
        assertEquals("#000000", getTestProxy().attr(FONT_COLOR));
        getTestProxy().attr(FONT_COLOR, null);
        assertEquals("#000000", getTestProxy().attr(FONT_COLOR));
    }

    @Test
    public void attr_FontName() {
        assertEquals(FontFactory.getDefaultUserViewFontData().getName(), getTestProxy().attr(FONT_NAME));
        getTestProxy().attr(FONT_NAME, "Comic Sans");
        assertEquals("Comic Sans", getTestProxy().attr(FONT_NAME));
    }

    @Test
    public void attr_FontSize() {
        assertEquals(FontFactory.getDefaultUserViewFontData().getHeight(), getTestProxy().attr(FONT_SIZE));
        getTestProxy().attr(FONT_SIZE, 34);
        assertEquals(34, getTestProxy().attr(FONT_SIZE));
    }

    @Test
    public void attr_FontStyle() {
        assertEquals("normal", getTestProxy().attr(FONT_STYLE));
        getTestProxy().attr(FONT_STYLE, "bold");
        assertEquals("bold", getTestProxy().attr(FONT_STYLE));
        getTestProxy().attr(FONT_STYLE, "italic");
        assertEquals("italic", getTestProxy().attr(FONT_STYLE));
        getTestProxy().attr(FONT_STYLE, "bolditalic");
        assertEquals("bolditalic", getTestProxy().attr(FONT_STYLE));
    }

    @Test
    public void attr_LineColor() {
        assertEquals(ColorFactory.convertRGBToString(ColorFactory.getDefaultLineColor(getTestProxy().getEObject()).getRGB()),
                getTestProxy().attr(LINE_COLOR));
        getTestProxy().attr(LINE_COLOR, "#232398");
        assertEquals("#232398", getTestProxy().attr(LINE_COLOR));
    }
    
    @Test
    public void attr_LineColorNull() {
        String defaultLineColor = ColorFactory.convertRGBToString(ColorFactory.getDefaultLineColor(getTestProxy().getEObject()).getRGB());
        
        assertEquals(defaultLineColor, getTestProxy().attr(LINE_COLOR));
        getTestProxy().attr(LINE_COLOR, null);
        assertEquals(defaultLineColor, getTestProxy().attr(LINE_COLOR));
    }

    @Test
    public void attr_LineWidth() {
        assertEquals(1, getTestProxy().attr(LINE_WIDTH));
        getTestProxy().attr(LINE_WIDTH, 4);
        assertEquals(3, getTestProxy().attr(LINE_WIDTH));
    }
    

}