/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.canvas.model.ICanvasFactory;
import com.archimatetool.canvas.model.ICanvasModel;
import com.archimatetool.editor.diagram.ArchimateDiagramModelFactory;
import com.archimatetool.editor.model.commands.AddListMemberCommand;
import com.archimatetool.editor.ui.factory.IGraphicalObjectUIProvider;
import com.archimatetool.editor.ui.factory.ObjectUIFactory;
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
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IConnectable;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDiagramModelReference;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProfile;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.AddElementCommand;
import com.archimatetool.script.commands.AddRelationshipCommand;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.MoveDiagramObjectCommand;
import com.archimatetool.script.commands.ScriptCommand;
import com.archimatetool.script.commands.ScriptCommandWrapper;

/**
 * Model Utils
 * 
 * @author Phillip Beauvoir
 */
class ModelFactory implements IModelConstants {
    
    private ModelFactory() {
    }
    
    /**
     * Create a new ArchimateElementProxy, adding it to a parentFolder
     * If parentFolder is null use default folder
     */
    static ArchimateElementProxy createElement(IArchimateModel model, String type, String name, IFolder parentFolder) {
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(model, parentFolder);
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(StringUtils.safeString(name));
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !ModelUtil.isCorrectFolderForObject(parentFolder, element)) {
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
        ModelUtil.checkComponentsInSameModel(model, parentFolder, source, target);
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateRelationship().isSuperTypeOf(eClass)) { // Check this is the correct type
            if(!ArchimateModelUtils.isValidRelationship(source, target, eClass)) {
                throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_3, type));
            }

            IArchimateRelationship relationship = (IArchimateRelationship)IArchimateFactory.eINSTANCE.create(eClass);
            relationship.setName(StringUtils.safeString(name));
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !ModelUtil.isCorrectFolderForObject(parentFolder, relationship)) {
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
     * Add an object to a folder
     * If the object already has parent folder, it is moved
     * throws ArchiScriptException if incorrect folder type
     */
    static void addObject(IFolder parentFolder, IArchimateModelObject object) {
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(parentFolder, object);

        if(!ModelUtil.isCorrectFolderForObject(parentFolder, object)) {
            throw new ArchiScriptException(Messages.ModelFactory_0);
        }
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parentFolder.getArchimateModel()) { //$NON-NLS-1$
            IFolder oldParent = (IFolder)object.eContainer();
            int oldPosition;
            
            @Override
            public void perform() {
                if(oldParent != null) {
                    oldPosition = oldParent.getElements().indexOf(object);
                    oldParent.getElements().remove(object);
                }
                parentFolder.getElements().add(object);
            }

            @Override
            public void undo() {
                parentFolder.getElements().remove(object);
                if(oldParent != null) {
                    oldParent.getElements().add(oldPosition, object);
                }
            }
            
            @Override
            public void dispose() {
                super.dispose();
                oldParent = null;
            }
        });
    }
    
    /**
     * Add an subfolder to a folder
     * If the folder already has parent folder, it is moved
     * throws ArchiScriptException if incorrect folder type
     */
    static void addFolder(IFolder parentFolder, IFolder folder) {
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(parentFolder, folder);

        if(!ModelUtil.canAddFolder(parentFolder, folder)) {
            throw new ArchiScriptException(Messages.ModelFactory_6);
        }
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parentFolder.getArchimateModel()) { //$NON-NLS-1$
            IFolder oldParent = (IFolder)folder.eContainer();
            int oldPosition;
            
            @Override
            public void perform() {
                if(oldParent != null) {
                    oldPosition = oldParent.getFolders().indexOf(folder);
                    oldParent.getFolders().remove(folder);
                }
                parentFolder.getFolders().add(folder);
            }

            @Override
            public void undo() {
                parentFolder.getFolders().remove(folder);
                if(oldParent != null) {
                    oldParent.getFolders().add(oldPosition, folder);
                }
            }
            
            @Override
            public void dispose() {
                super.dispose();
                oldParent = null;
            }
        });
    }
    
    /**
     * Create a new view and add to parentFolder
     */
    static DiagramModelProxy createView(IArchimateModel model, String type, String name, IFolder parentFolder) {
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(model, parentFolder);

        final IDiagramModel view = switch(type) {
            case VIEW_ARCHIMATE -> {
                yield IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
            }

            case VIEW_SKETCH -> {
                yield IArchimateFactory.eINSTANCE.createSketchModel();
            }

            case VIEW_CANVAS -> {
                yield ICanvasFactory.eINSTANCE.createCanvasModel();
            }

            default -> {
                throw new ArchiScriptException("Wrong view type: " + type); //$NON-NLS-1$
            }
        };

        view.setName(StringUtils.safeString(name));
        
        final IFolder parent;
        
        // Check folder is correct for type, if not use default folder
        if(parentFolder == null || !ModelUtil.isCorrectFolderForObject(parentFolder, view)) {
            parent = model.getDefaultFolderForObject(view);
        }
        else {
            parent = parentFolder;
        }
        
        CommandHandler.executeCommand(new ScriptCommand("Create", model) { //$NON-NLS-1$
            @Override
            public void perform() {
                parent.getElements().add(view);
            }

            @Override
            public void undo() {
                if(PlatformUI.isWorkbenchRunning()) {
                    EditorManager.closeDiagramEditor(view);
                }
                
                parent.getElements().remove(view);
            }
        });
        
        return (DiagramModelProxy)EObjectProxy.get(view);
    }
    
    /**
     * Create and add an Archimate diagram object containing the given element to a container parent at position
     */
    static DiagramModelObjectProxy addArchimateDiagramObject(IDiagramModelContainer parent, IArchimateElement element,
            int x, int y, int width, int height, boolean autoNest) {
        
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(parent, element);

        if(!(parent.getDiagramModel() instanceof IArchimateDiagramModel)) {
            throw new ArchiScriptException(Messages.ModelFactory_1);
        }
        
        IDiagramModelArchimateObject dmo = ArchimateDiagramModelFactory.createDiagramModelArchimateObject(element);
        return createDiagramObject(parent, dmo, x, y, width, height, autoNest);
    }
    
    /**
     * Move dmo to new parent
     */
    static void moveDiagramModelObject(IDiagramModelContainer parent, IDiagramModelObject dmo) {
        // Must be in same diagram model
        if(parent.getDiagramModel() != dmo.getDiagramModel()) {
            throw new ArchiScriptException(Messages.ModelFactory_16);
        }
        
        // Can't add to self
        if(parent == dmo) {
            throw new ArchiScriptException(Messages.ModelFactory_17);
        }
        
        // Already a child
        if(parent.getChildren().contains(dmo)) {
            throw new ArchiScriptException(Messages.ModelFactory_18);
        }
        
        // Move it
        CommandHandler.executeCommand(new MoveDiagramObjectCommand(parent, dmo));
    }
    
    /**
     * Create and add a view reference to another view
     */
    static DiagramModelReferenceProxy createViewReference(IDiagramModelContainer parent, IDiagramModel dm,
            int x, int y, int width, int height, boolean autoNest) {
        
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(parent, dm);
        
        // Ensure that the dm is not the same
        if(parent.getDiagramModel() == dm) {
            throw new ArchiScriptException(Messages.ModelFactory_7);
        }

        IDiagramModelReference dmRef = IArchimateFactory.eINSTANCE.createDiagramModelReference();
        dmRef.setReferencedModel(dm);
        
        return (DiagramModelReferenceProxy)createDiagramObject(parent, dmRef, x, y, width, height, autoNest);
    }
    
    /**
     * Create and add a diagram object of type to a container parent at position
     */
    static DiagramModelObjectProxy createDiagramObject(IDiagramModelContainer parent, String type,
            int x, int y, int width, int height, boolean autoNest) {
        
        IDiagramModelObject dmo = switch(type) {
            case DIAGRAM_MODEL_NOTE, "note" -> { //Backward compatibility  //$NON-NLS-1$
                if(!(parent.getDiagramModel() instanceof IArchimateDiagramModel)) {
                    throw new ArchiScriptException(Messages.ModelFactory_2);
                }
                
                // Use Factory for defaults
                yield (IDiagramModelObject)new ArchimateDiagramModelFactory(IArchimatePackage.eINSTANCE.getDiagramModelNote()).getNewObject();
            }

            case DIAGRAM_MODEL_GROUP, "group" -> { //Backward compatibility //$NON-NLS-1$
                if(parent.getDiagramModel() instanceof ICanvasModel) {
                    throw new ArchiScriptException(Messages.ModelFactory_3);
                }
                
                // Use Factory for defaults
                yield (IDiagramModelObject)new ArchimateDiagramModelFactory(IArchimatePackage.eINSTANCE.getDiagramModelGroup()).getNewObject();
            }

            default -> {
                throw new ArchiScriptException("Unsupported type"); //$NON-NLS-1$
            }
        };
        
        return createDiagramObject(parent, dmo, x, y, width, height, autoNest);
    }

    /**
     * Add a diagram object to a container parent at position
     */
    private static DiagramModelObjectProxy createDiagramObject(IDiagramModelContainer parent, IDiagramModelObject dmo,
            int x, int y, int width, int height, boolean autoNest) {
        
        // Create new bounds for object
        IBounds bounds = createBounds(dmo, x, y, width, height);
        dmo.setBounds(bounds);
        
        final IDiagramModelContainer container;
        
        // If autoNest is true add the diagram model object nested inside of the topmost diagram model object that occupies that space
        if(autoNest) {
            container = ModelUtil.getNestedParentAndBounds(parent, bounds);
        }
        else {
            container = parent;
        }

        CommandHandler.executeCommand(new ScriptCommand("Add", container.getDiagramModel().getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                container.getChildren().add(dmo);
            }

            @Override
            public void undo() {
                container.getChildren().remove(dmo);
            }
        });
        
        return (DiagramModelObjectProxy)EObjectProxy.get(dmo);
    }
    
    /**
     * Add an Archimate diagram connection
     */
    static DiagramModelConnectionProxy addArchimateDiagramConnection(IArchimateRelationship relation, IDiagramModelArchimateComponent source,
            IDiagramModelArchimateComponent target) {
        
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(relation, source, target);

        // Ensure they share the same view
        if(source.getDiagramModel() != target.getDiagramModel()) {
            throw new ArchiScriptException(Messages.ModelFactory_4);
        }

        // Check valid source and target
        if(source.getArchimateConcept() != relation.getSource() || target.getArchimateConcept() != relation.getTarget()) {
            throw new ArchiScriptException(Messages.ModelFactory_5);
        }

        IDiagramModelArchimateConnection dmc = ArchimateDiagramModelFactory.createDiagramModelArchimateConnection(relation);
        
        CommandHandler.executeCommand(new ScriptCommand("Add", source.getDiagramModel().getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                dmc.connect(source, target);
            }

            @Override
            public void undo() {
                dmc.disconnect();
            }
            
            @Override
            public void redo() {
                dmc.reconnect();
            }
        });
        
        return new DiagramModelConnectionProxy(dmc);
    }

    /**
     * Add a plain diagram connection
     */
    static DiagramModelConnectionProxy createDiagramConnection(IConnectable source, IConnectable target) {
        // Ensure they share the same view
        if(source.getDiagramModel() != target.getDiagramModel()) {
            throw new ArchiScriptException(Messages.ModelFactory_4);
        }
        
        // Cannot connect between two ArchiMate objects
        if(source instanceof IDiagramModelArchimateComponent && target instanceof IDiagramModelArchimateComponent) {
            throw new ArchiScriptException(Messages.ModelFactory_8);
        }
        
        // Cannot connect if source or target is a plain connection
        if(source.eClass() == IArchimatePackage.eINSTANCE.getDiagramModelConnection() || target.eClass() == IArchimatePackage.eINSTANCE.getDiagramModelConnection()) {
            throw new ArchiScriptException(Messages.ModelFactory_9);
        }
        
        IDiagramModelConnection dmc = (IDiagramModelConnection)new ArchimateDiagramModelFactory(IArchimatePackage.eINSTANCE.getDiagramModelConnection()).getNewObject();
        
        CommandHandler.executeCommand(new ScriptCommand("Add", source.getDiagramModel().getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                dmc.connect(source, target);
            }

            @Override
            public void undo() {
                dmc.disconnect();
            }
            
            @Override
            public void redo() {
                dmc.reconnect();
            }
        });
        
        return new DiagramModelConnectionProxy(dmc);
    }
 
    /**
     * Create and add a new Profile to the model
     * @param conceptType is kebab case
     * @return a ProfileProxy
     */
    static ProfileProxy createProfileProxy(IArchimateModel model, String name, String conceptType, Map<String, Object> image) {
        if(!StringUtils.isSet(name)) {
            throw new ArchiScriptException(Messages.ModelFactory_10);
        }
        
        // Convert kebab to camel case
        conceptType = ModelUtil.getCamelCase(conceptType);
        
        // Check it's the correct type
        if(!ModelUtil.isArchimateConcept(conceptType)) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_11, conceptType));
        }
        
        // Can we add an image?
        if(image != null && !ModelUtil.canHaveImage(conceptType)) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_13, conceptType));
        }
        
        // If imagePath is not null check that the ArchiveManager has this image
        String imagePath = image != null ? ModelUtil.getStringValueFromMap(image, "path", null) : null; //$NON-NLS-1$
        if(imagePath != null && !ModelUtil.hasImage(model, imagePath)) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_12, imagePath));
        }
        
        return new ProfileProxy(createProfile(model, name, conceptType, imagePath));
    }
    
    /**
     * Create and add a new Profile to the model
     * @return The new Profile or throw an excpetion if it already exists
     */
    static IProfile createProfile(IArchimateModel model, String name, String conceptType, String imagePath) {
        // Do we have a profile of this name and type?
        IProfile profile = ArchimateModelUtils.getProfileByNameAndType(model, name, conceptType);
        if(profile != null) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_14, name, conceptType));
        }
        
        // No, so create and add a new profile
        profile = IArchimateFactory.eINSTANCE.createProfile();
        profile.setName(name);
        profile.setConceptType(conceptType);
        profile.setImagePath(imagePath);

        CommandHandler.executeCommand(new ScriptCommandWrapper(new AddListMemberCommand<IProfile>(model.getProfiles(), profile), model));

        return profile;
    }
    
    /**
     * Create a JS Image object (HashMap) or null if imagePath is null
     */
    static Map<String, Object> createImageObject(IArchimateModel model, String imagePath) {
        if(imagePath == null) {
            return null;
        }
        
        Map<String, Object> map = new HashMap<>();
        
        map.put("path", imagePath); //$NON-NLS-1$
        
        ImageData id = ModelUtil.getArchiveManager(model).createImageData(imagePath);
        if(id != null) {
            map.put("width", id.width); //$NON-NLS-1$
            map.put("height", id.height); //$NON-NLS-1$
        }
        
        return map;
    }
    
    /**
     * Create a bounds for the given dmo checking for default and invalid values
     */
    static IBounds createBounds(IDiagramModelObject dmo, int x, int y, int width, int height) {
        // -1 means use figure width or height from preferences
        if(width == -1 || height == -1) {
            IGraphicalObjectUIProvider provider = (IGraphicalObjectUIProvider)ObjectUIFactory.INSTANCE.getProvider(dmo);
            Dimension defaultSize = provider != null ? provider.getDefaultSize() : IGraphicalObjectUIProvider.defaultSize();
            
            width = width == -1 ? defaultSize.width : width;
            height = height == -1 ? defaultSize.height : height;
        }
        
        // Can't have zero or negative width/height
        if(width <= 0 || height <= 0) {
            throw new ArchiScriptException(Messages.ModelFactory_15);
        }
        
        return IArchimateFactory.eINSTANCE.createBounds(x, y, width, height);
    }
}