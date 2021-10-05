/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;


import com.archimatetool.script.dom.model.ArchimateDiagramModelObjectProxyTests;
import com.archimatetool.script.dom.model.ArchimateDiagramModelProxyTests;
import com.archimatetool.script.dom.model.ArchimateElementProxyTests;
import com.archimatetool.script.dom.model.ArchimateModelProxyTests;
import com.archimatetool.script.dom.model.ArchimateRelationshipProxyTests;
import com.archimatetool.script.dom.model.CanvasDiagramModelProxyTests;
import com.archimatetool.script.dom.model.CurrentModelTests;
import com.archimatetool.script.dom.model.DiagramModelConnectionProxyTests;
import com.archimatetool.script.dom.model.DiagramModelGroupProxyTests;
import com.archimatetool.script.dom.model.DiagramModelNoteProxyTests;
import com.archimatetool.script.dom.model.DiagramModelReferenceProxyTests;
import com.archimatetool.script.dom.model.EObjectProxyCollectionTests;
import com.archimatetool.script.dom.model.FolderProxyTests;
import com.archimatetool.script.dom.model.ModelFactoryTests;
import com.archimatetool.script.dom.model.ModelTests;
import com.archimatetool.script.dom.model.ModelUtilTests;
import com.archimatetool.script.dom.model.ProfileProxyTests;
import com.archimatetool.script.dom.model.SelectorFilterFactoryTests;
import com.archimatetool.script.dom.model.SketchDiagramModelProxyTests;

import junit.framework.TestSuite;

@SuppressWarnings("nls")
public class AllTests {

    public static junit.framework.Test suite() {
		TestSuite suite = new TestSuite("com.archimatetool.script");

        suite.addTest(ArchimateDiagramModelObjectProxyTests.suite());
        suite.addTest(ArchimateDiagramModelProxyTests.suite());
        suite.addTest(ArchimateElementProxyTests.suite());
		suite.addTest(ArchimateModelProxyTests.suite());
        suite.addTest(ArchimateRelationshipProxyTests.suite());
        suite.addTest(CanvasDiagramModelProxyTests.suite());
        suite.addTest(CurrentModelTests.suite());
        suite.addTest(DiagramModelConnectionProxyTests.suite());
        suite.addTest(DiagramModelGroupProxyTests.suite());
        suite.addTest(DiagramModelNoteProxyTests.suite());
        suite.addTest(DiagramModelReferenceProxyTests.suite());
        suite.addTest(EObjectProxyCollectionTests.suite());
        suite.addTest(FolderProxyTests.suite());
        suite.addTest(ModelFactoryTests.suite());
		suite.addTest(ModelTests.suite());
        suite.addTest(ModelUtilTests.suite());
        suite.addTest(ProfileProxyTests.suite());
        suite.addTest(SelectorFilterFactoryTests.suite());
        suite.addTest(SketchDiagramModelProxyTests.suite());
		
        return suite;
	}

}