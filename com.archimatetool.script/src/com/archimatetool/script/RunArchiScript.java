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
import org.graalvm.polyglot.PolyglotException;

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
	
	// If true exceptions are thrown rather than printed to console or stdout.
	// Used for testing.
	boolean throwExceptions;

	public RunArchiScript(File file) {
		this.file = file;
	}
	
	public void run() {
        // Get the provider for this file type
	    IScriptEngineProvider provider = IScriptEngineProvider.INSTANCE.getProviderForFile(file)
                .orElseThrow(() -> new RuntimeException(NLS.bind("Script Provider not found for file: {0}", file)));
	    
        ScriptEngine engine = provider.createScriptEngine()
                .orElseThrow(() -> new RuntimeException(NLS.bind("Script engine not found for file: {0}", file)));
	    
	    // Set the script engine class name in a System Property in case we need to know what the engine is elsewhere
        System.getProperties().put("script.engine", engine.getClass().getName());
        
        defineGlobalVariables(engine);
        defineExtensionGlobalVariables(engine);
        
        // Start the console *after* the script engine has been created to avoid showing warning messages
        ConsoleOutput.start();

        // Initialise CommandHandler
        CommandHandler.init();

        // Initialise RefreshUIHandler
        RefreshUIHandler.init();
        
        // Check for linked file
        if(ScriptFiles.isLinkedFile(file)) {
            file = ScriptFiles.resolveLinkFile(file);
        }

        try {
            provider.run(file, engine);
        }
        catch(Throwable ex) {
            if(throwExceptions) {
                throw new RuntimeException(ex);
            }
            printException(ex);
        }
        finally {
            // End writing to the Console
            ConsoleOutput.end();
            
            // Finalise RefreshUIHandler
            RefreshUIHandler.finalise();
            
            // Run the Commands on the CommandStack to enable Undo/Redo
            CommandHandler.finalise();
            
            // Dispose any resources that a binding object may be holding onto
            for(Object object : engine.getBindings(ScriptContext.ENGINE_SCOPE).values()) {
                if(object instanceof IArchiScriptBinding binding) {
                    binding.dispose();
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
        engine.put("__SCRIPTS_DIR__", ArchiScriptPlugin.getInstance().getUserScriptsFolder().getAbsolutePath() + File.separator);
    }
    
    /**
     * Declared DOM extensions are registered
     */
    private void defineExtensionGlobalVariables(ScriptEngine engine) {
        for(Entry<String, Object> entry : DomExtensionFactory.getDOMExtensions().entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
    }

	private void printException(Throwable ex) {
	    // The init file must declare functions exit() and quit() to throw an exception with message "__EXIT__"
	    if(ex instanceof ScriptException && ex.getMessage().contains("__EXIT__")) {
	        System.out.println("Exited");
	    }
	    // Other exception
	    else {
	        // GraalVM exception
	        if(ex instanceof ScriptException && ex.getCause() instanceof PolyglotException) {
	            printStackTrace(ex.getCause(), 5);
	        }
	        // ArchiScriptException
	        else if(ex instanceof ArchiScriptException || ex.getCause() instanceof ArchiScriptException) {
	            printStackTrace(ex, 5);
	        }
	        // Nashorn or other
	        else {
	            printStackTrace(ex, 5);
	        }
	    }
	}
	
	private void printStackTrace(Throwable ex, int stackLines) {
	    System.err.println(ex);
        StackTraceElement[] elements = ex.getStackTrace();
        for(int i = 0; i < stackLines && i < elements.length; i++) {
            System.err.println("\tat " + elements[i]);
        }
	}
}
