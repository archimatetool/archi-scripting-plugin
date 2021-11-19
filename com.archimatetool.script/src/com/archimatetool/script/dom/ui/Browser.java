/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.ui;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.browser.BrowserEditorInput;
import com.archimatetool.editor.browser.IBrowserEditor;
import com.archimatetool.editor.browser.IBrowserEditorInput;
import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.editor.utils.StringUtils;


/**
 * Represents the "Browser" object
 */
public class Browser {
    
    private IBrowserEditor fBrowserEditor;
    
    public Browser() {
    }
    
    // Return new instance of Browser
    public Browser open() {
        return open(null, "Archi");  //$NON-NLS-1$
    }
    
    // Return new instance of Browser
    public Browser open(String url) {
        return open(url, url);
    }
    
    // Return new instance of Browser
    public Browser open(String url, String title) {
        Browser browser = new Browser();
        browser.setBrowserEditor(url, title); 
        return browser;
    }

    public void setText(String html) {
        if(fBrowserEditor != null) {
            fBrowserEditor.getBrowser().setText(html, true);
        }
    }
    
    public void setURL(String url) {
        setBrowserEditor(url, url);
    }
    
    public void setTitle(String title) {
        setBrowserEditor(null, title);
    }
    
    public void close() {
        if(fBrowserEditor != null) {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            page.closeEditor(fBrowserEditor, false);
        }
    }

    private void setBrowserEditor(String url, String title) {
        if(!PlatformUI.isWorkbenchRunning()) {
            return;
        }
        
        // TODO: Get these from a preference or JS argument
        boolean jsEnabled = true;
        boolean externalHostsEnabled = true;
        
        IBrowserEditorInput input = new BrowserEditorInput(url, title != null ? title : StringUtils.safeString(url));
        input.setJavascriptEnabled(jsEnabled);
        input.setExternalHostsEnabled(externalHostsEnabled);

        if(fBrowserEditor == null) {
            fBrowserEditor = (IBrowserEditor)EditorManager.openEditor(input, IBrowserEditor.ID);
        }
        else {
            fBrowserEditor.setBrowserEditorInput(input);
        }
    }
}
