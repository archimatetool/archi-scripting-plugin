/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.canvas.model.ICanvasFactory;
import com.archimatetool.canvas.model.ICanvasModel;
import com.archimatetool.editor.diagram.ArchimateDiagramModelFactory;
import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.editor.utils.StringUtils;
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
class ModelFactory {
    
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
     * Add a concept to a folder
     * If concept already has parent folder, it is moved
     * throws ArchiScriptException if incorrect folder type
     */
    static void addConcept(IArchimateConcept concept, IFolder parentFolder) {
        // Ensure all components share the same model
        ModelUtil.checkComponentsInSameModel(concept, parentFolder);

        if(!ModelUtil.isCorrectFolderForObject(parentFolder, concept)) {
            throw new ArchiScriptException(Messages.ModelFactory_0);
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
        ModelUtil.checkComponentsInSameModel(model, parentFolder);

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
                throw new ArchiScriptException("Wrong view type: " + type); //$NON-NLS-1$
        }
        
        view[0].setName(StringUtils.safeString(name));
        
        // Check folder is correct for type, if not use default folder
        if(parentFolder == null || !ModelUtil.isCorrectFolderForObject(parentFolder, view[0])) {
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
        
        return (DiagramModelProxy)EObjectProxy.get(view[0]);
    }
    
    /**
     * Create and add an Archimate diagram object containing the given element to a container parent at position
     */
    static DiagramModelObjectProxy addArchimateDiagramObject(IDiagramModelContainer parent, IArchimateElement element,
            int x, int y, int width, int height, boolean autoNest) {
        
        if(!(parent.getDiagramModel() instanceof IArchimateDiagramModel)) {
            throw new ArchiScriptException(Messages.ModelFactory_1);
        }
        
        IDiagramModelArchimateObject dmo = ArchimateDiagramModelFactory.createDiagramModelArchimateObject(element);
        return createDiagramObject(parent, dmo, x, y, width, height, autoNest);
    }
    
    /**
     * Create and add a diagram object of type to a container parent at position
     */
    static DiagramModelObjectProxy createDiagramObject(IDiagramModelContainer parent, String type,
            int x, int y, int width, int height, boolean autoNest) {
        
        IDiagramModelObject dmo = null;
        
        switch(type) {
            case "note": //$NON-NLS-1$
                if(!(parent.getDiagramModel() instanceof IArchimateDiagramModel)) {
                    throw new ArchiScriptException(Messages.ModelFactory_2);
                }
                // Use Factory for defaults
                dmo = (IDiagramModelObject)new ArchimateDiagramModelFactory(IArchimatePackage.eINSTANCE.getDiagramModelNote()).getNewObject();
                break;

            case "group": //$NON-NLS-1$
                if(parent.getDiagramModel() instanceof ICanvasModel) {
                    throw new ArchiScriptException(Messages.ModelFactory_3);
                }
                // Use Factory for defaults
                dmo = (IDiagramModelObject)new ArchimateDiagramModelFactory(IArchimatePackage.eINSTANCE.getDiagramModelGroup()).getNewObject();
                break;

            default:
                throw new ArchiScriptException("Unsupported type"); //$NON-NLS-1$
        }
        
        return createDiagramObject(parent, dmo, x, y, width, height, autoNest);
    }

    /**
     * Add a diagram object to a container parent at position
     */
    private static DiagramModelObjectProxy createDiagramObject(IDiagramModelContainer parent, IDiagramModelObject dmo,
            int x, int y, int width, int height, boolean autoNest) {
        
        // Create new bounds for object
        IBounds bounds = IArchimateFactory.eINSTANCE.createBounds(x, y, width, height);
        
        IDiagramModelContainer[] newParent = new IDiagramModelContainer[1];
        newParent[0] = parent;
        
        // If this is true add the diagram model object nested inside of the topmost diagram model object that occupies that space
        if(autoNest) {
            newParent[0] = ModelUtil.getNestedParentAndBounds(parent, bounds);
        }

        dmo.setBounds(bounds);
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parent.getDiagramModel().getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                newParent[0].getChildren().add(dmo);
            }

            @Override
            public void undo() {
                newParent[0].getChildren().remove(dmo);
            }
        });
        
        return (DiagramModelObjectProxy)EObjectProxy.get(dmo);
    }
    
    /**
     * Add an Archimate diagram connection
     */
    static DiagramModelConnectionProxy addArchimateDiagramConnection(IArchimateRelationship relation, IDiagramModelArchimateComponent source,
            IDiagramModelArchimateComponent target) {
        
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
    
}