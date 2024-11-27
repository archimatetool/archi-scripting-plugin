package com.archimatetool.script;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * RefreshUIHandler
 * 
 * @author Phillip Beauvoir
 */
public class RefreshUIHandler {
    
    // Refresh UI at set interval
    private static final int refreshInterval = 100;

    private static long time = 0L;

    public static void init() {
        if(!shouldRun()) {
            return;
        }
        
        // Disable UI
        setShellEnabled(false);

        // Current time
        time = System.currentTimeMillis();
    }
    
    public static void refresh() {
        if(!shouldRun()) {
            return;
        }
        
        // Not enough refresh interval time has passed
        if(System.currentTimeMillis() - time < refreshInterval) {
            return;
        }
        
        // Update UI thread
        try {
            if(Display.getCurrent() != null) {
                while(Display.getCurrent().readAndDispatch());
            }
        }
        catch(Exception ex) {
            setShellEnabled(true);
            ex.printStackTrace();
        }
        finally {
            time = System.currentTimeMillis();
        }
    }
    
    public static void finalise() {
        if(shouldRun()) {
            setShellEnabled(true);
        }
    }
    
    /**
     * Disable/Enable Application Shell and Menu Bar so user doesn't edit models
     */
    private static void setShellEnabled(boolean enabled) {
        if(Display.getCurrent() == null) {
            return;
        }
        
        if(Display.getCurrent().getSystemMenu() != null) { // Mac
            Display.getCurrent().getSystemMenu().setEnabled(enabled);
        }

        for(Shell shell : Display.getCurrent().getShells()) {
            shell.setEnabled(enabled);
            if(shell.getMenuBar() != null) { // Mac/Linux will have menu bar enabled
                shell.getMenuBar().setEnabled(enabled);
            }
        }
        
        // Set focus back to the active Workbench part.
        // This is a problem on Mac rather than Windows (don't know about Linux)
        if(enabled) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().setFocus();
        }
    }

    private static boolean shouldRun() {
        return PlatformUI.isWorkbenchRunning() && RefreshUICommandHandler.getState();
    }
}
