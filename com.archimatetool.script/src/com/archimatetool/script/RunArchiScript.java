/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.dom.IArchiScriptDOMFactory;
import com.archimatetool.script.dom.model.ModelHandler;
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
        
        try {
            // Start the console
            ConsoleOutput.start();
            
            // Initialize jArchi using the provided init.js script
            URL initURL = ArchiScriptPlugin.INSTANCE.getBundle().getEntry("js/init.js"); //$NON-NLS-1$
            try(InputStreamReader initReader = new InputStreamReader(initURL.openStream());) {
                engine.eval(initReader);
            }
        	
            // Initialise ModelHandler
            ModelHandler.init();
            
            // Initialise CommandHandler
            CommandHandler.init();

            // Evaluate the script
            try(FileReader reader = new FileReader(file)) {
                engine.eval(reader);
            }
            
            // If there is a "main" function invoke that
            if("function".equals(engine.eval("typeof main"))) { //$NON-NLS-1$ //$NON-NLS-2$
                ((Invocable)engine).invokeFunction("main"); //$NON-NLS-1$
            }
        }
        catch(ScriptException | IOException | NoSuchMethodException ex) {
            error(ex, ex.toString());
        }
        finally {
            ConsoleOutput.end();
            
            // Re-open models if any were closed
            ModelHandler.openModels();
            
            // Add Commands to UI
            CommandHandler.finalise();
        }
	}

    /**
     * Global Variables
     */
    private void defineGlobalVariables(ScriptEngine engine) {
        // Bind our global variables
        engine.put("__JARCHI_FILE__", file.getAbsolutePath()); //$NON-NLS-1$
        engine.put("__JARCHI_DIR__", file.getParent());  //$NON-NLS-1$
        
        // Eclipse ones - these are needed for calling UI methods such as opening dialogs, windows, etc
        if(PlatformUI.isWorkbenchRunning()) {
            engine.put("window", PlatformUI.getWorkbench().getActiveWorkbenchWindow()); //$NON-NLS-1$
            engine.put("workbench", PlatformUI.getWorkbench()); //$NON-NLS-1$
        }
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

	private void error(Exception x, String string) {
        System.err.println("Script Error at: " + x.getClass().getName() + ", " +  //$NON-NLS-1$//$NON-NLS-2$
                string);
	}

}
