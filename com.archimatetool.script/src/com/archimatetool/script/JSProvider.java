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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.script.preferences.IPreferenceConstants;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;


/**
 * JS Provider
 */
@SuppressWarnings("removal")
public class JSProvider implements IScriptEngineProvider {
    
    public static String ID = "com.archimatetool.script.provider.js"; //$NON-NLS-1$
    
    @Override
    public void run(File file, ScriptEngine engine) throws IOException, ScriptException {
        // Initialize jArchi using the provided init.js script
        URL initURL = ArchiScriptPlugin.INSTANCE.getBundle().getEntry("js/init.js"); //$NON-NLS-1$
        try(InputStreamReader initReader = new InputStreamReader(initURL.openStream());) {
            engine.eval(initReader);
        }

        // Normalize filename so that nashorn's load() can run it
        String scriptPath = PlatformUtils.isWindows() ? file.getAbsolutePath().replace('\\', '/') : file.getAbsolutePath();

        // Evaluate the script
        engine.eval("load('" + scriptPath + "')");  //$NON-NLS-1$//$NON-NLS-2$
	}

    @Override
    public ScriptEngine createScriptEngine() {
        switch((ArchiScriptPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.PREFS_JS_ENGINE))) {
            case 0:
                return new ScriptEngineManager().getEngineByName("Nashorn"); //$NON-NLS-1$

            case 1:
                return new NashornScriptEngineFactory().getScriptEngine("--language=es6"); //$NON-NLS-1$

            case 2:
                // Need to set this either here or in runtime
                System.getProperties().put("polyglot.js.nashorn-compat", "true"); //$NON-NLS-1$ //$NON-NLS-2$

                ScriptEngine engine = new ScriptEngineManager().getEngineByName("graal.js"); //$NON-NLS-1$

                // See https://www.graalvm.org/reference-manual/js/ScriptEngine/
//                Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
//                bindings.put("polyglot.js.allowHostAccess", true);
//                bindings.put("polyglot.js.allowIO", true);
//                bindings.put("polyglot.js.allowNativeAccess", true);
//                bindings.put("polyglot.js.allowCreateThread", true);
//                bindings.put("polyglot.js.allowHostClassLookup", true);
//                bindings.put("polyglot.js.allowHostClassLoading", true);
//                bindings.put("polyglot.js.allowAllAccess", true);

                return engine;

            default:
                return null;
        }
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getName() {
        return "jArchi"; //$NON-NLS-1$
    }
    
    @Override
    public String[] getSupportedFileExtensions() {
        return new String[] { ".ajs" }; //$NON-NLS-1$
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
        return ArchiScriptPlugin.INSTANCE.getBundle().getEntry("templates/new.ajs"); //$NON-NLS-1$
    }
}
