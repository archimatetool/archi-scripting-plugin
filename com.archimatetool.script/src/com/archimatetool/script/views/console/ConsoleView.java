/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;

import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.IArchiScriptImages;



/**
 * Script Console View
 */
public class ConsoleView
extends ViewPart {
    public static String ID = ArchiScriptPlugin.PLUGIN_ID + ".consoleView"; //$NON-NLS-1$
    public static String HELP_ID = ArchiScriptPlugin.PLUGIN_ID + ".consoleViewHelp"; //$NON-NLS-1$

    private IAction fActionClear;
    
    private StyledText fTextPane;
    private Color fTextColor;
    
    @Override
    public void createPartControl(Composite parent) {
        fTextPane = new StyledText(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        fTextPane.setEditable(false);
        fTextPane.setTabs(40);
        
        fActionClear = new Action() {
            {
                setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_CLEAR_CONSOLE));
                setText(Messages.ConsoleView_0);
                setToolTipText(Messages.ConsoleView_0);
            }
            
            @Override
            public void run() {
                fTextPane.setText(""); //$NON-NLS-1$
            }
        };
        
        makeLocalToolBarActions();
    }
    
    public void setTextColor(Color color) {
        fTextColor = color;
    }
    
    public Color getTextColor() {
        return fTextColor;
    }
    
    /**
     * Make Local Toolbar items
     */
    protected void makeLocalToolBarActions() {
        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();

        manager.add(fActionClear);
    }

    @Override
    public void setFocus() {
        if(fTextPane != null) {
            fTextPane.setFocus();
        }
    }

    public void append(String string) {
        if(!fTextPane.isDisposed()) {
            StyleRange sr = createStyleRange(string);
            fTextPane.append(string);
            fTextPane.setStyleRange(sr);
        }
    }
    
    public void setText(String text) {
        if(!fTextPane.isDisposed()) {
            fTextPane.setText(text);
        }
    }
    
    private StyleRange createStyleRange(String string) {
        StyleRange sr = new StyleRange();
        sr.foreground = fTextColor;
        sr.start = fTextPane.getCharCount();
        sr.length = string.length();
        return sr;
    }
}
