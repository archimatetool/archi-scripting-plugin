/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.script.ScriptEngine;

import org.junit.jupiter.api.Test;

import com.archimatetool.script.preferences.IPreferenceConstants;

/**
 * JSProvider Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class JSProviderTests {
    
    @Test
    public void engineIsValid() throws Exception {
        JSProvider provider = new JSProvider();
        ScriptEngine engine = provider.createScriptEngine().orElse(null);
        assertNotNull(engine);
        
        assertEquals("com.oracle.truffle.js.scriptengine.GraalJSScriptEngine", engine.getClass().getName());
        
        assertEquals("true", System.getProperty("polyglot.js.nashorn-compat"));
        assertEquals("false", System.getProperty("polyglot.engine.WarnInterpreterOnly"));
        assertEquals("latest", System.getProperty("polyglot.js.ecmascript-version"));
        
        assertEquals("true", System.getProperty("polyglot.js.commonjs-require"));
        assertEquals(ArchiScriptPlugin.getInstance().getPreferenceStore().getString(IPreferenceConstants.PREFS_SCRIPTS_FOLDER), System.getProperty("polyglot.js.commonjs-require-cwd"));
    }

}
