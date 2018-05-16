/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.ColorFactory;
import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.script.views.console.ConsoleView;


/**
 * Represents the Script "console" dom object
 */
public class Console {
    
    // Cache the current color here in case the viewer is not yet instantiated
    private Color currentColor;
    
    public Console() {
    }
    
    /**
     * Ensure that the Console is visible
     */
    public void show() {
        if(PlatformUI.isWorkbenchRunning()) {
            ViewManager.showViewPart(ConsoleView.ID, true);
        }
    }
    
    /**
     * Hide the Console
     */
    public void hide() {
        if(PlatformUI.isWorkbenchRunning()) {
            ViewManager.hideViewPart(ConsoleView.ID);
        }
    }
    
    public void setText(String text) {
        ConsoleView viewer = findConsoleViewer();
        if(viewer != null) {
            viewer.setTextColor(currentColor);
            viewer.setText(text);
        }
        else {
            System.out.println(text);
        }
    }
    
    public void println(String text) {
        print(text + "\n"); //$NON-NLS-1$
    }
    
    public void log(String text) {
        println(text);
    }

    public void print(String text) {
        ConsoleView viewer = findConsoleViewer();
        if(viewer != null) {
            viewer.setTextColor(currentColor);
            viewer.append(text);
        }
        else {
            System.out.print(text);
        }
    }
    
    public void error(Object error) {
        show();
        
        Color oldColor = currentColor;
        currentColor = ColorFactory.get(255, 0, 0);
        
        println(error.toString());
        
        currentColor = oldColor;
    }
    
    public void clear() {
        setText(""); //$NON-NLS-1$
    }

    public void setTextColor(int red, int green, int blue) {
        currentColor = ColorFactory.get(red, green, blue);
    }
    
    public void setDefaultTextColor() {
        currentColor = null;
    }
    
    private ConsoleView findConsoleViewer() {
        if(PlatformUI.isWorkbenchRunning()) {
            return (ConsoleView)ViewManager.findViewPart(ConsoleView.ID);
        }
        
        return null;
    }
}
