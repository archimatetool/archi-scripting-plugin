/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
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
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.AddElementCommand;
import com.archimatetool.script.commands.AddRelationshipCommand;
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
        
        // Don't add a CommandStack. One will be added if openInUI() is called
        
        return this;
    }
    
    public ArchimateModelProxy copy() {
        return new ArchimateModelProxy(getEObject());
    }
    
    public ArchimateModelProxy save(String path) throws IOException {
        if(getEObject() != null) {
            File file = new File(path);
            if(file.canWrite()) {
                getEObject().setFile(file);
                return save();
            }
            else {
                throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_4, file));
            }
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
     * @param type Type of element
     * @param name Name of element
     * @return The element
     */
    public ArchimateElementProxy addElement(String type, String name) {
        return addElement(type, name, null);
    }
    
    public ArchimateElementProxy addElement(String type, String name, IFolder parentFolder) {
        if(getEObject() == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass)) { // Check this is the correct type
            IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(eClass);
            element.setName(name);
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !ModelUtil.isCorrectFolderForConcept(parentFolder, element)) {
                parentFolder = getArchimateModel().getDefaultFolderForObject(element);
            }

            CommandHandler.executeCommand(new AddElementCommand(parentFolder, element));
            
            return new ArchimateElementProxy(element);
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_0, type));
    }
    
    public ArchimateRelationshipProxy addRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target) {
        return addRelationship(type, name, source, target, null);
    }
    
    public ArchimateRelationshipProxy addRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target, IFolder parentFolder) {
        if(getEObject() == null || source.getEObject() == null || target.getEObject() == null) {
            return null;
        }
        
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(type));
        if(eClass != null && IArchimatePackage.eINSTANCE.getArchimateRelationship().isSuperTypeOf(eClass)) { // Check this is the correct type
            if(!ArchimateModelUtils.isValidRelationship(source.getEObject(), target.getEObject(), eClass)) {
                throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_3, type));
            }

            IArchimateRelationship relationship = (IArchimateRelationship)IArchimateFactory.eINSTANCE.create(eClass);
            relationship.setName(name);
            
            // Check folder is correct for type, if not use default folder
            if(parentFolder == null || !ModelUtil.isCorrectFolderForConcept(parentFolder, relationship)) {
                parentFolder = getArchimateModel().getDefaultFolderForObject(relationship);
            }
            
            CommandHandler.executeCommand(new AddRelationshipCommand(parentFolder, relationship, source.getEObject(), target.getEObject()));
            
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
    protected Object attr(String attribute) {
        if((DOCUMENTATION.equals(attribute) || PURPOSE.equals(attribute)) && getEObject() != null) {
            return getEObject().getPurpose();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        if((DOCUMENTATION.equals(attribute) || PURPOSE.equals(attribute)) && getEObject() != null) {
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.ARCHIMATE_MODEL__PURPOSE, value));
            return this;
        }
        
        return super.attr(attribute, value);
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
