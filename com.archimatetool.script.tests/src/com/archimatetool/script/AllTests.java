/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;


import java.util.Objects;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import com.archimatetool.script.dom.model.AllModelTests;

@Suite
@SelectClasses({
    AllModelTests.class,
    JSProviderTests.class,
    RunArchiScriptTests.class
})

@SuiteDisplayName("All Scripting Tests")
@SuppressWarnings("nls")
public class AllTests {
    
    /**
     * Workaround for PDE JUnit launch. If tests are launched from Eclipse PDE JUnit then set
     * the context classloader for the current thread to the Equinox classloader.
     * If this is not done ScriptEngineManager fails to find registered ScriptEngines due to the way it uses a ClassLoader
     * See https://github.com/eclipse-pde/eclipse.pde/issues/2132
     */
    public static void setClassLoaderForPDETests() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if(Objects.equals("org.eclipse.pde.internal.junit.runtime.SPIBundleClassLoader", loader.getClass().getName())) {
            Thread.currentThread().setContextClassLoader(AllTests.class.getClassLoader());
        }
    }
}