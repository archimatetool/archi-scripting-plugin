/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


/**
 * Archi global binding Class to bind "$" and other global functions to the JS global "this" object
 * 
 * see https://stackoverflow.com/questions/31236550/defining-a-default-global-java-object-to-nashorn-script-engine
 * 
 * @author Phillip Beauvoir
 */
public class GlobalBinding {
    
    private ScriptEngine engine;
    private File scriptFile;
    
    public static void init(ScriptEngine engine, File scriptFile) throws NoSuchMethodException, ScriptException {
        // Get the JavaScript "global" object
        Object global = engine.eval("this"); //$NON-NLS-1$

        // Get the JS "Object" constructor object
        Object jsObject = engine.eval("Object"); //$NON-NLS-1$
        
        // Invoke the JS "bindProperties" function, and bind this class instance to "this"
        ((Invocable)engine).invokeMethod(jsObject, "bindProperties", global, new GlobalBinding(engine, scriptFile)); //$NON-NLS-1$
    }

    private GlobalBinding(ScriptEngine engine, File scriptFile) {
        this.scriptFile = scriptFile;
        this.engine = engine;
    }

    public List<?> $() {
        return Model.MODEL_INSTANCE.$("*"); //$NON-NLS-1$
    }
    
    public Object $(Object object) {
        // String is a selector on the model, so return a collection
        if(object instanceof String) {
            return Model.MODEL_INSTANCE.$((String)object);
        }
        
        // Else just return the same object
        return object;
    }
    
    /**
     * This is like the Nashorn load() function but allows a relative path to a local file
     * @param includeFile
     * @throws ScriptException
     * @throws IOException 
     */
    public void include(String includeFile) throws ScriptException, IOException {
        File f = new File(scriptFile.getParentFile(), includeFile);
        FileReader reader = new FileReader(f);
        engine.eval(reader);
        reader.close();
    }
}
