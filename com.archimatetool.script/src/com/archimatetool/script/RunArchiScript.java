/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.dom.IArchiScriptDOMFactory;
import com.archimatetool.script.dom.model.ArchimateModelProxy;
import com.archimatetool.script.dom.model.GlobalBinding;
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
        
        defineStandardGlobalVariables(engine);
        defineExtensionGlobalVariables(engine);
        
        FileReader reader = null;

        try {
            // Bind our class to the JS object
            // see https://stackoverflow.com/questions/31236550/defining-a-default-global-java-object-to-nashorn-script-engine
            
            // get JavaScript "global" object
            Object global = engine.eval("this"); //$NON-NLS-1$
            // get JS "Object" constructor object
            Object jsObject = engine.eval("Object"); //$NON-NLS-1$
            ((Invocable)engine).invokeMethod(jsObject, "bindProperties", global, new GlobalBinding()); //$NON-NLS-1$
            
            reader = new FileReader(file);

            ConsoleOutput.start();
            
            engine.eval(reader);
            
            // If there is a "main" function invoke that
            if("function".equals(engine.eval("typeof main"))) { //$NON-NLS-1$ //$NON-NLS-2$
                ((Invocable)engine).invokeFunction("main"); //$NON-NLS-1$
            }
        }
        catch(ScriptException | FileNotFoundException | NoSuchMethodException ex) {
            error(ex, ex.toString());
        }
        finally {
            ConsoleOutput.end();
            
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
            
            // Re-open any closed models
            for(IArchimateModel model : ArchimateModelProxy.CLOSED_MODELS) {
                IEditorModelManager.INSTANCE.openModel(model);
            }
        }
	}

    /**
     * Standard Eclipse Global Variables such as the window and workbench are registered
     */
    private void defineStandardGlobalVariables(ScriptEngine engine) {
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
