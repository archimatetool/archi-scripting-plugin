/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.ModelVersion;
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

    @Override
    protected IArchimateModel getEObject() {
        return (IArchimateModel)super.getEObject();
    }
    
    public EObjectProxy setPurpose(String purpose) {
        return attr(PURPOSE, purpose);
    }
    
    public String getPurpose() {
        return (String)attr(PURPOSE);
    }
    
    @Override
    public EObjectProxy setDocumentation(String documentation) {
        return setPurpose(documentation);
    }
    
    @Override
    public String getDocumentation() {
        return getPurpose();
    }
    
    public ArchimateModelProxy load(String path) {
        setEObject(IEditorModelManager.INSTANCE.loadModel(new File(path)));
        return this;
    }
    
    public ArchimateModelProxy create(String modelName) {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        model.setName(modelName);
        setEObject(model);
        
        IArchiveManager archiveManager = IArchiveManager.FACTORY.createArchiveManager(model);
        model.setAdapter(IArchiveManager.class, archiveManager);
        
        return this;
    }
    
    public ArchimateModelProxy copy() {
        return new ArchimateModelProxy(getEObject());
    }
    
    public ArchimateModelProxy save(String path) throws IOException {
        checkModelInUI();
        
        if(getEObject() != null) {
            File file = new File(path);
            getEObject().setFile(file);
        }

        return save();
    }
    
    public ArchimateModelProxy save() throws IOException {
        checkModelInUI();
        
        if(getEObject() != null && getEObject().getFile() != null) {
            getEObject().setVersion(ModelVersion.VERSION);
            IArchiveManager archiveManager = (IArchiveManager)getEObject().getAdapter(IArchiveManager.class);
            archiveManager.saveModel();
        }
        
        return this;
    }
    
    /**
     * @param type Type of element
     * @param name Name of element
     * @return The element
     */
    public EObjectProxy addElement(String type, String name) {
        checkModelInUI();
        
        if(getEObject() == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(type);
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(name);
            IFolder folder = getEObject().getDefaultFolderForObject(element);
            folder.getElements().add(element);
            return EObjectProxy.get(element);
        }
        
        return null;
    }
    
    public EObjectProxy addRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target) {
        checkModelInUI();
        
        if(getEObject() == null || source.getEObject() == null || target.getEObject() == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(type);
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateRelationship().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateRelationship relationship = (IArchimateRelationship)IArchimateFactory.eINSTANCE.create(eClass);
            relationship.setName(name);
            relationship.connect(source.getEObject(), target.getEObject());
            IFolder folder = getEObject().getDefaultFolderForObject(relationship);
            folder.getElements().add(relationship);
            return EObjectProxy.get(relationship);
        }
        
        return null;
    }
    
    /**
     * Open a model in the UI (models tree)
     * If Archi is not running has no effect
     * @return The ArchimateModelProxy
     */
    public ArchimateModelProxy openInUI() {
        if(PlatformUI.isWorkbenchRunning()) {
            IEditorModelManager.INSTANCE.openModel(getEObject());
        }
        
        return this;
    }
    
    public List<?> $(String selector) {
        ExtendedCollection list = new ExtendedCollection();
        
        if(getEObject() == null) {
            return list;
        }
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(selector);
        if(filter == null) {
            return list;
        }
        
        // Iterate over all model contents and filter objects into the list
        for(Iterator<EObject> iter = getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            
            if(filter.accept(eObject)) {
                list.add(EObjectProxy.get(eObject));
                
                if(filter.isSingle()) {
                    return list;
                }
            }
        }
        
        return list;
    }
    
    @Override
    public Object attr(String attribute) {
        if(PURPOSE.equals(attribute) && getEObject() != null) {
            return getEObject().getPurpose();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    public EObjectProxy attr(String attribute, Object value) {
        checkModelInUI();

        if(PURPOSE.equals(attribute) && getEObject() != null) {
            getEObject().setPurpose((String)value);
            return this;
        }
        
        return super.attr(attribute, value);
    }
}
