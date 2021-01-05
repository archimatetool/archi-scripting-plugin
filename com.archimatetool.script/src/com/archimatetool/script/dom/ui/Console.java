/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.ui;

import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.ColorFactory;
import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.script.views.console.ConsoleView;


/**
 * Represents the Script "console" dom object
 */
@SuppressWarnings("nls")
public class Console {
    
    // Cache the current color here in case the viewer is not yet instantiated
    private Color currentColor;
    
    public Console() {
    }
    
    public static boolean isVisible() {
        return findConsoleViewer() != null;
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
    
    public void println(Object obj) {
        if(obj == null) {
            print("(null)\n"); 
            return;
        }
        
        print(obj + "\n");
    }
    
    public void log(Object... objs) {
        if(objs == null) {
            print("(null)\n"); 
            return;
        }
        
        for(Object o : objs) {
            print(o);
        }
        
        print("\n"); //$NON-NLS-1$
    }

    public void print(Object obj) {
        String output = "";
        
        if(obj == null) {
            output = "(null)";
        }
        else if(obj instanceof Map<?, ?>) {
            StringJoiner joiner = new StringJoiner(", ");
            for(Entry<?, ?> e : ((Map<?, ?>)obj).entrySet()) {
                joiner.add(e.getKey() + ": " + e.getValue());
            }
            output = "{" + joiner.toString() + "}";
        }
        else {
            output = obj.toString();
        }
        
        ConsoleView viewer = findConsoleViewer();
        
        if(viewer != null) {
            viewer.setTextColor(currentColor);
            viewer.append(output);
        }
        else {
            System.out.print(output);
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
        setText("");
    }

    public void setTextColor(int red, int green, int blue) {
        currentColor = ColorFactory.get(red, green, blue);
    }
    
    public void setDefaultTextColor() {
        currentColor = null;
    }
    
    private static ConsoleView findConsoleViewer() {
        if(PlatformUI.isWorkbenchRunning()) {
            return (ConsoleView)ViewManager.findViewPart(ConsoleView.ID);
        }
        
        return null;
    }
}
