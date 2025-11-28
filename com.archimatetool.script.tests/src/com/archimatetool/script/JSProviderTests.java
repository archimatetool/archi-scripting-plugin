/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.archimatetool.script.preferences.IPreferenceConstants;

/**
 * JSProvider Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class JSProviderTests {
    
    @AfterAll
    public static void resetPrefs( ) {
        // Reset to default Graal engine
        ArchiScriptPlugin.getInstance().getPreferenceStore().setToDefault(IPreferenceConstants.PREFS_JS_ENGINE);
    }

    @BeforeAll
    public static void runOnce() {
        // Eclipse PDE JUnit workaround
        AllTests.setClassLoaderForPDETests();
    }

    @Test
    public void graalEngine() throws Exception {
        // Set to Graal engine
        ArchiScriptPlugin.getInstance().getPreferenceStore().setValue(IPreferenceConstants.PREFS_JS_ENGINE, 2);
        
        JSProvider provider = new JSProvider();
        
        provider.createScriptEngine().ifPresentOrElse(engine -> {
            assertEquals("com.oracle.truffle.js.scriptengine.GraalJSScriptEngine", engine.getClass().getName());
        }, () -> {
            fail("Graal engine not found");
        });
        
        assertEquals("true", System.getProperty("polyglot.js.nashorn-compat"));
        assertEquals("false", System.getProperty("polyglot.engine.WarnInterpreterOnly"));
        assertEquals("latest", System.getProperty("polyglot.js.ecmascript-version"));
        
        assertEquals("true", System.getProperty("polyglot.js.commonjs-require"));
        assertEquals(ArchiScriptPlugin.getInstance().getPreferenceStore().getString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER), System.getProperty("polyglot.js.commonjs-require-cwd"));
    }
    
    @Test
    public void nashornES5Engine() throws Exception {
        // Set to Nashorn ES5 engine
        ArchiScriptPlugin.getInstance().getPreferenceStore().setValue(IPreferenceConstants.PREFS_JS_ENGINE, 0);
        
        JSProvider provider = new JSProvider();
        
        provider.createScriptEngine().ifPresentOrElse(engine -> {
            assertEquals("org.openjdk.nashorn.api.scripting.NashornScriptEngine", engine.getClass().getName());
        }, () -> {
            fail("Nashorn ES5 engine not found");
        });
    }

    @Test
    public void nashornES6Engine() throws Exception {
        // Set to Nashorn ES6 engine
        ArchiScriptPlugin.getInstance().getPreferenceStore().setValue(IPreferenceConstants.PREFS_JS_ENGINE, 1);
        
        JSProvider provider = new JSProvider();
        
        provider.createScriptEngine().ifPresentOrElse(engine -> {
            assertEquals("org.openjdk.nashorn.api.scripting.NashornScriptEngine", engine.getClass().getName());
        }, () -> {
            fail("Nashorn ES6 engine not found");
        });
    }

}
