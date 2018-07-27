/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.canvas.model.ICanvasFactory;
import com.archimatetool.canvas.model.ICanvasModel;
import com.archimatetool.editor.diagram.ArchimateDiagramModelFactory;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.AddElementCommand;
import com.archimatetool.script.commands.AddRelationshipCommand;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.ScriptCommand;

/**
 * Model Utils
 * 
 * @author Phillip Beauvoir
 */
class ModelUtil {
    
    private ModelUtil() {
    }
    
    /**
     * Create a new ArchimateElementProxy, adding it to a parentFolder
     * If parentFolder is null use default folder
     */
    static ArchimateElementProxy createElement(IArchimateModel model, String type, String name, IFolder parentFolder) {
        // Ensure all components share the same model
        checkComponentsInSameModel(model, parentFolder);
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(StringUtils.safeString(name));
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !isCorrectFolderForObject(parentFolder, element)) {
                parentFolder = model.getDefaultFolderForObject(element);
            }

            CommandHandler.executeCommand(new AddElementCommand(parentFolder, element));
            
            return new ArchimateElementProxy(element);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_0, type));
    }

    /**
     * Create a new ArchimateRelationshipProxy, adding it to a folder
     * If parentFolder is null use default folder
     */
    static ArchimateRelationshipProxy createRelationship(IArchimateModel model, String type, String name, IArchimateConcept source,
            IArchimateConcept target, IFolder parentFolder) {
        
        // Ensure all components share the same model
        checkComponentsInSameModel(model, parentFolder, source, target);
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateRelationship().isSuperTypeOf(eClass)) { // Check this is the correct type
            if(!ArchimateModelUtils.isValidRelationship(source, target, eClass)) {
                throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_3, type));
            }

            IArchimateRelationship relationship = (IArchimateRelationship)IArchimateFactory.eINSTANCE.create(eClass);
            relationship.setName(StringUtils.safeString(name));
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !isCorrectFolderForObject(parentFolder, relationship)) {
                parentFolder = model.getDefaultFolderForObject(relationship);
            }
            
            CommandHandler.executeCommand(new AddRelationshipCommand(parentFolder, relationship, source, target));
            
            return new ArchimateRelationshipProxy(relationship);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_1, type));
    }

    /**
     * Create a new FolderProxy
     */
    static FolderProxy createFolder(IFolder parentFolder, String name) {
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        folder.setName(StringUtils.safeString(name));
        folder.setType(FolderType.USER);
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parentFolder.getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                parentFolder.getFolders().add(folder);
            }

            @Override
            public void undo() {
                parentFolder.getFolders().remove(folder);
            }
        });
        
        return new FolderProxy(folder);
    }
    
    /**
     * Add a concept to a folder
     * If concept already has parent folder, it is moved
     * throws ArchiScriptException if incorrect folder type
     */
    static void addConcept(IArchimateConcept concept, IFolder parentFolder) {
        // Ensure all components share the same model
        checkComponentsInSameModel(concept, parentFolder);

        if(!isCorrectFolderForObject(parentFolder, concept)) {
            throw new ArchiScriptException(Messages.ModelUtil_0);
        }
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parentFolder.getArchimateModel()) { //$NON-NLS-1$
            IFolder oldParent = (IFolder)concept.eContainer();
            int oldPosition;
            
            @Override
            public void perform() {
                if(oldParent != null) {
                    oldPosition = oldParent.getElements().indexOf(concept);
                    oldParent.getElements().remove(concept);
                }
                parentFolder.getElements().add(concept);
            }

            @Override
            public void undo() {
                parentFolder.getElements().remove(concept);
                if(oldParent != null) {
                    oldParent.getElements().add(oldPosition, concept);
                }
            }
        });
    }
    
    /**
     * Create a new view and add to parentFolder
     */
    static DiagramModelProxy createView(IArchimateModel model, String type, String name, IFolder parentFolder) {
        // Ensure all components share the same model
        checkComponentsInSameModel(model, parentFolder);

        final IDiagramModel[] view = new IDiagramModel[1];
        final IFolder[] parent = new IFolder[1];
        
        switch(type) {
            case "archimate":  //$NON-NLS-1$
                view[0] = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
                break;

            case "sketch":  //$NON-NLS-1$
                view[0] = IArchimateFactory.eINSTANCE.createSketchModel();
                break;

            case "canvas":  //$NON-NLS-1$
                view[0] = ICanvasFactory.eINSTANCE.createCanvasModel();
                break;

            default:
                throw new ArchiScriptException("Wring view type: " + type); //$NON-NLS-1$
        }
        
        view[0].setName(StringUtils.safeString(name));
        
        // Check folder is correct for type, if not use default folder
        if(parentFolder == null || !isCorrectFolderForObject(parentFolder, view[0])) {
            parent[0] = model.getDefaultFolderForObject(view[0]);
        }
        else {
            parent[0] = parentFolder;
        }
        
        CommandHandler.executeCommand(new ScriptCommand("Create", model) { //$NON-NLS-1$
            @Override
            public void perform() {
                parent[0].getElements().add(view[0]);
            }

            @Override
            public void undo() {
                if(PlatformUI.isWorkbenchRunning()) {
                    EditorManager.closeDiagramEditor(view[0]);
                }
                parent[0].getElements().remove(view[0]);
            }
        });
        
        return new DiagramModelProxy(view[0]);
    }
    
    static DiagramModelObjectProxy addArchimateDiagramObject(IDiagramModelContainer parent, IArchimateElement element, int x, int y, int width, int height) {
        if(!(parent.getDiagramModel() instanceof IArchimateDiagramModel)) {
            throw new ArchiScriptException(Messages.ModelUtil_1);
        }
        
        IDiagramModelArchimateObject dmo = ArchimateDiagramModelFactory.createDiagramModelArchimateObject(element);
        return createDiagramObject(parent, dmo, x, y, width, height);
    }
    
    /**
     * Create and add a diagram object of type to a container parent at position
     */
    static DiagramModelObjectProxy createDiagramObject(IDiagramModelContainer parent, String type, int x, int y, int width, int height) {
        EClass eClass = null;
        
        switch(type) {
            case "note": //$NON-NLS-1$
                if(!(parent.getDiagramModel() instanceof IArchimateDiagramModel)) {
                    throw new ArchiScriptException(Messages.ModelUtil_2);
                }
                eClass = IArchimatePackage.eINSTANCE.getDiagramModelNote();
                break;

            case "group": //$NON-NLS-1$
                if(parent.getDiagramModel() instanceof ICanvasModel) {
                    throw new ArchiScriptException(Messages.ModelUtil_3);
                }
                eClass = IArchimatePackage.eINSTANCE.getDiagramModelGroup();
                break;

            default:
                throw new ArchiScriptException("Unsupported type"); //$NON-NLS-1$
        }
        
        IDiagramModelObject dmo = (IDiagramModelObject)new ArchimateDiagramModelFactory(eClass).getNewObject();
        return createDiagramObject(parent, dmo, x, y, width, height);
    }

    /**
     * Add a diagram object to a container parent at position
     */
    private static DiagramModelObjectProxy createDiagramObject(IDiagramModelContainer parent, IDiagramModelObject dmo, int x, int y, int width, int height) {
        if(dmo.getBounds() == null) { // The ArchimateDiagramModelFactory doesn't add bounds in some cases
            dmo.setBounds(0, 0, -1, -1);
        }
        
        dmo.getBounds().setLocation(x, y);
        dmo.getBounds().setSize(width, height);
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parent.getDiagramModel().getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                parent.getChildren().add(dmo);
            }

            @Override
            public void undo() {
                parent.getChildren().remove(dmo);
            }
        });
        
        return new DiagramModelObjectProxy(dmo);
    }

    /**
     * @return true if the given parent folder is the correct folder to contain this object
     */
    static boolean isCorrectFolderForObject(IFolder folder, EObject eObject) {
        if(folder == null) {
            return false;
        }
        
        IFolder topFolder = folder.getArchimateModel().getDefaultFolderForObject(eObject);
        if(folder == topFolder) {
            return true;
        }
        
        EObject e = folder;
        while((e = e.eContainer()) != null) {
            if(e == topFolder) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * check allowed type
     * @return false if trying to set an invalid type
     */
    static boolean isAllowedSetType(IArchimateConcept concept, String type) {
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(getCamelCase(type));
        
        // Check source relationships
        for(IArchimateRelationship rel : concept.getSourceRelationships()) {
            if(!ArchimateModelUtils.isValidRelationship(eClass, rel.getTarget().eClass(), rel.eClass())) {
                return false;
            }
        }
        
        // Check target relationships
        for(IArchimateRelationship rel : concept.getTargetRelationships()) {
            if(!ArchimateModelUtils.isValidRelationship(rel.getSource().eClass(), eClass, rel.eClass())) {
                return false;
            }
        }
        
        // If a relationship, check ends
        if(concept instanceof IArchimateRelationship) {
            if(!ArchimateModelUtils.isValidRelationship(((IArchimateRelationship)concept).getSource(),
                    ((IArchimateRelationship)concept).getTarget(), eClass)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * If a DiagramModelComponent needs a refresh in a View, this does the trick/
     * It simply deletes the model component and adds it again causing a MVC refresh
     * @param dmc
     */
    static void refreshDiagramModelComponent(IDiagramModelComponent dmc) {
        if(PlatformUI.isWorkbenchRunning()) {
            IDiagramModelContainer parent = (IDiagramModelContainer)dmc.eContainer();
            if(parent != null) {
                if(dmc instanceof IDiagramModelObject) {
                    int index = parent.getChildren().indexOf(dmc);
                    parent.getChildren().remove(dmc);
                    parent.getChildren().add(index, (IDiagramModelObject)dmc);
                }
                else if(dmc instanceof IDiagramModelConnection) {
                    ((IDiagramModelConnection)dmc).disconnect();
                    ((IDiagramModelConnection)dmc).reconnect();
                }
            }
        }
    }

    static void openModelInUI(IArchimateModel model) {
        if(model != null && PlatformUI.isWorkbenchRunning()) {
            // If the model has already been loaded by a load() command
            if(IEditorModelManager.INSTANCE.isModelLoaded(model.getFile())) {
                // Need to do this!
                IEditorModelManager.INSTANCE.firePropertyChange(IEditorModelManager.INSTANCE, IEditorModelManager.PROPERTY_MODEL_OPENED,
                        null, model);
            }
            // Else from create()
            else {
                // If it's been saved already
                if(model.getFile() != null) {
                    IEditorModelManager.INSTANCE.openModel(model.getFile());
                }
                // Else
                else {
                    IEditorModelManager.INSTANCE.openModel(model);
                }
            }
        }
    }
    
    static String getKebabCase(String string) {
        return string.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    static String getCamelCase(String string) {
        if(string == null || "".equals(string)) { //$NON-NLS-1$
            return string;
        }
        
        return Arrays.stream(string.split("\\-")) //$NON-NLS-1$
                .map(String::toLowerCase)
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining());
    }
    
    /**
     * Get an integer value from a property map.
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    static int getIntValueFromMap(Map<?, ?> map, String key, int defaultValue) {
        return (map != null && map.get(key) instanceof Number) ? ((Number)map.get(key)).intValue() : defaultValue;
    }

    /**
     * Get a string value from a property map.
     * @param map
     * @param key
     * @param defaultValue
     * @return
     */
    static String getStringValueFromMap(Map<?, ?> map, String key, String defaultValue) {
        return (map != null && map.get(key) instanceof String) ? (String)map.get(key) : defaultValue;
    }
    
    /**
     * Check all components belong to the same model
     */
    static void checkComponentsInSameModel(IArchimateModelObject... eObjects) {
        IArchimateModel model = null;
        
        for(IArchimateModelObject eObject : eObjects) {
            if(eObject != null) {
                IArchimateModel thatModel = eObject.getArchimateModel();
                if(thatModel != null && model != null && thatModel != model) {
                    throw new ArchiScriptException("Components belong to different models!"); //$NON-NLS-1$
                }
                model = thatModel;
            }
        }
    }
}