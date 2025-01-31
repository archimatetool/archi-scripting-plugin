/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SelectClasses({
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

@SuiteDisplayName("All Model Tests")
public class AllModelTests {
}