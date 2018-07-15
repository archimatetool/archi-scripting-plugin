/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.ModelVersion;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.SetCommand;

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
        if(getEObject() != null) {
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.ARCHIMATE_MODEL__PURPOSE, purpose));
        }
        return this;
    }
    
    public String getPurpose() {
        return getEObject() != null ? getEObject().getPurpose() : null;
    }
    
    @Override
    public EObjectProxy setDocumentation(String documentation) {
        return setPurpose(documentation);
    }
    
    @Override
    public String getDocumentation() {
        return getPurpose();
    }
    
    public ArchimateModelProxy copy() {
        return new ArchimateModelProxy(getEObject());
    }
    
    public ArchimateModelProxy save(String path) throws IOException {
        if(getEObject() != null) {
            File file = new File(path);
            
            // Check we don't already have a model open in UI with the same file name
            if(PlatformUI.isWorkbenchRunning() && IEditorModelManager.INSTANCE.isModelLoaded(file)) {
                throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_5, file));
            }
            
            getEObject().setFile(file);
            
            return save();
        }

        return this;
    }
    
    public ArchimateModelProxy save() throws IOException {
        if(getEObject() != null && getEObject().getFile() != null) {
            getEObject().setVersion(ModelVersion.VERSION);
            IArchiveManager archiveManager = (IArchiveManager)getEObject().getAdapter(IArchiveManager.class);
            archiveManager.saveModel();
        }
        
        return this;
    }
    
    /**
     * Add an ArchiMate element and put in default folder
     */
    public ArchimateElementProxy addElement(String type, String name) {
        return ModelUtil.addElement(getEObject(), type, name, null);
    }
    
    /**
     * Add an ArchiMate element and put in folder
     */
    public ArchimateElementProxy addElement(String type, String name, FolderProxy parentFolder) {
        return ModelUtil.addElement(getEObject(), type, name, parentFolder.getEObject());
    }
    
    /**
     * Add an ArchiMate relationship and put in default folder
     */
    public ArchimateRelationshipProxy addRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target) {
        return ModelUtil.addRelationship(getEObject(), type, name, source.getEObject(), target.getEObject(), null);
    }
    
    /**
     * Add an ArchiMate relationship and put in folder
     */
    public ArchimateRelationshipProxy addRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target, FolderProxy parentFolder) {
        return ModelUtil.addRelationship(getEObject(), type, name, source.getEObject(), target.getEObject(), parentFolder.getEObject());
    }
    
    /**
     * Open a model in the UI (models tree)
     * If Archi is not running has no effect
     * @return The ArchimateModelProxy
     */
    public ArchimateModelProxy openInUI() {
        ModelUtil.openModelInUI(getEObject());
        return this;
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case PURPOSE:
            case DOCUMENTATION:
                return getPurpose();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case PURPOSE:
            case DOCUMENTATION:
                if(value instanceof String) {
                    return setPurpose((String)value);
                }
        }
        
        return super.attr(attribute, value);
    }
    
    /**
     * Set the Current Model to this
     * @return
     */
    public ArchimateModelProxy setAsCurrent() {
        CurrentModel.INSTANCE.setEObject(getEObject());
        return this;
    }

    // Expose find methods as public
    
    @Override
    public EObjectProxyCollection find() {
        return super.find();
    }
    
    @Override
    public EObjectProxyCollection find(EObject eObject) {
        return super.find(eObject);
    }
    
    @Override
    public EObjectProxyCollection find(EObjectProxy object) {
        return super.find(object);
    }
    
    @Override
    public EObjectProxyCollection find(String selector) {
        return super.find("*").filter(selector); //$NON-NLS-1$
    }
}
