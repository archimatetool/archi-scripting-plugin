/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import com.archimatetool.model.IFolder;
import com.archimatetool.model.ModelVersion;
import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;

/**
 * ArchiMate Model object wrapper proxy thing
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateModelProxy extends EObjectProxy {
    
    public static List<IArchimateModel> CLOSED_MODELS = new ArrayList<IArchimateModel>();
    
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
    
    @Override
    protected void setEObject(EObject eObject) {
        super.setEObject(eObject);
        
        // If the model is loaded in the UI...
        if(getArchimateModel() != null && IEditorModelManager.INSTANCE.isModelLoaded(getArchimateModel().getFile())) {
            // It's Dirty so throw exception
            if(IEditorModelManager.INSTANCE.isModelDirty(getArchimateModel())) {
                throw new RuntimeException(Messages.ArchimateModelProxy_0 + getArchimateModel().getFile());
            }
            
            // Close model and add to the global list
            try {
                IEditorModelManager.INSTANCE.closeModel(getArchimateModel());
                CLOSED_MODELS.add(getArchimateModel());
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
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
        setArchimateModel(IEditorModelManager.INSTANCE.loadModel(new File(path), false));
        return this;
    }
    
    public ArchimateModelProxy create(String modelName) {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        model.setName(modelName);
        setArchimateModel(model);
        
        IArchiveManager archiveManager = IArchiveManager.FACTORY.createArchiveManager(model);
        model.setAdapter(IArchiveManager.class, archiveManager);
        
        return this;
    }
    
    public ArchimateModelProxy copy() {
        return new ArchimateModelProxy(getArchimateModel());
    }
    
    public ArchimateModelProxy save(String path) throws IOException {
        if(getArchimateModel() != null) {
            File file = new File(path);
            getArchimateModel().setFile(file);
        }

        return save();
    }
    
    public ArchimateModelProxy save() throws IOException {
        if(getArchimateModel() != null && getArchimateModel().getFile() != null) {
            getArchimateModel().setVersion(ModelVersion.VERSION);
            IArchiveManager archiveManager = (IArchiveManager)getArchimateModel().getAdapter(IArchiveManager.class);
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
        if(getArchimateModel() == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(type);
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(name);
            IFolder folder = getArchimateModel().getDefaultFolderForObject(element);
            folder.getElements().add(element);
            return EObjectProxy.get(element);
        }
        
        return null;
    }
    
    public EObjectProxy addRelationship(String type, String name, EObjectProxy source, EObjectProxy target) {
        if(getArchimateModel() == null) {
            return null;
        }
        
        // TODO
        
        
        return null;
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
                list.add(EObjectProxy.get(eObject));
                
                if(filter.isSingle()) {
                    return list;
                }
            }
        }
        
        return list;
    }
    
    @Override
    protected boolean canReadAttr(String attribute) {
        return super.canReadAttr(attribute) || PURPOSE.equals(attribute);
    }
}
