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
import org.eclipse.ui.PlatformUI;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.util.ArchimateModelUtils;

/**
 * Model Utils
 * 
 * @author Phillip Beauvoir
 */
public class ModelUtil {
    
    private ModelUtil() {
    }
    
    /**
     * @param folder
     * @param concept
     * @return true if the given parent folder is the correct folder to contain this concept
     */
    public static boolean isCorrectFolderForConcept(IFolder folder, IArchimateConcept concept) {
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
    public static boolean isAllowedSetType(IArchimateConcept concept, String type) {
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
    public static void refreshDiagramModelComponent(IDiagramModelComponent dmc) {
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

    public static String getKebabCase(String string) {
        return string.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String getCamelCase(String string) {
        if(string == null || "".equals(string)) { //$NON-NLS-1$
            return string;
        }
        
        return Arrays.stream(string.split("\\-")) //$NON-NLS-1$
                .map(String::toLowerCase)
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining());
    }
}