/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;

/**
 * ArchiMate Model object wrapper proxy thing
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateModelProxy {
    
    private IArchimateModel fModel;
    
    public ArchimateModelProxy() {
    }

    public ArchimateModelProxy(IArchimateModel model) {
        fModel = model;
    }

    public IArchimateModel getModel() {
        return fModel;
    }
    
    public void setModel(IArchimateModel model) {
        fModel = model;
    }
    
    public ArchimateModelProxy load(String path) {
        fModel = IEditorModelManager.INSTANCE.loadModel(new File(path), false);
        return this;
    }
    
    public ArchimateModelProxy copy() {
        return new ArchimateModelProxy(fModel);
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
    
    public List<Object> addElement(String type, String name) {
        // TODO
        System.out.println("called addElement()");
        return new ExtendedCollection<>();
    }
    
    public List<Object> addRelationship(String type, String name, List<Object> source, List<Object> target) {
        // TODO
        System.out.println("called addRelationship()");
        return new ExtendedCollection<>();
    }
    
    /**
     * Open a model in the UI (models tree)
     * If Archi is not running has no effect
     * @return The ArchimateModelProxy
     */
    public ArchimateModelProxy openInUI() {
        if(PlatformUI.isWorkbenchRunning()) {
            IEditorModelManager.INSTANCE.openModel(fModel);
        }
        
        return this;
    }
    
    public List<Object> $(String selector) {
        List<Object> list = new ExtendedCollection<Object>();
        
        if(fModel == null) {
            return list;
        }
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(selector);
        if(filter == null) {
            return list;
        }
        
        // Iterate over all model contents and filter objects into the list
        for(Iterator<EObject> iter = fModel.eAllContents(); iter.hasNext();) {
            EObject object = iter.next();
            
            if(filter.accept(object)) {
                list.add(object);
                
                if(filter.isSingle()) {
                    return list;
                }
            }
        }
        
        return list;
    }
}
