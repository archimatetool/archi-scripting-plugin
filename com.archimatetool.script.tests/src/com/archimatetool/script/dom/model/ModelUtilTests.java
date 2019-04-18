/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.testingtools.ArchimateTestModel;

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
    
    private ArchimateModelProxy testModelProxy;
    
    private void loadTestModel() {
        testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
    }
    
    @Test
    public void isCorrectFolderForObject() {
        loadTestModel();
        
        // Business Interface
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "1544");
        // parent folders
        assertTrue(ModelUtil.isCorrectFolderForObject((IFolder)concept.eContainer(), concept));
        assertTrue(ModelUtil.isCorrectFolderForObject((IFolder)concept.eContainer().eContainer(), concept));
        // other business folder
        IFolder folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3eb047a9");
        assertTrue(ModelUtil.isCorrectFolderForObject(folder, concept));
        // application folder
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4a8e833a");
        assertFalse(ModelUtil.isCorrectFolderForObject(folder, concept));
        // view
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "e64e9b49");
        assertFalse(ModelUtil.isCorrectFolderForObject(folder, concept));
        
        // Relationship
        concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "693");
        // parent folders
        assertTrue(ModelUtil.isCorrectFolderForObject((IFolder)concept.eContainer(), concept));
        assertTrue(ModelUtil.isCorrectFolderForObject((IFolder)concept.eContainer().eContainer(), concept));
        assertTrue(ModelUtil.isCorrectFolderForObject((IFolder)concept.eContainer().eContainer().eContainer(), concept));
        // other relationship folder
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3de37d5c");
        assertTrue(ModelUtil.isCorrectFolderForObject(folder, concept));
        // application folder
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4a8e833a");
        assertFalse(ModelUtil.isCorrectFolderForObject(folder, concept));
        // view
        folder = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "e64e9b49");
        assertFalse(ModelUtil.isCorrectFolderForObject(folder, concept));
        assertTrue(ModelUtil.isCorrectFolderForObject(folder, IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
    }
    
    @Test
    public void isAllowedSetType() {
        loadTestModel();
        
        // Business Role
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
    
    @Test
    public void getIntValueFromMap() {
        Map<String, Number> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", 2.1);
        
        assertEquals(1, ModelUtil.getIntValueFromMap(map, "key1", 10));
        assertEquals(2, ModelUtil.getIntValueFromMap(map, "key2", 10));
        assertEquals(3, ModelUtil.getIntValueFromMap(map, "key3", 3));
        
        // Null map
        assertEquals(4, ModelUtil.getIntValueFromMap(null, "key4", 4));
    }
    
    @Test
    public void getStringValueFromMap() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "hello");
        map.put("key2", "there");
        
        assertEquals("hello", ModelUtil.getStringValueFromMap(map, "key1", ""));
        assertEquals("there", ModelUtil.getStringValueFromMap(map, "key2", ""));
        assertEquals("oscar", ModelUtil.getStringValueFromMap(map, "key3", "oscar"));

        // Null map
        assertEquals("bogus", ModelUtil.getStringValueFromMap(null, "key4", "bogus"));
    }

    @Test
    public void checkComponentsInSameModel() {
        ArchimateTestModel testModel1 = new ArchimateTestModel();
        IArchimateModel model = testModel1.createSimpleModel();
        IArchimateModelObject o1 = (IArchimateModelObject)testModel1.createModelElementAndAddToModel(IArchimatePackage.eINSTANCE.getBusinessActor());
        IArchimateModelObject o2 = (IArchimateModelObject)testModel1.createModelElementAndAddToModel(IArchimatePackage.eINSTANCE.getBusinessEvent());
        
        // Should not throw an exception
        ModelUtil.checkComponentsInSameModel(model, o1, o2);
    }
    
    @Test(expected = ArchiScriptException.class)
    public void checkComponentsInSameModel_Exception() {
        ArchimateTestModel testModel1 = new ArchimateTestModel();
        IArchimateModel model1 = testModel1.createSimpleModel();
        
        ArchimateTestModel testModel2 = new ArchimateTestModel();
        testModel2.createSimpleModel();
        IArchimateModelObject o3 = (IArchimateModelObject)testModel2.createModelElementAndAddToModel(IArchimatePackage.eINSTANCE.getBusinessActor());
        IArchimateModelObject o4 = (IArchimateModelObject)testModel2.createModelElementAndAddToModel(IArchimatePackage.eINSTANCE.getBusinessEvent());
        
        ModelUtil.checkComponentsInSameModel(model1, o3, o4);
    }
    
    // ==================================================================================================================================================
    // TODO!!!!!!!!
    // Delete these three tests and use the same methods in com.archimatetool.editor.model.DiagramModelUtils when the next version of Archi is released
    // ==================================================================================================================================================
    
    @Test
    public void getAbsoluteBounds() {
        IArchimateDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        
        IDiagramModelGroup dmo1 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dmo1.setBounds(10, 15, 500, 500);
        dm.getChildren().add(dmo1);
        
        IBounds bounds = ModelUtil.getAbsoluteBounds(dmo1);
        assertEquals(10, bounds.getX());
        assertEquals(15, bounds.getY());
        
        IDiagramModelGroup dmo2 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dmo2.setBounds(10, 15, 400, 400);
        dmo1.getChildren().add(dmo2);

        bounds = ModelUtil.getAbsoluteBounds(dmo2);
        assertEquals(20, bounds.getX());
        assertEquals(30, bounds.getY());
        
        IDiagramModelGroup dmo3 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dmo3.setBounds(10, 15, 300, 300);
        dmo2.getChildren().add(dmo3);

        bounds = ModelUtil.getAbsoluteBounds(dmo3);
        assertEquals(30, bounds.getX());
        assertEquals(45, bounds.getY());
    }
    
    
    @Test
    public void getRelativeBounds() {
        IArchimateDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        
        // Add main parent diagram model object
        IDiagramModelGroup dmo1 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dmo1.setBounds(10, 10, 200, 200);
        dm.getChildren().add(dmo1);
        
        // Add child
        IDiagramModelGroup dmo2 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dmo1.getChildren().add(dmo2);

        // Get relative bounds
        IBounds absoluteBounds = IArchimateFactory.eINSTANCE.createBounds(50, 60, 100, 100);
        IBounds relativebounds = ModelUtil.getRelativeBounds(absoluteBounds, dmo1);
        assertEquals(40, relativebounds.getX());
        assertEquals(50, relativebounds.getY());
        dmo2.setBounds(relativebounds);
        
        IDiagramModelGroup dmo3 = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        dmo2.getChildren().add(dmo3);

        absoluteBounds = IArchimateFactory.eINSTANCE.createBounds(90, 75, 500, 500);
        relativebounds = ModelUtil.getRelativeBounds(absoluteBounds, dmo2);
        assertEquals(40, relativebounds.getX());
        assertEquals(15, relativebounds.getY());
        dmo3.setBounds(relativebounds);
    }
 
    @Test
    public void outerBoundsContainsInnerBounds() {
        IBounds outer = IArchimateFactory.eINSTANCE.createBounds(0, 0, 100, 100);
        
        IBounds inner = IArchimateFactory.eINSTANCE.createBounds(0, 0, 100, 100);
        assertTrue(ModelUtil.outerBoundsContainsInnerBounds(outer, inner));
        
        inner = IArchimateFactory.eINSTANCE.createBounds(10, 10, 100, 100);
        assertFalse(ModelUtil.outerBoundsContainsInnerBounds(outer, inner));
        
        inner = IArchimateFactory.eINSTANCE.createBounds(10, 10, 90, 90);
        assertTrue(ModelUtil.outerBoundsContainsInnerBounds(outer, inner));
        
        inner = IArchimateFactory.eINSTANCE.createBounds(-10, -10, 90, 90);
        assertFalse(ModelUtil.outerBoundsContainsInnerBounds(outer, inner));

        inner = IArchimateFactory.eINSTANCE.createBounds(-0, 0, 101, 100);
        assertFalse(ModelUtil.outerBoundsContainsInnerBounds(outer, inner));
    }
}
