/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.codehaus.groovy.jsr223.GroovyScriptEngineFactory;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
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
import com.archimatetool.script.preferences.IPreferenceConstants;
import com.archimatetool.script.views.console.ConsoleOutput;

import groovy.lang.GroovyClassLoader;
import groovyjarjarpicocli.CommandLine.ParentCommand;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;


/**
 * Script Runner
 */
public class RunArchiScript {
	private File file;

	public RunArchiScript(File file) {
		this.file = file;
	}
	
	public void run() {
        ScriptEngine engine;
        ScriptStarter starter;
        
        switch (ArchiScriptPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.PREFS_SCRIPTS_SUPPORT)) {
		case IPreferenceConstants.PREFS_JAVASCRIPT_ES6:
			NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
            engine = factory.getScriptEngine("--language=es6"); //$NON-NLS-1$
            starter = new JavascriptStarter();
			break;
		case IPreferenceConstants.PREFS_GROOVY:
			engine = new ScriptEngineManager().getEngineByName("groovy"); //$NON-NLS-1$
			GroovyClassLoader cl = ((GroovyScriptEngineImpl) engine).getClassLoader();
			Arrays.asList(new File(ArchiScriptPlugin.INSTANCE.getPreferenceStore().getString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER)).listFiles((dir, name) -> name.endsWith(".jar"))).forEach(jar -> {
				try {
					cl.addURL(jar.toURI().toURL());
				} catch (MalformedURLException e) {
				}
			});
			starter = new GroovyStarter();
			break;
		case IPreferenceConstants.PREFS_JAVASCRIPT_ES5:
		default:
			engine = new ScriptEngineManager().getEngineByName("JavaScript"); //$NON-NLS-1$
			starter = new JavascriptStarter();
			break;
		}
        
        defineGlobalVariables(engine);
        defineExtensionGlobalVariables(engine);
        setBindings(engine);
        
        try {
            starter.start(engine, file);
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
	        	// Customize message when scitping engine don't set the line number
	            System.out.println(sex.getLineNumber() == -1 ? "Exited on exit function" : "Exited at line number " + sex.getLineNumber()); //$NON-NLS-1$
	            return;
	        }
	    }
	    
        System.err.println("Script Error at: " + ex.getClass().getName() + ", " +  //$NON-NLS-1$//$NON-NLS-2$
                string);
	}
}
