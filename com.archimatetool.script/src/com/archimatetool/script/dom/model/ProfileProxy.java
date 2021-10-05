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
public class ProfileProxy implements Comparable<ProfileProxy> {
    
    private IProfile profile;

    ProfileProxy(IProfile profile) {
        this.profile = profile;
    }
    
    IProfile getProfile() {
        return profile;
    }
    
    public String getName() {
        return profile.getName();
    }
    
    public ProfileProxy setName(String name) {
        // Sames
        if(profile.getName().equals(name)) {
            return this;
        }
        
        if(!StringUtils.isSetAfterTrim(name)) {
            throw new ArchiScriptException(Messages.ProfileProxy_0);
        }
        
        // Check we don't already have one
        if(ArchimateModelUtils.hasProfileByNameAndType(profile.getArchimateModel(), name, profile.getConceptType())) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_1, name));
        }
        
        CommandHandler.executeCommand(new SetCommand(profile, IArchimatePackage.Literals.NAMEABLE__NAME, name));
        
        return this;
    }
    
    public String getType() {
        return ModelUtil.getKebabCase(profile.getConceptType());
    }
    
    public ProfileProxy setType(String conceptType) {
        // Convert kebab to camel case
        conceptType = ModelUtil.getCamelCase(conceptType);
        
        // Same
        if(profile.getConceptType().equals(conceptType)) {
            return this;
        }
        
        // Check it's the correct type
        if(!ModelUtil.isArchimateConcept(conceptType)) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_11, conceptType));
        }
        
        // Check whether the profile is being used
        if(!ArchimateModelUtils.findProfileUsage(profile).isEmpty()) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_2, profile.getName()));
        }
        
        // Check we don't already have one
        if(ArchimateModelUtils.hasProfileByNameAndType(profile.getArchimateModel(), profile.getName(), conceptType)) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_1, profile.getName()));
        }
        
        CommandHandler.executeCommand(new SetCommand(profile, IArchimatePackage.Literals.PROFILE__CONCEPT_TYPE, conceptType));
        
        // If concept type is a relation or connector remove image if it has one
        if(!ModelUtil.canHaveImage(conceptType)) {
            CommandHandler.executeCommand(new SetCommand(profile, IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH, null));
        }
        
        return this;
    }
    
    public Map<String, Object> getImage() {
        return ModelFactory.createImageObject(profile.getArchimateModel(), profile.getImagePath());
    }
    
    public ProfileProxy setImage(Map<String, Object> map) {
        if(map != null && !ModelUtil.canHaveImage(profile.getConceptType())) {
            throw new ArchiScriptException(NLS.bind(Messages.ProfileProxy_3, profile.getConceptType()));
        }
        
        String imagePath = map != null ? ModelUtil.getStringValueFromMap(map, "path", null) : null; //$NON-NLS-1$
        
        // If imagePath is not null check that the ArchiveManager has this image
        if(imagePath != null && !ModelUtil.hasImage(profile.getArchimateModel(), imagePath)) {
            throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_12, imagePath));
        }

        CommandHandler.executeCommand(new SetCommand(profile, IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH, imagePath));
        
        return this;
    }
    
    public void delete() {
        // Delete Usages first
        for(IProfiles owner : ArchimateModelUtils.findProfileUsage(getProfile())) {
            CommandHandler.executeCommand(new ScriptCommandWrapper(new RemoveListMemberCommand<IProfile>(owner.getProfiles(), profile), profile));
        }

        // Then delete the Profile from the Model
        CommandHandler.executeCommand(new ScriptCommandWrapper(new RemoveListMemberCommand<IProfile>(profile.getArchimateModel().getProfiles(), profile), profile));
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof ProfileProxy)) {
            return false;
        }
        
        if(getProfile() == null) {
            return false;
        }
        
        return getProfile() == ((ProfileProxy)obj).getProfile();
    }
    
    // Need to use the hashCode of the underlying object because a Java Set will use it for contains()
    @Override
    public int hashCode() {
        return getProfile() == null ? super.hashCode() : getProfile().hashCode();
    }
    
    @Override
    public String toString() {
        return getName() + ": " + getType(); //$NON-NLS-1$
    }

    @Override
    public int compareTo(ProfileProxy p) {
        if(p == null || p.getName() == null || getName() == null) {
            return 0;
        }
        return getName().compareTo(p.getName());
    }
}
