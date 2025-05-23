/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.ecore.EClass;
import org.junit.jupiter.api.Test;

import com.archimatetool.canvas.model.ICanvasPackage;
import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.preferences.IPreferenceConstants;
import com.archimatetool.editor.ui.factory.IGraphicalObjectUIProvider;
import com.archimatetool.editor.ui.factory.ObjectUIFactory;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.TestFiles;


/**
 * ModelUtil Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ModelFactoryTests implements IModelConstants {
    
    private ArchimateModelProxy testModelProxy;
    
    private void loadTestModel() {
        testModelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
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
    
    @Test
    public void createElement_Exception() {
        loadTestModel();
        
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createElement(testModelProxy.getArchimateModel(), "association-relationship", "Fido", null);
        });
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

    @Test
    public void createRelationship_Exception() {
        loadTestModel();
        
        IArchimateConcept source = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IArchimateConcept target = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "507");

        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createRelationship(testModelProxy.getArchimateModel(), "node", "Fido", source, target, null);
        });
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
    public void addObject_Concept() {
        loadTestModel();
        
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "403e5717");
        ModelFactory.addObject(parent, concept);
        assertSame(parent, concept.eContainer());
    }
    
    @Test
    public void addObject_Concept_NoExistingParent() {
        loadTestModel();
        
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "403e5717");
        ModelFactory.addObject(parent, concept);
        assertSame(parent, concept.eContainer());
    }
    
    @Test
    public void addObject_Concept_Exception() {
        loadTestModel();
        
        IArchimateConcept concept = (IArchimateConcept)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "521");
        IFolder parent = (IFolder)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4a8e833a");
        
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.addObject(parent, concept);
        });
    }
    
    @Test
    public void addObject_View() {
        loadTestModel();
        
        IDiagramModel dm = (IDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3641");
        
        IFolder newParent = IArchimateFactory.eINSTANCE.createFolder();
        newParent.setType(FolderType.USER);
        newParent.setName("Test");
        ((IFolder)dm.eContainer()).getFolders().add(newParent);
        
        ModelFactory.addObject(newParent, dm);
        assertSame(newParent, dm.eContainer());
    }
    
    @Test
    public void addFolder() {
        loadTestModel();
        
        IFolder topFolder = testModelProxy.getArchimateModel().getFolder(FolderType.BUSINESS);
        
        // Add folder to diagrams folder
        IFolder folder1 = IArchimateFactory.eINSTANCE.createFolder();
        folder1.setType(FolderType.USER);
        folder1.setName("Test");
        topFolder.getFolders().add(folder1);
        
        // Add another folder to diagrams folder
        IFolder folder2 = IArchimateFactory.eINSTANCE.createFolder();
        folder2.setType(FolderType.USER);
        folder2.setName("Test 2");
        folder1.getFolders().add(folder2);
        
        // Add the second folder to the first folder
        ModelFactory.addFolder(topFolder, folder2);
        
        assertSame(topFolder, folder2.eContainer());
    }
    
    @Test
    public void createView() {
        loadTestModel();
        
        IFolder parent = testModelProxy.getEObject().getFolder(FolderType.DIAGRAMS);

        DiagramModelProxy viewProxy = ModelFactory.createView(testModelProxy.getEObject(), VIEW_ARCHIMATE, "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getArchimateDiagramModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());

        viewProxy = ModelFactory.createView(testModelProxy.getEObject(), VIEW_SKETCH, "test", parent);
        assertEquals("test", viewProxy.getName());
        assertEquals(IArchimatePackage.eINSTANCE.getSketchModel(), viewProxy.getEObject().eClass());
        assertSame(parent, viewProxy.getEObject().eContainer());

        viewProxy = ModelFactory.createView(testModelProxy.getEObject(), VIEW_CANVAS, "test", parent);
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
    public void addArchimateDiagramObjectUsesPreferencesWidthAndHeight() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        IArchimateElement element = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "528");
        
        DiagramModelObjectProxy proxy = ModelFactory.addArchimateDiagramObject(view, element, 10, 15, -1, -1, false);
        assertTrue(proxy.getEObject() instanceof IDiagramModelArchimateObject);
        assertSame(view, proxy.getEObject().eContainer());
        
        IBounds bounds = proxy.getEObject().getBounds();
        assertEquals(10, bounds.getX());
        assertEquals(15, bounds.getY());
        assertEquals(ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH), bounds.getWidth());
        assertEquals(ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT), bounds.getHeight());
    }

    @Test
    public void addArchimateDiagramObject_ThrowsExceptionIfBoundsIncorrect() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        IArchimateElement element = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "528");
        
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.addArchimateDiagramObject(view, element, 10, 15, 0, 0, false);
        });
    }
    
    @Test
    public void addArchimateDiagramObject_Nested() {
        loadTestModel();
        
        // "Layered View"
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4056");
        
        // Element
        IArchimateElement element = (IArchimateElement)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "528");
        
        // Put it inside the "Pay" object
        DiagramModelObjectProxy proxy = ModelFactory.addArchimateDiagramObject(view, element, 400, 408, 10, 10, true);
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
        
        String[] types = {DIAGRAM_MODEL_NOTE, DIAGRAM_MODEL_GROUP};
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

    @Test
    public void createDiagramObject_ThrowsExceptionIfWrongType() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createDiagramObject(view, "bogus", 10, 15, 100, 200, false);
        });
    }
    
    @Test
    public void createDiagramObject_ThrowsExceptionIfBoundsIncorrect() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createDiagramObject(view, DIAGRAM_MODEL_NOTE, 10, 15, 0, 200, false);
        });
    }
    
    @Test
    public void createViewReference() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        IArchimateDiagramModel parentView = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3641");
        DiagramModelReferenceProxy proxy = ModelFactory.createViewReference(parentView, view, 10, 10, 100, 100, false);
        
        assertSame(parentView, proxy.getEObject().eContainer());
        
        IBounds bounds = proxy.getEObject().getBounds();
        assertEquals(10, bounds.getX());
        assertEquals(10, bounds.getY());
        assertEquals(100, bounds.getWidth());
        assertEquals(100, bounds.getHeight());
    }
    
    @Test
    public void createViewReference_ThrowsExceptionIfSelf() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createViewReference(view, view, 10, 10, 100, 100, false);
        });
    }

    @Test
    public void createViewReference_ThrowsExceptionIfBoundsIncorrect() {
        loadTestModel();
        
        IArchimateDiagramModel view = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3965");
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createViewReference(view, view, 10, 10, 0, 0, false);
        });
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

    @Test
    public void addArchimateDiagramConnection_Exception() {
        loadTestModel();
        
        IArchimateRelationship relation = (IArchimateRelationship)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "756");
        IDiagramModelArchimateComponent source = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3790");
        IDiagramModelArchimateComponent target = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3774");
        
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.addArchimateDiagramConnection(relation, source, target);
        });
    }

    @Test
    public void createDiagramConnection() {
        loadTestModel();
        
        IDiagramModelArchimateComponent source = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3790");
        IDiagramModelArchimateComponent target = (IDiagramModelArchimateComponent)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "3788");
        IDiagramModelNote note = IArchimateFactory.eINSTANCE.createDiagramModelNote();
        source.getDiagramModel().getChildren().add(note);
        IDiagramModelGroup group = IArchimateFactory.eINSTANCE.createDiagramModelGroup();
        source.getDiagramModel().getChildren().add(group);
        
        // Cannot create plain connection between two ArchiMate concepts
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createDiagramConnection(source, target);
        });
        
        DiagramModelConnectionProxy proxy1 = ModelFactory.createDiagramConnection(note, group);
        assertTrue(proxy1.getEObject().eClass() == IArchimatePackage.eINSTANCE.getDiagramModelConnection());
        
        DiagramModelConnectionProxy proxy2 = ModelFactory.createDiagramConnection(group, source);
        assertTrue(proxy2.getEObject().eClass() == IArchimatePackage.eINSTANCE.getDiagramModelConnection());
        
        DiagramModelConnectionProxy proxy3 = ModelFactory.createDiagramConnection(note, target);
        assertTrue(proxy3.getEObject().eClass() == IArchimatePackage.eINSTANCE.getDiagramModelConnection());
        
        // Cannot create connection if one end is a plain connection
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createDiagramConnection(proxy1.getEObject(), target);
        });
        
        // Cannot create connection if one end is a plain connection
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createDiagramConnection(source, proxy1.getEObject());
        });
    }
    
    @Test
    public void createProfileProxy() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        
        // Add a Profile
        ProfileProxy proxy1 = ModelFactory.createProfileProxy(model, "Profile", "business-actor", null);
        assertEquals("Profile", proxy1.getName());
        assertEquals("business-actor", proxy1.getType());
        assertEquals(null, proxy1.getImage());
        
        assertEquals(1, model.getProfiles().size());
        
        // Add a Profile with the same name and type should throw an exception
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createProfileProxy(model, "Profile", "business-actor", null);
        });
    }
    
    @Test
    public void createProfileProxy_ThrowsExceptions() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        
        // Should throw an exception if type is wrong
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createProfileProxy(model, "Profile", "business-cat", null);
        });
    
        // Should throw an exception if image path references non existing image
        assertThrows(ArchiScriptException.class, () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", "imagePath");
            ModelFactory.createProfileProxy(model, "Profile", "business-actor", map);
        });
        
        // Should throw an exception if the profile already exists
        assertThrows(ArchiScriptException.class, () -> {
            // Create it
            ModelFactory.createProfileProxy(model, "Profile", "business-actor", null);
            
            // Create it again
            ModelFactory.createProfileProxy(model, "Profile", "business-actor", null);
        });
    }

    @Test
    public void createBounds() {
        IBounds bounds = ModelFactory.createBounds(IArchimateFactory.eINSTANCE.createDiagramModelNote(), 20, 20, 10, 10);
        assertEquals(20, bounds.getX());
        assertEquals(20, bounds.getY());
        assertEquals(10, bounds.getWidth());
        assertEquals(10, bounds.getHeight());
        
        checkDefaultBounds(IArchimateFactory.eINSTANCE.createDiagramModelNote());
        checkDefaultBounds(IArchimateFactory.eINSTANCE.createDiagramModelGroup());
        checkDefaultBounds(IArchimateFactory.eINSTANCE.createDiagramModelImage());
        checkDefaultBounds(IArchimateFactory.eINSTANCE.createDiagramModelReference());
        
        IDiagramModelArchimateObject dmao = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmao.setArchimateConcept(IArchimateFactory.eINSTANCE.createBusinessActor());
        checkDefaultBounds(dmao);
        
        dmao.setArchimateConcept(IArchimateFactory.eINSTANCE.createGrouping());
        checkDefaultBounds(dmao);
    }
    
    @Test
    public void createBounds_Exception() {
        IDiagramModelObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelNote();
        
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createBounds(dmo, 20, 20, 0, 10);
        });
        
        assertThrows(ArchiScriptException.class, () -> {
            ModelFactory.createBounds(dmo, 20, 20, 10, 0);
        });
    }
    
    private void checkDefaultBounds(IDiagramModelObject dmo) {
        IGraphicalObjectUIProvider provider = (IGraphicalObjectUIProvider)ObjectUIFactory.INSTANCE.getProvider(dmo);
        Dimension defaultSize = provider.getDefaultSize();

        IBounds bounds = ModelFactory.createBounds(dmo, 0, 0, -1, -1);
        assertEquals(0, bounds.getX());
        assertEquals(0, bounds.getY());
        assertEquals(defaultSize.width, bounds.getWidth());
        assertEquals(defaultSize.height, bounds.getHeight());
    }
}
