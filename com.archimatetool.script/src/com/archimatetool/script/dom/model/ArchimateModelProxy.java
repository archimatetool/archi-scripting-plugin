/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.osgi.util.NLS;
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
import com.archimatetool.script.ArchiScriptException;

/**
 * ArchiMate Model object wrapper proxy thing
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateModelProxy extends EObjectProxy {
    
    ArchimateModelProxy(IArchimateModel model) {
        super(model);
    }

    @Override
    protected IArchimateModel getEObject() {
        return (IArchimateModel)super.getEObject();
    }
    
    @Override
    public ArchimateModelProxy getModel() {
        return this;
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
        IArchimateModel model = IEditorModelManager.INSTANCE.loadModel(new File(path));
        
        if(model != null) {
            setEObject(model);
        }
        else {
            throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_2, path));
        }
        
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
        checkModelAccess();
        
        if(getEObject() != null) {
            File file = new File(path);
            getEObject().setFile(file);
        }

        return save();
    }
    
    public ArchimateModelProxy save() throws IOException {
        checkModelAccess();
        
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
    public ArchimateElementProxy addElement(String type, String name) {
        checkModelAccess();
        
        if(getEObject() == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(type);
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(name);
            IFolder folder = getEObject().getDefaultFolderForObject(element);
            folder.getElements().add(element);
            return new ArchimateElementProxy(element);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_0, type));
    }
    
    public ArchimateRelationshipProxy addRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target) {
        checkModelAccess();
        
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
            return new ArchimateRelationshipProxy(relationship);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_1, type));
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
    
    @Override
    public Object attr(String attribute) {
        if(PURPOSE.equals(attribute) && getEObject() != null) {
            return getEObject().getPurpose();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    public EObjectProxy attr(String attribute, Object value) {
        checkModelAccess();

        if(PURPOSE.equals(attribute) && getEObject() != null) {
            getEObject().setPurpose((String)value);
            return this;
        }
        
        return super.attr(attribute, value);
    }
}
