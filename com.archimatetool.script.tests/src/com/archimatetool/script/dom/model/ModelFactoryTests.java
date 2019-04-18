/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EClass;
import org.junit.Test;

import com.archimatetool.canvas.model.ICanvasPackage;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;

import junit.framework.JUnit4TestAdapter;


/**
 * ModelUtil Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ModelFactoryTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ModelFactoryTests.class);
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
        ArchimateElementProxy elementProxy = ModelFactory.createElement(testModelProxy.getArchimateModel(), "application-component", "Fido", subfolder);
        assertEquals("Fido", elementProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getApplicationComponent(), elementProxy.getEObject().eClass());
        assertSame(subfolder, elementProxy.getEObject().eContainer());
    }
    
    @Test
    public void createElement_FolderIsNull() {
        loadTestModel();
        
        ArchimateElementProxy elementProxy = ModelFactory.createElement(testModelProxy.getArchimateModel(), "application-component", "Fido", null);
        assertEquals("Fido", elementProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getApplicationComponent(), elementProxy.getEObject().eClass());
        assertEquals(testModelProxy.getEObject().getFolder(FolderType.APPLICATION), elementProxy.getEObject().eContainer());
    }
    
    @Test(expected=ArchiScriptException.class)
    public void createElement_Exception() {
        loadTestModel();
        
        ModelFactory.createElement(testModelProxy.getArchimateModel(), "association-relationship", "Fido", null);
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
        ArchimateRelationshipProxy relationProxy = ModelFactory.createRelationship(testModelProxy.getArchimateModel(), "composition-relationship", "Fido",
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
        ArchimateRelationshipProxy relationProxy = ModelFactory.createRelationship(testModelProxy.getArchimateModel(), "composition-relationship", "Fido",
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
        ModelFactory.createRelationship(testModelProxy.getArchimateModel(), "node", "Fido", source, target, null);
    }

    @Test
    public void createFolder() {
        loadTestModel();
        
        IFolder parent = testModelProxy.getEObject().getFolder(FolderType.APPLICATION);
        FolderProxy folderProxy = ModelFactory.createFolder(parent, "Fido");
        assertEquals("Fido", folderProxy.getName());
        assertEquals(FolderType.USER, folderProxy.getEObject().getType());
        assertSame(parent, folderProxy.getEObject().eContainer());
    }
    
    @Test
    public void addConcept() {
        loadTestModel();
        
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "403e5717");
        ModelFactory.addConcept(concept, parent);
        assertSame(parent, concept.eContainer());
    }
    
    @Test
    public void addConcept_NoExistingParent() {
        loadTestModel();
        
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "403e5717");
        ModelFactory.addConcept(concept, parent);
        assertSame(parent, concept.eContainer());
    }
    
    @Test(expected=ArchiScriptException.class)
    public void moveConcept_Exception() {
        loadTestModel();
        
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4a8e833a");
        ModelFactory.addConcept(concept, parent);
    }
    
    @Test
    public void createView() {
        loadTestModel();
        
        IFolder parent = testModelProxy.getEObject().getFolder(FolderType.DIAGRAMS);

        DiagramModelProxy viewProxy = ModelFactory.createView(testModelProxy.getEObject(), "archimate", "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getArchimateDiagramModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());

        viewProxy = ModelFactory.createView(testModelProxy.getEObject(), "sketch", "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getSketchModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());

        viewProxy = ModelFactory.createView(testModelProxy.getEObject(), "canvas", "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(ICanvasPackage.eINSTANCE.getCanvasModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());
    }
    
    @Test
    public void addArchimateDiagramObject() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        IArchimateElement element = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "528");
        
        DiagramModelObjectProxy proxy = ModelFactory.addArchimateDiagramObject(view, element, 10, 15, 100, 200, false);
        assertTrue(proxy.getEObject() instanceof IDiagramModelArchimateObject);
        assertSame(view, proxy.getEObject().eContainer());
        
        IBounds bounds = proxy.getEObject().getBounds();
        assertEquals(10, bounds.getX());
        assertEquals(15, bounds.getY());
        assertEquals(100, bounds.getWidth());
        assertEquals(200, bounds.getHeight());
    }

    @Test
    public void addArchimateDiagramObject_Nested() {
        loadTestModel();
        
        // "Layered View"
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4056");
        
        // Element
        IArchimateElement element = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "528");
        
        // Put it inside the "Pay" object
        DiagramModelObjectProxy proxy = ModelFactory.addArchimateDiagramObject(view, element, 400, 390, 10, 10, true);
        assertTrue(proxy.getEObject() instanceof IDiagramModelArchimateObject);
        assertEquals("4093", ((IIdentifier)proxy.getEObject().eContainer()).getId());
        
        IBounds bounds = proxy.getEObject().getBounds();
        assertEquals(29, bounds.getX());
        assertEquals(5, bounds.getY());
        assertEquals(10, bounds.getWidth());
        assertEquals(10, bounds.getHeight());
    }

    @Test
    public void createDiagramObject() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        
        String[] types = {"note", "group"};
        EClass[] classes = { IArchimatePackage.eINSTANCE.getDiagramModelNote(), IArchimatePackage.eINSTANCE.getDiagramModelGroup() };
        
        for(int i = 0; i < types.length; i++) {
            DiagramModelObjectProxy proxy = ModelFactory.createDiagramObject(view, types[i], 10, 15, 100, 200, false);
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
        ModelFactory.createDiagramObject(view, "bogus", 10, 15, 100, 200, false);
    }

    @Test
    public void addArchimateDiagramConnection() {
        loadTestModel();
        
        IArchimateRelationship relation = (IArchimateRelationship)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "756");

        IDiagramModelArchimateComponent source = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3790");
        IDiagramModelArchimateComponent target = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3788");
        
        DiagramModelConnectionProxy proxy = ModelFactory.addArchimateDiagramConnection(relation, source, target);
        assertTrue(proxy.getEObject() instanceof IDiagramModelArchimateConnection);
    }

    @Test(expected=ArchiScriptException.class)
    public void addArchimateDiagramConnection_Exception() {
        loadTestModel();
        
        IArchimateRelationship relation = (IArchimateRelationship)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "756");
        IDiagramModelArchimateComponent source = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3790");
        IDiagramModelArchimateComponent target = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3774");
        ModelFactory.addArchimateDiagramConnection(relation, source, target);
    }

}
