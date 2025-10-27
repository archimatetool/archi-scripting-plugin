/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.model.ModelChecker;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProfile;
import com.archimatetool.model.ModelVersion;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.modelimporter.ImportException;
import com.archimatetool.modelimporter.ModelImporter;
import com.archimatetool.modelimporter.StatusMessage;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.ScriptCommandWrapper;
import com.archimatetool.script.commands.SetCommand;

/**
 * ArchiMate Model object wrapper proxy
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
    
    // ==============================================================================================================================
    // Save model
    // ==============================================================================================================================
    
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
            checkModel();
            IArchiveManager archiveManager = (IArchiveManager)getEObject().getAdapter(IArchiveManager.class);
            archiveManager.saveModel();
        }
        
        return this;
    }
    
    // ==============================================================================================================================
    // Merge (Import) model
    // ==============================================================================================================================
    
    public ArchimateModelProxy merge(String filePath, boolean update, boolean updateAll) throws IOException, ImportException {
        return merge(filePath, update, updateAll, null);
    }
    
    public ArchimateModelProxy merge(String filePath, boolean update, boolean updateAll, List<StatusMessage> messages) throws IOException, ImportException {
        IArchimateModel model = IEditorModelManager.INSTANCE.load(new File(filePath));
        return merge(new ArchimateModelProxy(model), update, updateAll, messages);
    }
    
    public ArchimateModelProxy merge(ArchimateModelProxy modelProxy, boolean update, boolean updateAll) throws IOException, ImportException {
        return merge(modelProxy, update, updateAll, null);
    }
    
    public ArchimateModelProxy merge(ArchimateModelProxy modelProxy, boolean update, boolean updateAll, List<StatusMessage> messages) throws IOException, ImportException {
        ModelImporter importer = new ModelImporter();
        importer.setUpdate(update);
        importer.setUpdateAll(updateAll);
        CommandHandler.executeCommand(new ScriptCommandWrapper(importer.getCommand(modelProxy.getEObject(), getEObject()), getEObject()));
        
        if(messages != null) {
            messages.addAll(importer.getStatusMessages());
        }
        
        return this;
    }
    
    // ==============================================================================================================================
    // Other methods
    // ==============================================================================================================================
    
    /**
     * Return the file path of the model, or null
     */
    public String getPath() {
        return getEObject().getFile() == null ? null : getEObject().getFile().getAbsolutePath();
    }
    
    /**
     * Create and add an ArchiMate element and put in default folder
     */
    public ArchimateElementProxy createElement(String type, String name) {
        return ModelFactory.createElement(getEObject(), type, name, null);
    }
    
    /**
     * Create and add an ArchiMate element and put in folder
     */
    public ArchimateElementProxy createElement(String type, String name, FolderProxy parentFolder) {
        return ModelFactory.createElement(getEObject(), type, name, parentFolder.getEObject());
    }
    
    /**
     * Create and add an ArchiMate relationship and put in default folder
     */
    public ArchimateRelationshipProxy createRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target) {
        return ModelFactory.createRelationship(getEObject(), type, name, source.getEObject(), target.getEObject(), null);
    }
    
    /**
     * Create and add an ArchiMate relationship and put in folder
     */
    public ArchimateRelationshipProxy createRelationship(String type, String name, ArchimateConceptProxy source, ArchimateConceptProxy target, FolderProxy parentFolder) {
        return ModelFactory.createRelationship(getEObject(), type, name, source.getEObject(), target.getEObject(), parentFolder.getEObject());
    }
    
    /**
     * Create and add an ArchiMate View and put in default folder
     */
    public ArchimateDiagramModelProxy createArchimateView(String name) {
        return (ArchimateDiagramModelProxy)ModelFactory.createView(getEObject(), VIEW_ARCHIMATE, name, null);
    }

    /**
     * Create and add an ArchiMate View and put in specified folder
     */
    public ArchimateDiagramModelProxy createArchimateView(String name, FolderProxy parentFolder) {
        return (ArchimateDiagramModelProxy)ModelFactory.createView(getEObject(), VIEW_ARCHIMATE, name, parentFolder.getEObject());
    }
    
    /**
     * Create and add an Sketch View and put in default folder
     */
    public SketchDiagramModelProxy createSketchView(String name) {
        return (SketchDiagramModelProxy)ModelFactory.createView(getEObject(), VIEW_SKETCH, name, null);
    }

    /**
     * Create and add an Sketch View and put in specified folder
     */
    public SketchDiagramModelProxy createSketchView(String name, FolderProxy parentFolder) {
        return (SketchDiagramModelProxy)ModelFactory.createView(getEObject(), VIEW_SKETCH, name, parentFolder.getEObject());
    }

    /**
     * Create and add a Canvas View and put in default folder
     */
    public CanvasDiagramModelProxy createCanvasView(String name) {
        return (CanvasDiagramModelProxy)ModelFactory.createView(getEObject(), VIEW_CANVAS, name, null);
    }

    /**
     * Create and add an Canvas View and put in specified folder
     */
    public CanvasDiagramModelProxy createCanvasView(String name, FolderProxy parentFolder) {
        return (CanvasDiagramModelProxy)ModelFactory.createView(getEObject(), VIEW_CANVAS, name, parentFolder.getEObject());
    }
    
    /**
     * Create and add a Specialization (Profile) to the model
     * @param conceptType is kebab case
     */
    public ProfileProxy createSpecialization(String name, String conceptType) {
        return createSpecialization(name, conceptType, null);
    }
    
    public ProfileProxy createSpecialization(String name, String conceptType, Map<String, Object> image) {
        return ModelFactory.createProfileProxy(getEObject(), name, conceptType, image);
    }
    
    /**
     * Add an image from an image file to this model's ArchiveManager storage cache.
     * If the image already exists the existing image path is returned.
     * @param filePath The image file path
     * @return The Image Object
     */
    public Map<String, Object> createImage(String filePath) throws IOException {
        String imagePath = ModelUtil.getArchiveManager(getEObject()).addImageFromFile(new File(filePath));
        return ModelFactory.createImageObject(getEObject(), imagePath);
    }
    
    /**
     * @return a list of all model Specializations (Profiles)
     */
    public List<ProfileProxy> getSpecializations() {
        List<ProfileProxy> list = new ArrayList<>();
        
        for(IProfile profile : getEObject().getProfiles()) {
            list.add(new ProfileProxy(profile));
        }
        
        return list;
    }
    
    /**
     * @return A Specialization (Profile) in the model by name and type, or null
     */
    public ProfileProxy findSpecialization(String name, String conceptType) {
        if(name == null || conceptType == null) {
            return null;
        }
        
        IProfile profile = ArchimateModelUtils.getProfileByNameAndType(getEObject(), name, ModelUtil.getCamelCase(conceptType));
        return profile != null ? new ProfileProxy(profile) : null;
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
                if(value instanceof String str) {
                    return setPurpose(str);
                }
        }
        
        return super.attr(attribute, value);
    }
    
    @Override
    protected EObjectProxyCollection children() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(IFolder folder : getEObject().getFolders()) {
            list.add(EObjectProxy.get(folder));
        }
        
        return list;
    }
    
    /**
     * Set the Current Model to this
     * @return
     */
    public ArchimateModelProxy setAsCurrent() {
        CurrentModel.setAsCurrentModel(this);
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
        // If selector is id (#) or concepts, views and folders (*) then filter on all the model's objects
        if(selector != null && (selector.startsWith("#") || selector.equals("*"))) { //$NON-NLS-1$ //$NON-NLS-2$
            return super.find(selector);
        }
        
        // Else, as this is the model we will additionally filter only on concepts, views and folders
        return super.find(selector).filter("*"); //$NON-NLS-1$
    }
    
    /**
     * Check a model by calling the ModelChecker
     */
    private void checkModel() throws IOException {
        // Model Checker
        ModelChecker checker = new ModelChecker(getEObject());
        
        if(!checker.checkAll()) {
            for(String m : checker.getErrorMessages()) {
                String logMessage = "Model Integrity Error.";  //$NON-NLS-1$
                logMessage += " \'" + getEObject().getName() + "\':"; //$NON-NLS-1$ //$NON-NLS-2$
                logMessage += " " + m; //$NON-NLS-1$
                System.err.println(logMessage);
            }

            throw new IOException("Model has lost integrity. Check console for details."); //$NON-NLS-1$
        }
    }
}
