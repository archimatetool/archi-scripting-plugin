package com.archimatetool.script;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.script.preferences.IPreferenceConstants;

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
        if(Display.getCurrent() != null) {
            if(Display.getCurrent().getSystemMenu() != null) { // Mac
                Display.getCurrent().getSystemMenu().setEnabled(enabled);
            }
            
            for(Shell shell : Display.getCurrent().getShells()) {
                shell.setEnabled(enabled);
                if(shell.getMenuBar() != null) { // Mac/Linux will have menu bar enabled
                    shell.getMenuBar().setEnabled(enabled);
                }
            }
        }
    }

    private static boolean shouldRun() {
        return PlatformUI.isWorkbenchRunning() &&
                ArchiScriptPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_REFRESH_UI_WHEN_RUNNING_SCRIPT);
    }
}
