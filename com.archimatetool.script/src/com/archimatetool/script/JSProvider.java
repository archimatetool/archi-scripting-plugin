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
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import com.archimatetool.editor.utils.PlatformUtils;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.script.preferences.IPreferenceConstants;


/**
 * JS Provider
 */
@SuppressWarnings("nls")
public class JSProvider implements IScriptEngineProvider {
    
    public static final String ID = "com.archimatetool.script.provider.js";
    
    public static boolean isNashornInstalled() {
        return getNashornScriptEngineFactoryClass() != null;
    }
    
    /**
     * @return The class used for the Nashorn engine if it is installed, or null.
     */
    private static Class<?> getNashornScriptEngineFactoryClass() {
        Class<?> clazz = null;
        
        try {
            // Standalone Nashorn bundle
            Bundle bundle = Platform.getBundle("com.archimatetool.script.nashorn");
            if(bundle != null) {
                clazz = bundle.loadClass("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory");
            }
        }
        catch(ClassNotFoundException ex1) {
        }
        
        return clazz;
    }
    
    @Override
    public void run(File file, ScriptEngine engine) throws IOException, ScriptException {
        // Initialize jArchi using the provided init.js script
        URL initURL = ArchiScriptPlugin.INSTANCE.getBundle().getEntry("js/init.js");
        try(InputStreamReader initReader = new InputStreamReader(initURL.openStream())) {
            engine.eval(initReader);
        }

        // Normalize filename so that load() can run it
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
            switch(ArchiScriptPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.PREFS_JS_ENGINE)) {
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
        
        return engine;
    }
    
    private ScriptEngine getGraalScriptEngine() {
        // Nashorn compatibility
        System.setProperty("polyglot.js.nashorn-compat", "true");
        
        // Turn off console warnings
        System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
        
        // Set this for the ECMA version. Use latest.
        // See https://www.graalvm.org/latest/reference-manual/js/Options/
        System.setProperty("polyglot.js.ecmascript-version", "latest");

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

        // Enable *before* getting engine
        enableDebugger();

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
     * Enable debugger
     * @see https://www.graalvm.org/latest/tools/chrome-debugger/
     * @see https://github.com/oracle/graal/blob/master/docs/tools/chrome-debugger.md 
     */
    private void enableDebugger() {
        if(!PlatformUI.isWorkbenchRunning()   // Ensure we are running in the UI
                || PlatformUtils.isLinux()) { // On Linux, Chrome will freeze and the devtools URL can't be pasted into the address bar.
                                              // There will also be popup dialogs in Archi about waiting or force quitting the app.
            return;
        }
        
        // Default is disabled, so remove these properties before running a script
        System.getProperties().remove("polyglot.inspect");
        System.getProperties().remove("polyglot.inspect.Path");
        //System.getProperties().remove("polyglot.inspect.WaitAttached");
        //System.getProperties().remove("polyglot.inspect.Suspend");
        
        String port = ArchiScriptPlugin.INSTANCE.getPreferenceStore().getString(IPreferenceConstants.PREFS_DEBUGGER_PORT);
        
        // If Debugger not enabled or port not set
        if(!(ArchiScriptPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_DEBUGGER_ENABLED) && StringUtils.isSet(port))) {
            return;
        }
        
        MessageDialog dialog = new MessageDialog(null,
                Messages.JSProvider_0,
                null,
                Messages.JSProvider_1,
                MessageDialog.WARNING,
                0,
                Messages.JSProvider_2, Messages.JSProvider_3);
        
        if(dialog.open() != Window.OK) {
            return;
        }

        // Mac/Linux needs time to close the dialog window
        if(!PlatformUtils.isWindows()) {
            while(Display.getCurrent().readAndDispatch());
        }

        // Create URL
        String path = UUID.randomUUID().toString();
        String url = "devtools://devtools/bundled/js_app.html?ws=127.0.0.1:" + port + "/" + path; //$NON-NLS-1$ //$NON-NLS-2$

        // Copy URL to clipboard
        Clipboard clipboard = new Clipboard(null);
        clipboard.setContents(new Object[] {url}, new Transfer[] {TextTransfer.getInstance()});
        clipboard.dispose();

        // Setting these properties will set GraalVM to use Chrome Debug mode.
        // The user has to paste the URL into a Chrome browser or else Archi will freeze.
        System.setProperty("polyglot.inspect", port);
        System.setProperty("polyglot.inspect.Path", path);
        //System.setProperty("polyglot.inspect.WaitAttached", "true");
        //System.setProperty("polyglot.inspect.Suspend", "false");

        // The "polyglot.inspect.SuspensionTimeout" option is not present in the current version of chromeinspector
        //System.setProperty("polyglot.inspect.SuspensionTimeout", "1m");

        // Open Browser if it's set in preferences and exists
        String browserPath = ArchiScriptPlugin.INSTANCE.getPreferenceStore().getString(IPreferenceConstants.PREFS_DEBUGGER_BROWSER);
        if(new File(browserPath).exists()) {
            String[] paths = null;

            // Mac can open the devtools URL
            if(PlatformUtils.isMac()) {
                paths = new String[] { "open", "-a", browserPath, url }; //$NON-NLS-1$ //$NON-NLS-2$
            }
            // Windows/Linux can't open the devtools URL so just open the browser
            else {
                paths = new String[] { browserPath };
            }

            try {
                Runtime.getRuntime().exec(paths);
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
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
