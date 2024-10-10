/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Map;

import org.eclipse.osgi.util.NLS;

import com.archimatetool.editor.model.commands.RemoveListMemberCommand;
import com.archimatetool.editor.utils.StringUtils;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IProfile;
import com.archimatetool.model.IProfiles;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.ScriptCommandWrapper;
import com.archimatetool.script.commands.SetCommand;

/**
 * Proxy wrapper around an IProfile
 * 
 * @author Phillip Beauvoir
 */
public class ProfileProxy extends EObjectProxy {
    
    ProfileProxy(IProfile profile) {
        super(profile);
    }
    
    @Override
    protected IProfile getEObject() {
        return (IProfile)super.getEObject();
    }
    
    @Override
    public EObjectProxy setName(String name) {
        if(!StringUtils.isSetAfterTrim(name)) {
            throw new ArchiScriptException(Messages.ProfileProxy_0);
        }
        
        // Same
        if(getEObject().getName().equals(name)) {
            return this;
        }
        
        // Allowed to change case of this one, but check we don't already have one
        if(!getEObject().getName().equalsIgnoreCase(name) && ArchimateModelUtils.hasProfileByNameAndType(getEObject().getArchimateModel(), name, getEObject().getConceptType())) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_1, name));
        }
        
        return super.setName(name);
    }
    
    @Override
    public String getType() {
        return ModelUtil.getKebabCase(getEObject().getConceptType());
    }
    
    public ProfileProxy setType(String conceptType) {
        // Convert kebab to camel case
        conceptType = ModelUtil.getCamelCase(conceptType);
        
        // Same
        if(getEObject().getConceptType().equals(conceptType)) {
            return this;
        }
        
        // Check it's the correct type
        if(!ModelUtil.isArchimateConcept(conceptType)) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_11, conceptType));
        }
        
        // Check whether the profile is being used
        if(!ArchimateModelUtils.findProfileUsage(getEObject()).isEmpty()) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_2, getEObject().getName()));
        }
        
        // Check we don't already have one
        if(ArchimateModelUtils.hasProfileByNameAndType(getEObject().getArchimateModel(), getEObject().getName(), conceptType)) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_1, getEObject().getName()));
        }
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.PROFILE__CONCEPT_TYPE, conceptType));
        
        // If concept type is a relation or connector remove image if it has one
        if(!ModelUtil.canHaveImage(conceptType)) {
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH, null));
        }
        
        return this;
    }
    
    public Map<String, Object> getImage() {
        return ModelFactory.createImageObject(getEObject().getArchimateModel(), getEObject().getImagePath());
    }
    
    public ProfileProxy setImage(Map<String, Object> map) {
        if(map != null && !ModelUtil.canHaveImage(getEObject().getConceptType())) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_3, getEObject().getConceptType()));
        }
        
        String imagePath = map != null ? ModelUtil.getStringValueFromMap(map, "path", null) : null; //$NON-NLS-1$
        
        // If imagePath is not null check that the ArchiveManager has this image
        if(imagePath != null && !ModelUtil.hasImage(getEObject().getArchimateModel(), imagePath)) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_12, imagePath));
        }

        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH, imagePath));
        
        return this;
    }
    
    @Override
    public void delete() {
        // Delete Usages first
        for(IProfiles owner : ArchimateModelUtils.findProfileUsage(getEObject())) {
            CommandHandler.executeCommand(new ScriptCommandWrapper(new RemoveListMemberCommand<IProfile>(owner.getProfiles(), getEObject()), getEObject()));
        }

        // Then delete the Profile from the Model
        CommandHandler.executeCommand(new ScriptCommandWrapper(new RemoveListMemberCommand<IProfile>(getEObject().getArchimateModel().getProfiles(), getEObject()), getEObject()));
    }
    
    @Override
    public String toString() {
        return getName() + ": " + getType(); //$NON-NLS-1$
    }
}
