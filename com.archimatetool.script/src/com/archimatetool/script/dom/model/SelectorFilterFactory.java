/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;

/**
 * Selector Filter Factory
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
class SelectorFilterFactory implements IModelConstants {
    
    @FunctionalInterface
    interface ISelectorFilter {
        boolean accept(EObject eObject);
        
        default boolean isUnique() {
            return false;
        }
    }
    
    private static SelectorFilterFactory instance = new SelectorFilterFactory();
    
    static SelectorFilterFactory getInstance() {
        return instance;
    }
    
    private SelectorFilterFactory() {}

    /**
     * @return a ISelectorFilter for the given selector, or null
     */
    public ISelectorFilter getFilter(String selector) {
        if(selector == null || selector.length() == 0) {
            return null;
        }
        
        // All model concepts, diagram models, and folders
        if(selector.equals("*")) {
            return eObject -> {
                return eObject instanceof IArchimateConcept || eObject instanceof IDiagramModel || eObject instanceof IFolder;
            };
        }
        
        // All concepts
        else if(selector.equals(CONCEPT)) {
            return eObject -> {
                return getReferencedObject(eObject) instanceof IArchimateConcept;
            };
        }
        
        // All elements
        else if(selector.equals(ELEMENT)) {
            return eObject -> {
                return getReferencedObject(eObject) instanceof IArchimateElement;
            };
        }
        
        // All relationships
        else if(selector.equals(RELATION) || selector.equals(RELATIONSHIP)) {
            return eObject -> {
                return getReferencedObject(eObject) instanceof IArchimateRelationship;
            };
        }

        // All views
        else if(selector.equals(VIEW)) {
            return eObject -> {
                return eObject instanceof IDiagramModel;
            };
        }

        // Find single unique object by its Id (example - #1234)
        else if(selector.startsWith("#")) {
            return new ISelectorFilter() {
                @Override
                public boolean accept(EObject eObject) {
                    return eObject instanceof IIdentifier identifier && selector.substring(1).equals(identifier.getId());
                }
                
                @Override
                public boolean isUnique() {
                    return true;
                }
            };
        }
        
        // Find all objects with given name
        else if(selector.startsWith(".")) {
            return eObject -> {
                return eObject instanceof INameable nameable && selector.substring(1).equals(nameable.getName());
            };
        }
        
        // Find all objects with given type (class) and name
        else if(selector.contains(".") && selector.length() > 2) {
            String[] s = selector.split("\\.", 2);
            
            if(s.length != 2) {
                return null;
            }
            
            return eObject -> {
                String type = ModelUtil.getCamelCase(s[0]);
                String name = s[1];
                
                return getReferencedObject(eObject) instanceof INameable nameable &&
                        Objects.equals(nameable.getName(), name) && Objects.equals(nameable.eClass().getName(), type);
            };
        }

        // Class type of object
        return eObject -> {
            EObject resolved = getReferencedObject(eObject);
            return resolved != null && resolved.eClass().getName().equals(ModelUtil.getCamelCase(selector));
        };
    }
    
    private EObject getReferencedObject(EObject eObject) {
        return eObject instanceof IDiagramModelArchimateComponent dmac ? dmac.getArchimateConcept() : eObject;
    }
}
