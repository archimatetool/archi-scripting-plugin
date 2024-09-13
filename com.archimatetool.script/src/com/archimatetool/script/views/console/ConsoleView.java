/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.console;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IContributedContentsView;
import org.eclipse.ui.part.ViewPart;

import com.archimatetool.editor.ui.FontFactory;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.ArchiScriptPlugin;
import com.archimatetool.script.IArchiScriptImages;
import com.archimatetool.script.RefreshUIHandler;
import com.archimatetool.script.preferences.IPreferenceConstants;



/**
 * Script Console View
 */
public class ConsoleView
extends ViewPart implements IContributedContentsView {
    public static String ID = ArchiScriptPlugin.PLUGIN_ID + ".consoleView"; //$NON-NLS-1$
    public static String HELP_ID = ArchiScriptPlugin.PLUGIN_ID + ".consoleViewHelp"; //$NON-NLS-1$

    public static Font DEFAULT_FONT = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);
    
    private IAction fActionClear, fActionWordWrap, fActionScrollLock;
    
    private StyledText fTextPane;
    private Color fTextColor;
    
    private IPropertyChangeListener prefsListener = (event) -> {
        if(IPreferenceConstants.PREFS_CONSOLE_FONT == event.getProperty()) {
            setFontFromPreferences();
        }
    };
    
    @Override
    public void createPartControl(Composite parent) {
        fTextPane = new StyledText(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        fTextPane.setEditable(false);
        fTextPane.setTabs(4);
        fTextPane.setWordWrap(ArchiScriptPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_CONSOLE_WORD_WRAP));

        setFontFromPreferences();
        
        fActionClear = new Action(Messages.ConsoleView_0) {
            {
                setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_CLEAR_CONSOLE));
                setToolTipText(getText());
            }
            
            @Override
            public void run() {
                fTextPane.setText(""); //$NON-NLS-1$
            }
        };
        
        fActionWordWrap = new Action(Messages.ConsoleView_1, IAction.AS_CHECK_BOX) {
            {
                setChecked(ArchiScriptPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_CONSOLE_WORD_WRAP));
                setToolTipText(getText());
                setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_CONSOLE_WRAP));
            }
            
            @Override
            public void run() {
                fTextPane.setWordWrap(isChecked());
                ArchiScriptPlugin.INSTANCE.getPreferenceStore().setValue(IPreferenceConstants.PREFS_CONSOLE_WORD_WRAP, isChecked());
            }
        };
        
        fActionScrollLock = new Action(Messages.ConsoleView_2, IAction.AS_CHECK_BOX) {
            {
                setChecked(ArchiScriptPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_CONSOLE_SCROLL_LOCK));
                setToolTipText(getText());
                setImageDescriptor(IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_CONSOLE_SCROLL_LOCK));
            }
            
            @Override
            public void run() {
                ArchiScriptPlugin.INSTANCE.getPreferenceStore().setValue(IPreferenceConstants.PREFS_CONSOLE_SCROLL_LOCK, isChecked());
            }
        };
        
        makeLocalMenuActions();
        makeLocalToolBarActions();
        
        ArchiScriptPlugin.INSTANCE.getPreferenceStore().addPropertyChangeListener(prefsListener);
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
        manager.add(new Separator());
        manager.add(fActionScrollLock);
        manager.add(fActionWordWrap);
    }

    protected void makeLocalMenuActions() {
        //IActionBars actionBars = getViewSite().getActionBars();
        // Local menu items go here
        //IMenuManager manager = actionBars.getMenuManager();
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
            
            // Scroll to end
            if(!fActionScrollLock.isChecked()) {
                fTextPane.setTopIndex(fTextPane.getLineCount() - 1);
                fTextPane.setCaretOffset(fTextPane.getText().length());
            }
            
            // Update UI
            RefreshUIHandler.refresh();
        }
    }
    
    public void setText(String text) {
        if(!fTextPane.isDisposed()) {
            fTextPane.setText(text);
            
            // Scroll to end
            if(!fActionScrollLock.isChecked()) {
                fTextPane.setTopIndex(fTextPane.getLineCount() - 1);
                fTextPane.setCaretOffset(fTextPane.getText().length());
            }
            
            // Update UI
            RefreshUIHandler.refresh();
        }
    }
    
    private StyleRange createStyleRange(String string) {
        StyleRange sr = new StyleRange();
        sr.foreground = fTextColor;
        sr.start = fTextPane.getCharCount();
        sr.length = string.length();
        return sr;
    }
    
    private void setFontFromPreferences() {
        String fontName = ArchiScriptPlugin.INSTANCE.getPreferenceStore().getString(IPreferenceConstants.PREFS_CONSOLE_FONT);
        if(!StringUtils.isSet(fontName)) {
            fTextPane.setFont(DEFAULT_FONT);
        }
        else {
            fTextPane.setFont(FontFactory.get(fontName));
        }
    }
    
    /**
     * Return null so that the Properties View displays "The active part does not provide properties" instead of a table
     */
    @Override
    public IWorkbenchPart getContributingPart() {
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        ArchiScriptPlugin.INSTANCE.getPreferenceStore().removePropertyChangeListener(prefsListener);
    }
}
