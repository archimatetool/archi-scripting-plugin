/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.script.preferences.IPreferenceConstants;


/**
 * JS Provider
 */
@SuppressWarnings("nls")
public class JSProvider implements IScriptEngineProvider {
    
    public static String ID = "com.archimatetool.script.provider.js";
    
    public static boolean isNashornInstalled() {
        return getNashornScriptEngineFactoryClass() != null;
    }
    
    /**
     * @return The class used for the Nashorn engine
     *         Either the one shipped with Java or the standalone one, or null if not installed
     */
    private static Class<?> getNashornScriptEngineFactoryClass() {
        Class<?> clazz = null;
        
        try {
            // Java Nashorn
            clazz = Class.forName("jdk.nashorn.api.scripting.NashornScriptEngineFactory");
        }
        catch(ClassNotFoundException ex) {
            try {
                // Standalone Nashorn bundle
                Bundle bundle = Platform.getBundle("com.archimatetool.script.nashorn");
                if(bundle != null) {
                    clazz = bundle.loadClass("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory");
                }
            }
            catch(ClassNotFoundException ex1) {
            }
        }
        
        return clazz;
    }
    
    @Override
    public void run(File file, ScriptEngine engine) throws IOException, ScriptException {
        // Initialize jArchi using the provided init.js script
        URL initURL = ArchiScriptPlugin.INSTANCE.getBundle().getEntry("js/init.js");
        try(InputStreamReader initReader = new InputStreamReader(initURL.openStream());) {
            engine.eval(initReader);
        }

        // Normalize filename so that nashorn's load() can run it
        String scriptPath = PlatformUtils.isWindows() ? file.getAbsolutePath().replace('\\', '/') : file.getAbsolutePath();
        
        // Escape apostrophes to not conflict with the load('path') command
        scriptPath = scriptPath.replace("'", "\\\'");

        // Evaluate the script
        engine.eval("load('" + scriptPath + "')");
	}

    @Override
    public ScriptEngine createScriptEngine() {
        ScriptEngine engine = null;
        
        Class<?> clazz = getNashornScriptEngineFactoryClass();
        
        // If Nashorn is installed use the engine as set in user preferences
        if(clazz != null) {
            switch((ArchiScriptPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.PREFS_JS_ENGINE))) {
                case 0:
                    engine = new ScriptEngineManager(clazz.getClassLoader()).getEngineByName("nashorn");
                    break;

                case 1:
                    try {
                        // Get the NashornScriptEngineFactory by reflection
                        // This is the equivalent of: engine = new NashornScriptEngineFactory().getScriptEngine("--language=es6");
                        Object nashornScriptEngineFactory = clazz.getConstructor().newInstance();
                        Method getScriptEngineMethod = clazz.getMethod("getScriptEngine", String[].class);
                        engine = (ScriptEngine)getScriptEngineMethod.invoke(nashornScriptEngineFactory, new Object[] {new String[] {"--language=es6"}});
                    }
                    catch(Exception ex) {
                        ex.printStackTrace();
                    }
                    break;

                default:
                    engine = getGraalScriptEngine();
                    break;
            }
        }
        // Just use Graal
        else {
            engine = getGraalScriptEngine();
        }
        
        if(engine != null) {
            setBindings(engine);
        }
        
        return engine;
    }
    
    private ScriptEngine getGraalScriptEngine() {
        // Need to set this either here or in runtime
        System.setProperty("polyglot.js.nashorn-compat", "true");
        
        // Turn off console warnings
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        
        // Need this for GraalVM 22.2
        System.setProperty("polyglot.js.ecmascript-version", "2022");

        // Enable loading Node.js modules
        // See https://www.graalvm.org/latest/reference-manual/js/Modules/
        boolean commonJSEnabled = ArchiScriptPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_COMMONJS_ENABLED);
        if(commonJSEnabled) {
            System.setProperty("polyglot.js.commonjs-require", "true");
            System.setProperty("polyglot.js.commonjs-require-cwd", ArchiScriptPlugin.INSTANCE.getPreferenceStore().getString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER));
        }
        else {
            System.clearProperty("polyglot.js.commonjs-require");
            System.clearProperty("polyglot.js.commonjs-require-cwd");
        }

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js");
        
        // See https://www.graalvm.org/reference-manual/js/ScriptEngine/
//        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//        bindings.put("polyglot.js.ecmascript-version", "2022");
//        bindings.put("polyglot.js.nashorn-compat", true);
//        bindings.put("polyglot.js.allowIO", true);
//        bindings.put("polyglot.js.allowNativeAccess", true);
//        bindings.put("polyglot.js.allowCreateThread", true);
//        bindings.put("polyglot.js.allowHostClassLookup", true);
//        bindings.put("polyglot.js.allowHostClassLoading", true);
//        bindings.put("polyglot.js.allowAllAccess", true);

        return engine;
    }
    
    /**
     * Set/Remove some JS global bindings
     */
    private void setBindings(ScriptEngine engine) {
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        
        // Remove these
        bindings.remove("exit");
        bindings.remove("quit");
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return "jArchi";
    }
    
    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] { ".ajs" };
    }
    
    @Override
    public Image getImage() {
        return IArchiScriptImages.ImageFactory.getImage(IArchiScriptImages.ICON_SCRIPT);
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return IArchiScriptImages.ImageFactory.getImageDescriptor(IArchiScriptImages.ICON_SCRIPT);
    }

    @Override
    public URL getNewFile() {
        return ArchiScriptPlugin.INSTANCE.getBundle().getEntry("templates/new.ajs");
    }
}
