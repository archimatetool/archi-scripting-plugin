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
import com.archimatetool.script.views.console.ConsoleOutput;
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
            ConsoleOutput.start(); // Ensure Console is re-directing output
        }
    }
    
    /**
     * Hide the Console
     */
    public void hide() {
        if(PlatformUI.isWorkbenchRunning()) {
            ViewManager.hideViewPart(ConsoleView.ID);
            ConsoleOutput.end(); // Ensure Console is re-directing output
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
    
    public void log(Object obj) {
        toConsole(toString(obj) + "\n");
    }
    
    public void log(Object... objs) {
        // Null
        if(objs == null) {
            println(null); 
            return;
        }
        
        StringJoiner joiner = new StringJoiner(" ");
        
        for(Object o : objs) {
            joiner.add(toString(o));
        }
        
        toConsole(joiner.toString() + "\n");
    }

    public void print(Object obj) {
        toConsole(toString(obj));
    }
    
    public void println(Object obj) {
        toConsole(toString(obj) + "\n");
    }
    
    private void toConsole(String output) {
        ConsoleView viewer = findConsoleViewer();
        
        if(viewer != null) {
            viewer.setTextColor(currentColor);
            viewer.append(output);
        }
        else {
            System.out.print(output);
        }
    }
    
    private String toString(Object obj) {
        // Null
        if(obj == null) {
            return "(null)";
        }
        
        // Map
        if(obj instanceof Map<?, ?>) {
            StringJoiner joiner = new StringJoiner(", ");
            for(Entry<?, ?> e : ((Map<?, ?>)obj).entrySet()) {
                joiner.add(e.getKey() + ": " + e.getValue());
            }
            return "{" + joiner.toString() + "}";
        }
        
        // Object
        return obj.toString();
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
