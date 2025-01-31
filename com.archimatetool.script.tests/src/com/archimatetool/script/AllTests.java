/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;


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
public class AllTests {
}