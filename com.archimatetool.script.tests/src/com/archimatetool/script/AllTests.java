/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;


import com.archimatetool.script.dom.model.ArchimateElementProxyTests;
import com.archimatetool.script.dom.model.ArchimateModelProxyTests;
import com.archimatetool.script.dom.model.DiagramModelObjectProxyTests;
import com.archimatetool.script.dom.model.ModelTests;

import junit.framework.TestSuite;

@SuppressWarnings("nls")
public class AllTests {

    public static junit.framework.Test suite() {
		TestSuite suite = new TestSuite("com.archimatetool.script");

        suite.addTest(ArchimateElementProxyTests.suite());
		suite.addTest(ArchimateModelProxyTests.suite());
        suite.addTest(DiagramModelObjectProxyTests.suite());
		suite.addTest(ModelTests.suite());
		
        return suite;
	}

}