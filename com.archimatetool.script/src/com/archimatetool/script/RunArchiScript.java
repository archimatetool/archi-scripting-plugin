/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.dom.IArchiScriptDOMFactory;
import com.archimatetool.script.views.console.ConsoleOutput;


/**
 * Script Runner
 */
public class RunArchiScript {
	private File file;

	public RunArchiScript(File file) {
		this.file = file;
	}
	
	public void run() {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript"); //$NON-NLS-1$
        
        defineGlobalVariables(engine);
        defineExtensionGlobalVariables(engine);
        setBindings(engine);
        
        try {
            // Start the console
            ConsoleOutput.start();
            
            // Initialize jArchi using the provided init.js script
            URL initURL = ArchiScriptPlugin.INSTANCE.getBundle().getEntry("js/init.js"); //$NON-NLS-1$
            try(InputStreamReader initReader = new InputStreamReader(initURL.openStream());) {
                engine.eval(initReader);
            }
        	
            // Initialise CommandHandler
            CommandHandler.init();
            
            if(ScriptFiles.isLinkedFile(file)) {
                file = ScriptFiles.resolveLinkFile(file);
            }
            
            // Normalize filename so that nashorn's load() can run it
            String scriptPath = PlatformUtils.isWindows() ? file.getAbsolutePath().replace('\\', '/') : file.getAbsolutePath();
            
            // Initialise RefreshUIHandler
            RefreshUIHandler.init();
            
            // Evaluate the script
            engine.eval("load('" + scriptPath + "')");  //$NON-NLS-1$//$NON-NLS-2$
        }
        catch(ScriptException | IOException ex) {
            error(ex, ex.toString());
        }
        finally {
            ConsoleOutput.end();
            
            RefreshUIHandler.finalise();
            
            // Add Commands to UI
            CommandHandler.finalise(FileUtils.getFileNameWithoutExtension(file));
        }
	}

    /**
     * Global Variables
     */
    private void defineGlobalVariables(ScriptEngine engine) {
        // Eclipse ones - these are needed for calling UI methods such as opening dialogs, windows, etc
        if(PlatformUI.isWorkbenchRunning()) {
            engine.put("workbench", PlatformUI.getWorkbench()); //$NON-NLS-1$
            engine.put("workbenchwindow", PlatformUI.getWorkbench().getActiveWorkbenchWindow()); //$NON-NLS-1$
            engine.put("shell", PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()); //$NON-NLS-1$
        }
        
    }
    
    /**
     * Set/Remove some JS global bindings
     */
    private void setBindings(ScriptEngine engine) {
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        
        // Remove these
        bindings.remove("exit"); //$NON-NLS-1$
        bindings.remove("quit"); //$NON-NLS-1$
    }

    /**
     * Declared DOM extensions are registered
     */
    private void defineExtensionGlobalVariables(ScriptEngine engine) {
        IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(IArchiScriptDOMFactory.EXTENSION_ID);
        
        if(point != null) {
            for(IExtension extension : point.getExtensions()) {
                for(IConfigurationElement element : extension.getConfigurationElements()) {
                    try { 
                        String variableName = element.getAttribute("variableName"); //$NON-NLS-1$
                        Object domObject = element.createExecutableExtension("class"); //$NON-NLS-1$
                        
                        // If the class object implements IArchiScriptDOMFactory then call its getDOMroot() method as a proxy.
                        // Useful if the factory needs to instantiate the dom class object in a non-simple way.
                        if(domObject instanceof IArchiScriptDOMFactory) {
                            domObject = ((IArchiScriptDOMFactory)domObject).getDOMroot();
                        }
                        
                        if(variableName != null && domObject != null) {
                            engine.put(variableName, domObject);
                        }
                    }
                    catch(CoreException ex) {
                        ex.printStackTrace();
                    } 
                }
            }
        }
    }

	private void error(Exception ex, String string) {
	    if(ex instanceof ScriptException) {
	        ScriptException sex = (ScriptException)ex;
	        
	        if(sex.getMessage().contains("__EXIT__")) { //$NON-NLS-1$
	            System.out.println("Exited at line number " + sex.getLineNumber()); //$NON-NLS-1$
	            return;
	        }
	    }
	    
        System.err.println("Script Error at: " + ex.getClass().getName() + ", " +  //$NON-NLS-1$//$NON-NLS-2$
                string);
	}
}
