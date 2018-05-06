package com.archimatetool.script.views.file;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

import com.archimatetool.editor.ui.UIUtils;
import com.archimatetool.editor.ui.components.CellEditorGlobalActionHandler;

/**
 * Tree Text CellEditor
 * 
 * TODO!!!!!!!! When Archi 4.3 is released delete this one and use the one in com.archimatetool.editor.ui.components
 * 
 * @author Phillip Beauvoir
 */
public class TreeTextCellEditor extends TextCellEditor {
    int minHeight = 0;
    CellEditorGlobalActionHandler fGlobalActionHandler;

    public TreeTextCellEditor(Tree tree) {
        super(tree, SWT.BORDER);
        Text txt = (Text)getControl();
        
        // Filter out nasties
        UIUtils.applyInvalidCharacterFilter(txt);
        
        // Not sure if we need this
        //UIUtils.conformSingleTextControl(txt);

        FontData[] fontData = txt.getFont().getFontData();
        if(fontData != null && fontData.length > 0) {
            minHeight = fontData[0].getHeight() + 10;
        }
    }

    @Override
    public LayoutData getLayoutData() {
        LayoutData data = super.getLayoutData();
        if(minHeight > 0) {
            data.minimumHeight = minHeight;
        }
        return data;
    }
    
    @Override
    public void activate() {
        // Clear global key binds
        fGlobalActionHandler = new CellEditorGlobalActionHandler();
        fGlobalActionHandler.clearGlobalActions();
    }
    
    @Override
    public void deactivate() {
        super.deactivate();
        
        // Restore global key binds
        if(fGlobalActionHandler != null) {
            fGlobalActionHandler.restoreGlobalActions();
            fGlobalActionHandler = null;
        }
    }
}
