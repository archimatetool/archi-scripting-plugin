/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;

/**
 * Model Utils
 * 
 * @author Phillip Beauvoir
 */
class ModelUtil {
    
    private ModelUtil() {
    }
    
    /**
     * When adding a new object to a View and the nesting option is set this will find the foremost parent
     * container for the given bounds.
     * @param parent - the initial parent container which should be a View
     * @param bounds - the absolute bounds where we hope to add the object. This will be updated with new relative bounds.
     * @return the parent container, which might be the View itself, bounds x,y will be set to the relative co-ords of the parent
     */
    static IDiagramModelContainer getNestedParentAndBounds(IDiagramModelContainer parent, IBounds bounds) {
        // Get the actual parent if there is one that occupies that space
        for(Iterator<EObject> iter = parent.eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            
            if(eObject instanceof IDiagramModelContainer) {
                IDiagramModelObject dmo = (IDiagramModelObject)eObject;
                IBounds dmoBounds = DiagramModelUtils.getAbsoluteBounds(dmo);
                if(DiagramModelUtils.outerBoundsContainsInnerBounds(dmoBounds, bounds)) {
                    parent = (IDiagramModelContainer)eObject;
                }
            }
        }
        
        // Convert back to relative co-ords
        if(parent instanceof IDiagramModelObject) {
            IBounds newBounds = DiagramModelUtils.getRelativeBounds(bounds, (IDiagramModelObject)parent);
            bounds.setX(newBounds.getX());
            bounds.setY(newBounds.getY());
        }

        return parent;
    }

    /**
     * @return true if the given parent folder is the correct folder to contain this object
     */
    static boolean isCorrectFolderForObject(IFolder folder, EObject eObject) {
        if(folder == null || eObject == null) {
            return false;
        }
        
        // Check that the object is in the correct main category folder 
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
     * @return true if we can add folder to parent folder
     */
    static boolean canAddFolder(IFolder parent, IFolder folder) {
        // Only user folder types
        if(folder.getType() != FolderType.USER) {
            return false;
        }
        
        // Not the same parent folder
        if(folder.eContainer() == parent) {
            return false;
        }
        
        // Can't move to a descendant
        EObject f = parent;
        while(f instanceof IFolder) {
            if(f == folder) {
                return false;
            }
            f = f.eContainer();
        }
        
        // Common ancestor
        while(parent.eContainer() instanceof IFolder) {
            parent = (IFolder)parent.eContainer();
        }

        while(folder.eContainer() instanceof IFolder) {
            folder = (IFolder)folder.eContainer();
        }
        
        return (parent == folder);
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
    
    /**
     * Return the IArchiveManager for a model
     * @throws ArchiScriptException if null
     */
    static IArchiveManager getArchiveManager(IArchimateModel model) {
        IArchiveManager archiveManager = (IArchiveManager)model.getAdapter(IArchiveManager.class);
        if(archiveManager == null) {
            throw new ArchiScriptException("Could not load ArchiveManager"); //$NON-NLS-1$
        }
        return archiveManager;
    }
}