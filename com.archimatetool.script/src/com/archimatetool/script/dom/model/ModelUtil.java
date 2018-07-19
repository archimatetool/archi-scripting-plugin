/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
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
     * Create a new ArchimateElementProxy, adding it to a folder
     * @param model
     * @param type
     * @param name
     * @param parentFolder
     * @return
     */
    static ArchimateElementProxy createElement(IArchimateModel model, String type, String name, IFolder parentFolder) {
        if(model == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(StringUtils.safeString(name));
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !isCorrectFolderForConcept(parentFolder, element)) {
                parentFolder = model.getDefaultFolderForObject(element);
            }

            CommandHandler.executeCommand(new AddElementCommand(parentFolder, element));
            
            return new ArchimateElementProxy(element);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_0, type));
    }

    /**
     * Create a new ArchimateRelationshipProxy, adding it to a folder
     * @param model
     * @param type
     * @param name
     * @param source
     * @param target
     * @param parentFolder
     * @return
     */
    static ArchimateRelationshipProxy createRelationship(IArchimateModel model, String type, String name, IArchimateConcept source,
            IArchimateConcept target, IFolder parentFolder) {
        
        if(model == null || source == null || target == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateRelationship().isSuperTypeOf(eClass)) { // Check this is the correct type
            if(!ArchimateModelUtils.isValidRelationship(source, target, eClass)) {
                throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_3, type));
            }

            IArchimateRelationship relationship = (IArchimateRelationship)IArchimateFactory.eINSTANCE.create(eClass);
            relationship.setName(StringUtils.safeString(name));
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !isCorrectFolderForConcept(parentFolder, relationship)) {
                parentFolder = model.getDefaultFolderForObject(relationship);
            }
            
            CommandHandler.executeCommand(new AddRelationshipCommand(parentFolder, relationship, source, target));
            
            return new ArchimateRelationshipProxy(relationship);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_1, type));
    }

    /**
     * Create a new FolderProxy
     * @param parent
     * @param name
     * @return
     */
    static FolderProxy createFolder(IFolder parent, String name) {
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        folder.setName(StringUtils.safeString(name));
        folder.setType(FolderType.USER);
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parent.getArchimateModel()) { //$NON-NLS-1$
            @Override
            public void perform() {
                parent.getFolders().add(folder);
            }

            @Override
            public void undo() {
                parent.getFolders().remove(folder);
            }
        });
        
        return new FolderProxy(folder);
    }
    
    /**
     * Add a concept to a folder
     * If concept already has parent folder, it is moved
     * throws ArchiScriptException if incorrect folder type
     * @param concept
     * @param parent
     */
    static void addConcept(IArchimateConcept concept, IFolder parent) {
        if(!isCorrectFolderForConcept(parent, concept)) {
            throw new ArchiScriptException(Messages.ModelUtil_0);
        }
        
        CommandHandler.executeCommand(new ScriptCommand("Add", parent.getArchimateModel()) { //$NON-NLS-1$
            IFolder oldParent = (IFolder)concept.eContainer();
            int oldPosition;
            
            @Override
            public void perform() {
                if(oldParent != null) {
                    oldPosition = oldParent.getElements().indexOf(concept);
                    oldParent.getElements().remove(concept);
                }
                parent.getElements().add(concept);
            }

            @Override
            public void undo() {
                parent.getElements().remove(concept);
                if(oldParent != null) {
                    oldParent.getElements().add(oldPosition, concept);
                }
            }
        });
    }
    
    /**
     * @param folder
     * @param concept
     * @return true if the given parent folder is the correct folder to contain this concept
     */
    static boolean isCorrectFolderForConcept(IFolder folder, IArchimateConcept concept) {
        if(folder == null) {
            return false;
        }
        
        IFolder topFolder = folder.getArchimateModel().getDefaultFolderForObject(concept);
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
     * @param element
     * @param type kebab-case type
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
}