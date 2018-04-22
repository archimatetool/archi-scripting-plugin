/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IFolder;
import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;

/**
 * ArchiMate Model object wrapper proxy thing
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateModelProxy extends EObjectProxy {
    
    public ArchimateModelProxy() {
    }

    public ArchimateModelProxy(IArchimateModel model) {
        super(model);
    }

    public IArchimateModel getArchimateModel() {
        return (IArchimateModel)getEObject();
    }
    
    public void setArchimateModel(IArchimateModel model) {
        setEObject(model);
    }
    
    public void setPurpose(String purpose) {
        attr(PURPOSE, purpose);
    }
    
    public String getPurpose() {
        return (String)attr(PURPOSE);
    }
    
    public ArchimateModelProxy load(String path) {
        setArchimateModel(IEditorModelManager.INSTANCE.loadModel(new File(path), false));
        return this;
    }
    
    public ArchimateModelProxy create(String modelName) {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        model.setName(modelName);
        setArchimateModel(model);
        return this;
    }
    
    public ArchimateModelProxy copy() {
        return new ArchimateModelProxy(getArchimateModel());
    }
    
    public ArchimateModelProxy save(String path) {
        // TODO
        System.out.println("called save(path)");
        return this;
    }
    
    public ArchimateModelProxy save() {
        // TODO
        System.out.println("called save()");
        return this;
    }
    
    public List<?> addElement(String type, String name) {
        ExtendedCollection list = new ExtendedCollection();
        
        if(getArchimateModel() == null) {
            return list;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(type);
        
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(name);
            IFolder folder = getArchimateModel().getDefaultFolderForObject(element);
            folder.getElements().add(element);
            list.add(new EObjectProxy(element));
        }
        
        return list;
    }
    
    public List<?> addRelationship(String type, String name, List<Object> source, List<Object> target) {
        ExtendedCollection list = new ExtendedCollection();
        
        if(getArchimateModel() == null) {
            return list;
        }
        
        // TODO
        
        
        return list;
    }
    
    /**
     * Open a model in the UI (models tree)
     * If Archi is not running has no effect
     * @return The ArchimateModelProxy
     */
    public ArchimateModelProxy openInUI() {
        if(PlatformUI.isWorkbenchRunning()) {
            IEditorModelManager.INSTANCE.openModel(getArchimateModel());
        }
        
        return this;
    }
    
    public List<?> $(String selector) {
        ExtendedCollection list = new ExtendedCollection();
        
        if(getArchimateModel() == null) {
            return list;
        }
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(selector);
        if(filter == null) {
            return list;
        }
        
        // Iterate over all model contents and filter objects into the list
        for(Iterator<EObject> iter = getArchimateModel().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            
            if(filter.accept(eObject)) {
                list.add(new EObjectProxy(eObject));
                
                if(filter.isSingle()) {
                    return list;
                }
            }
        }
        
        return list;
    }
}
