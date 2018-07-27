/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.junit.Test;

import com.archimatetool.canvas.model.ICanvasPackage;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;

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
    public void createElement_WithFolder() {
        loadTestModel();
        
        IFolder folder = testModelProxy.getEObject().getFolder(FolderType.APPLICATION);
        IFolder subfolder = IArchimateFactory.eINSTANCE.createFolder();
        subfolder.setType(FolderType.USER);
        folder.getFolders().add(subfolder);
        ArchimateElementProxy elementProxy = ModelUtil.createElement(testModelProxy.getArchimateModel(), "application-component", "Fido", subfolder);
        assertEquals("Fido", elementProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getApplicationComponent(), elementProxy.getEObject().eClass());
        assertSame(subfolder, elementProxy.getEObject().eContainer());
    }
    
    @Test
    public void createElement_FolderIsNull() {
        loadTestModel();
        
        ArchimateElementProxy elementProxy = ModelUtil.createElement(testModelProxy.getArchimateModel(), "application-component", "Fido", null);
        assertEquals("Fido", elementProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getApplicationComponent(), elementProxy.getEObject().eClass());
        assertEquals(testModelProxy.getEObject().getFolder(FolderType.APPLICATION), elementProxy.getEObject().eContainer());
    }
    
    @Test(expected=ArchiScriptException.class)
    public void createElement_Exception() {
        loadTestModel();
        
        ModelUtil.createElement(testModelProxy.getArchimateModel(), "association-relationship", "Fido", null);
    }

    @Test
    public void createRelationship_WithFolder() {
        loadTestModel();
        
        IFolder folder = testModelProxy.getEObject().getFolder(FolderType.RELATIONS);
        IFolder subfolder = IArchimateFactory.eINSTANCE.createFolder();
        subfolder.setType(FolderType.USER);
        folder.getFolders().add(subfolder);
        IArchimateConcept source = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IArchimateConcept target = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "507");
        ArchimateRelationshipProxy relationProxy = ModelUtil.createRelationship(testModelProxy.getArchimateModel(), "composition-relationship", "Fido",
                source, target, subfolder);
        assertEquals("Fido", relationProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getCompositionRelationship(), relationProxy.getEObject().eClass());
        assertSame(subfolder, relationProxy.getEObject().eContainer());
    }
    
    @Test
    public void createRelationship_FolderIsNull() {
        loadTestModel();
        
        IArchimateConcept source = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IArchimateConcept target = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "507");
        ArchimateRelationshipProxy relationProxy = ModelUtil.createRelationship(testModelProxy.getArchimateModel(), "composition-relationship", "Fido",
                source, target, null);
        assertEquals("Fido", relationProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getCompositionRelationship(), relationProxy.getEObject().eClass());
        assertSame(testModelProxy.getEObject().getFolder(FolderType.RELATIONS), relationProxy.getEObject().eContainer());
    }

    @Test(expected=ArchiScriptException.class)
    public void createRelationship_Exception() {
        loadTestModel();
        
        IArchimateConcept source = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IArchimateConcept target = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "507");
        ModelUtil.createRelationship(testModelProxy.getArchimateModel(), "node", "Fido", source, target, null);
    }

    @Test
    public void createFolder() {
        loadTestModel();
        
        IFolder parent = testModelProxy.getEObject().getFolder(FolderType.APPLICATION);
        FolderProxy folderProxy = ModelUtil.createFolder(parent, "Fido");
        assertEquals("Fido", folderProxy.getName());
        assertEquals(FolderType.USER, folderProxy.getEObject().getType());
        assertSame(parent, folderProxy.getEObject().eContainer());
    }
    
    @Test
    public void addConcept() {
        loadTestModel();
        
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "403e5717");
        ModelUtil.addConcept(concept, parent);
        assertSame(parent, concept.eContainer());
    }
    
    @Test
    public void addConcept_NoExistingParent() {
        loadTestModel();
        
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "403e5717");
        ModelUtil.addConcept(concept, parent);
        assertSame(parent, concept.eContainer());
    }
    
    @Test(expected=ArchiScriptException.class)
    public void moveConcept_Exception() {
        loadTestModel();
        
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4a8e833a");
        ModelUtil.addConcept(concept, parent);
    }
    
    @Test
    public void createView() {
        loadTestModel();
        
        IFolder parent = testModelProxy.getEObject().getFolder(FolderType.DIAGRAMS);

        DiagramModelProxy viewProxy = ModelUtil.createView(testModelProxy.getEObject(), "archimate", "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getArchimateDiagramModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());

        viewProxy = ModelUtil.createView(testModelProxy.getEObject(), "sketch", "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getSketchModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());

        viewProxy = ModelUtil.createView(testModelProxy.getEObject(), "canvas", "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(ICanvasPackage.eINSTANCE.getCanvasModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());
    }
    
    @Test
    public void addArchimateDiagramObject() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        IArchimateElement element = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "528");
        
        DiagramModelObjectProxy proxy = ModelUtil.addArchimateDiagramObject(view, element, 10, 15, 100, 200);
        assertTrue(proxy.getEObject() instanceof IDiagramModelArchimateObject);
        assertSame(view, proxy.getEObject().eContainer());
        IBounds bounds = proxy.getEObject().getBounds();
        assertEquals(10, bounds.getX());
        assertEquals(15, bounds.getY());
        assertEquals(100, bounds.getWidth());
        assertEquals(200, bounds.getHeight());
    }

    @Test
    public void createDiagramObject() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        
        String[] types = {"note", "group"};
        EClass[] classes = { IArchimatePackage.eINSTANCE.getDiagramModelNote(), IArchimatePackage.eINSTANCE.getDiagramModelGroup() };
        
        for(int i = 0; i < types.length; i++) {
            DiagramModelObjectProxy proxy = ModelUtil.createDiagramObject(view, types[i], 10, 15, 100, 200);
            assertTrue(proxy.getEObject().eClass() == classes[i]);
            assertSame(view, proxy.getEObject().eContainer());
            IBounds bounds = proxy.getEObject().getBounds();
            assertEquals(10, bounds.getX());
            assertEquals(15, bounds.getY());
            assertEquals(100, bounds.getWidth());
            assertEquals(200, bounds.getHeight());
        }
    }

    @Test(expected=ArchiScriptException.class)
    public void createDiagramObject_Exception() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        ModelUtil.createDiagramObject(view, "bogus", 10, 15, 100, 200);
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

}
