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

import com.archimatetool.editor.ui.services.ViewManager;


/**
 * Redirect standard out to Console
 */
public class ConsoleOutput {
    
    private static PrintStream oldOut;
    private static PrintStream oldErr;
    
    private static PrintStream newOut;
    private static PrintStream newErr;
    
    /**
     * Start the console re-direction
     */
    public static void start() {
        // Init
        if(PlatformUI.isWorkbenchRunning()) {
            if(oldOut == null) {
                oldOut = System.out;
                oldErr = System.err;
                
                newOut = new PrintStream(new DumpStream(new Color(0, 0, 255)), true);
                newErr = new PrintStream(new DumpStream(new Color(255, 0, 0)), true);
            }
        }

        // If console is showing redirect
        if(getConsoleViewer() != null) {
            System.setOut(newOut);
            System.setErr(newErr);
        }
    }
    
    /**
     * End the console re-direction
     */
    public static void end() {
        // Restore streams
        if(oldOut != null) {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
    }
    
    /**
     * @return The Console Viewer if it is active, else null
     */
    private static ConsoleView getConsoleViewer() {
        return PlatformUI.isWorkbenchRunning() ? (ConsoleView)ViewManager.findViewPart(ConsoleView.ID) : null;
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
            ConsoleView console = getConsoleViewer();
            
            if(buf != null && console != null) {
                Color oldColor = console.getTextColor();
                console.setTextColor(color);
                console.append(buf.toString());
                console.setTextColor(oldColor);
            }
            
            buf = null;
        }
    }
}
