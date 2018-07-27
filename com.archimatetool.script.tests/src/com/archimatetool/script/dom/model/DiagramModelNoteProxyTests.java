/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IDiagramModelNote;

import junit.framework.JUnit4TestAdapter;


/**
 * DiagramModelNoteProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class DiagramModelNoteProxyTests extends DiagramModelObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(DiagramModelNoteProxyTests.class);
    }
    
    @Override
    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(IArchimateFactory.eINSTANCE.createDiagramModelNote());
        assertTrue(proxy instanceof DiagramModelNoteProxy);
    }

    @Test
    public void attr_Text() {
        IDiagramModelNote note = IArchimateFactory.eINSTANCE.createDiagramModelNote();
        DiagramModelNoteProxy proxy = new DiagramModelNoteProxy(note);

        proxy.attr(IModelConstants.TEXT, "Hello");
        assertEquals("Hello", proxy.attr(IModelConstants.TEXT));
    }

    @Test
    public void setText() {
        IDiagramModelNote note = IArchimateFactory.eINSTANCE.createDiagramModelNote();
        DiagramModelNoteProxy proxy = new DiagramModelNoteProxy(note);

        proxy.setText("Hello");
        assertEquals("Hello", proxy.getText());
    }
}