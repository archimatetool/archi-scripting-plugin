/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.views.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.ColorFactory;
import com.archimatetool.editor.ui.services.ViewManager;


/**
 * Redirect standard out to Console
 */
public class ConsoleOutput {
    
    private static PrintStream fOldOut, fOldErr;
    private static ConsoleView fConsole;
    
    /**
     * Start the console re-direction
     */
    public static void start() {
        fConsole = findConsoleViewer();
        
        // Redirect to new streams
        if(fConsole != null) {
            addStreams();
        }
    }
    
    /**
     * End the console re-direction
     */
    public static void end() {
        // Restore streams
        restoreStreams();
    }
    
    /**
     * Add new Streams
     */
    private static void addStreams() {
        // Save old streams
        fOldOut = System.out;
        fOldErr = System.err;
        
        try {
            PrintStream out = new PrintStream(new DumpStream(ColorFactory.get(0, 0, 255)), true);
            System.setOut(out);
            
            PrintStream err = new PrintStream(new DumpStream(ColorFactory.get(255, 0, 0)), true);
            System.setErr(err);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Restore the error streams
     */
    private static void restoreStreams() {
        if(fOldOut != null) {
            System.setOut(fOldOut);
        }
        if(fOldErr != null) {
            System.setErr(fOldErr);
        }
    }
    
    /**
     * @return The Console Viewer if it is active, else null
     */
    private static ConsoleView findConsoleViewer() {
        if(PlatformUI.isWorkbenchRunning()) {
            return (ConsoleView)ViewManager.findViewPart(ConsoleView.ID);
        }
        
        return null;
    }

    /**
     * An OutputStream that redirects all System output to the Console
     */
    private static class DumpStream extends OutputStream {
        StringBuffer buf;
        Color color;
        
        public DumpStream(Color color) {
            this.color = color;
        }
        
        @Override
        public void write(int b) {
            if(buf == null) {
                buf = new StringBuffer();
            }
            buf.append((char)(b & 255));
        }
        
        @Override
        public void flush() throws IOException {
            if(buf != null && fConsole != null) {
                Color oldColor = fConsole.getTextColor();
                fConsole.setTextColor(color);
                fConsole.append(buf.toString());
                fConsole.setTextColor(oldColor);
            }
            buf = null;
        }
    }

}
