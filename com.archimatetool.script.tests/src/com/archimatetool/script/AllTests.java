/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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

@RunWith(Suite.class)

@Suite.SuiteClasses({
    ArchimateDiagramModelObjectProxyTests.class,
    ArchimateDiagramModelProxyTests.class,
    ArchimateElementProxyTests.class,
    ArchimateModelProxyTests.class,
    ArchimateRelationshipProxyTests.class,
    CanvasDiagramModelProxyTests.class,
    CurrentModelTests.class,
    DiagramModelConnectionProxyTests.class,
    DiagramModelGroupProxyTests.class,
    DiagramModelNoteProxyTests.class,
    DiagramModelReferenceProxyTests.class,
    EObjectProxyCollectionTests.class,
    FolderProxyTests.class,
    ModelFactoryTests.class,
    ModelTests.class,
    ModelUtilTests.class,
    ProfileProxyTests.class,
    SelectorFilterFactoryTests.class,
    SketchDiagramModelProxyTests.class
})

public class AllTests {
}