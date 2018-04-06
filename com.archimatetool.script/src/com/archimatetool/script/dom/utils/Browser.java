/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.utils;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.browser.BrowserEditorInput;
import com.archimatetool.editor.browser.IBrowserEditor;
import com.archimatetool.editor.ui.services.EditorManager;


/**
 * Represents the "Browser" object
 */
public class Browser {
    
    private IBrowserEditor fBrowserEditor;
    
    private boolean instanced;
    
    public Browser() {
    }
    
    public Browser newInstance() {
        Browser browser = new Browser();
        browser.instanced = true;
        return browser;
    }
    
    public void open() {
        if(checkInstanced()) {
            open("http://localhost", "Archi");  //$NON-NLS-1$//$NON-NLS-2$
        }
    }
    
    public void open(String url, String title) {
        if(checkInstanced()) {
            createBrowserEditor(url, title);
        }
    }
    
    public void setText(String html) {
        if(checkInstanced() && fBrowserEditor != null) {
            fBrowserEditor.getBrowser().setText(html, true);
        }
    }
    
    public void close() {
        if(checkInstanced() && fBrowserEditor != null) {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            page.closeEditor(fBrowserEditor, false);
        }
    }

    private boolean checkInstanced()  {
        if(!instanced) {
            System.err.println(Messages.Browser_0);
        }
        
        return instanced;
    }
    
    private void createBrowserEditor(String url, String title) {
        BrowserEditorInput input = new BrowserEditorInput(url, title);
        
        if(fBrowserEditor == null) {
            fBrowserEditor = (IBrowserEditor)EditorManager.openEditor(input, IBrowserEditor.ID);
        }
        else {
            fBrowserEditor.setBrowserEditorInput(new BrowserEditorInput(url, title));
        }
    }
}
