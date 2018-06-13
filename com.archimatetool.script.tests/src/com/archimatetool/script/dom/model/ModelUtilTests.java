/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;

import junit.framework.JUnit4TestAdapter;


/**
 * ModelUtil Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ModelUtilTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ModelUtilTests.class);
    }
    
    private static ArchimateModelProxy testModelProxy;
    
    @BeforeClass
    public static void loadTestModel() {
        testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
    }
    
    @Test
    public void isCorrectFolderForConcept() {
        // Business Interface
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "1544");
        // parent folders
        assertTrue(ModelUtil.isCorrectFolderForConcept((IFolder)concept.eContainer(), concept));
        assertTrue(ModelUtil.isCorrectFolderForConcept((IFolder)concept.eContainer().eContainer(), concept));
        // other business folder
        IFolder folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3eb047a9");
        assertTrue(ModelUtil.isCorrectFolderForConcept(folder, concept));
        // application folder
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4a8e833a");
        assertFalse(ModelUtil.isCorrectFolderForConcept(folder, concept));
        // view
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "e64e9b49");
        assertFalse(ModelUtil.isCorrectFolderForConcept(folder, concept));
        
        // Relationship
        concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "693");
        // parent folders
        assertTrue(ModelUtil.isCorrectFolderForConcept((IFolder)concept.eContainer(), concept));
        assertTrue(ModelUtil.isCorrectFolderForConcept((IFolder)concept.eContainer().eContainer(), concept));
        assertTrue(ModelUtil.isCorrectFolderForConcept((IFolder)concept.eContainer().eContainer().eContainer(), concept));
        // other relationship folder
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3de37d5c");
        assertTrue(ModelUtil.isCorrectFolderForConcept(folder, concept));
        // application folder
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4a8e833a");
        assertFalse(ModelUtil.isCorrectFolderForConcept(folder, concept));
        // view
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "e64e9b49");
        assertFalse(ModelUtil.isCorrectFolderForConcept(folder, concept));
    }
    
    @Test
    public void isAllowedSetType() {
        // Business Actor
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        assertTrue(ModelUtil.isAllowedSetType(concept, "business-actor"));
        assertFalse(ModelUtil.isAllowedSetType(concept, "business-interface"));
        assertTrue(ModelUtil.isAllowedSetType(concept, "business-role"));
        assertFalse(ModelUtil.isAllowedSetType(concept, "business-function"));
        assertFalse(ModelUtil.isAllowedSetType(concept, "node"));
        assertFalse(ModelUtil.isAllowedSetType(concept, "resource"));
        
        // serving relationship
        concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "f19393d1");
        assertTrue(ModelUtil.isAllowedSetType(concept, "serving-relationship"));
        assertTrue(ModelUtil.isAllowedSetType(concept, "association-relationship"));
        assertFalse(ModelUtil.isAllowedSetType(concept, "composition-relationship"));
        assertFalse(ModelUtil.isAllowedSetType(concept, "aggregation-relationship"));
        assertFalse(ModelUtil.isAllowedSetType(concept, "influence-relationship"));
        assertTrue(ModelUtil.isAllowedSetType(concept, "triggering-relationship"));
        assertTrue(ModelUtil.isAllowedSetType(concept, "flow-relationship"));
    }
    
    @Test
    public void getKebabCase() {
        assertEquals("", ModelUtil.getKebabCase(""));
        assertEquals("a", ModelUtil.getKebabCase("a"));
        assertEquals("a", ModelUtil.getKebabCase("A"));
        assertEquals("abc", ModelUtil.getKebabCase("ABC"));
        assertEquals("aa-bb-cc", ModelUtil.getKebabCase("AaBbCc"));
        assertEquals("archimate-diagram-model", ModelUtil.getKebabCase("ArchimateDiagramModel"));
        assertEquals("folder", ModelUtil.getKebabCase("Folder"));
        assertEquals("business-object", ModelUtil.getKebabCase("BusinessObject"));
    }

    @Test
    public void getCamelCase() {
        assertEquals("", ModelUtil.getCamelCase(""));
        assertEquals("A", ModelUtil.getCamelCase("a"));
        assertEquals("A", ModelUtil.getCamelCase("A"));
        assertEquals("ABC", ModelUtil.getCamelCase("a-b-c"));
        assertEquals("Folder", ModelUtil.getCamelCase("folder"));
        assertEquals("BusinessObject", ModelUtil.getCamelCase("business-object"));
        assertEquals("Businessobject", ModelUtil.getCamelCase("BusinessObject"));
    }
    

}
