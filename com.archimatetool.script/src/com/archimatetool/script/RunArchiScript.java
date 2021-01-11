/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.util.Map.Entry;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.dom.DomExtensionFactory;
import com.archimatetool.script.dom.IArchiScriptBinding;
import com.archimatetool.script.views.console.ConsoleOutput;


/**
 * Script Runner
 */
@SuppressWarnings("nls")
public class RunArchiScript {
	private File file;

	public RunArchiScript(File file) {
		this.file = file;
	}
	
	public void run() {
        // Get the provider for this file type
	    IScriptEngineProvider provider = IScriptEngineProvider.INSTANCE.getProviderForFile(file);
        
	    if(provider == null) {
	        throw new RuntimeException(NLS.bind("Script Provider not found for file: {0}", file)); //$NON-NLS-1$
	    }
	    
	    ScriptEngine engine = provider.createScriptEngine();
	    
	    if(engine == null) {
            throw new RuntimeException(NLS.bind("Script Engine not found for file: {0}", file)); //$NON-NLS-1$
        }
	    
	    // Set the script engine class name in a System Property in case we need to know what the engine is elsewhere
        System.getProperties().put("script.engine", engine.getClass().getName());
        
        defineGlobalVariables(engine);
        defineExtensionGlobalVariables(engine);
        
        // Start the console *after* the script engine has been created to avoid showing warning messages
        ConsoleOutput.start();

        // Initialise CommandHandler
        CommandHandler.init(FileUtils.getFileNameWithoutExtension(file));

        // Initialise RefreshUIHandler
        RefreshUIHandler.init();

        try {
            if(ScriptFiles.isLinkedFile(file)) {
                file = ScriptFiles.resolveLinkFile(file);
            }
            provider.run(file, engine);
        }
        catch(Throwable ex) {
            error(ex);
        }
        finally {
            // End writing to the Console
            ConsoleOutput.end();
            
            // Finalise RefreshUIHandler
            RefreshUIHandler.finalise();
            
            // Run the Commands on the CommandStack to enable Undo/Redo
            CommandHandler.finalise();
            
            // Dispose any resources that a binding object may be holding onto
            for(Object binding : engine.getBindings(ScriptContext.ENGINE_SCOPE).values()) {
                if(binding instanceof IArchiScriptBinding) {
                    ((IArchiScriptBinding)binding).dispose();
                }
            }
        }
	}
	
    /**
     * Global Variables
     */
    private void defineGlobalVariables(ScriptEngine engine) {
        // Eclipse ones - these are needed for calling UI methods such as opening dialogs, windows, etc
        if(PlatformUI.isWorkbenchRunning()) {
            engine.put("workbench", PlatformUI.getWorkbench());
            engine.put("workbenchwindow", PlatformUI.getWorkbench().getActiveWorkbenchWindow());
            engine.put("shell", PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        }

        // directory of user scripts folder
        engine.put("__SCRIPTS_DIR__", ArchiScriptPlugin.INSTANCE.getUserScriptsFolder().getAbsolutePath() + File.separator);
    }
    
    /**
     * Declared DOM extensions are registered
     */
    private void defineExtensionGlobalVariables(ScriptEngine engine) {
        for(Entry<String, Object> entry : DomExtensionFactory.getDOMExtensions().entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
    }

	private void error(Throwable ex) {
	    // The init.js function exit() works by throwing an exception with message "__EXIT__"
	    if(ex instanceof ScriptException && ex.getMessage().contains("__EXIT__")) {
	        System.out.println("Exited");
	    }
	    // Other exception
	    else {
	        System.err.println("Script Error: " + ex.toString());
	    }
	}
}
