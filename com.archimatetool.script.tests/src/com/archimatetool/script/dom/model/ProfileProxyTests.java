/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IBusinessActor;
import com.archimatetool.model.IProfile;
import com.archimatetool.script.ArchiScriptException;


/**
 * ProfileProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ProfileProxyTests {
    
    private IProfile profile, profile2;
    private ProfileProxy proxy, proxy2;
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setAdapter(IArchiveManager.class, IArchiveManager.FACTORY.createArchiveManager(model));
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessActor();
        model.getDefaultFolderForObject(concept).getElements().add(concept);

        profile = IArchimateFactory.eINSTANCE.createProfile();
        profile.setName("Profile");
        profile.setConceptType("BusinessActor");
        profile.setImagePath("imgpath");
        model.getProfiles().add(profile);
        concept.getProfiles().add(profile);
        
        proxy = new ProfileProxy(profile);
        
        profile2 = IArchimateFactory.eINSTANCE.createProfile();
        profile2.setName("Profile");
        profile2.setConceptType("BusinessRole");
        profile.getArchimateModel().getProfiles().add(profile2);
        
        proxy2 = new ProfileProxy(profile2);
    }
    
    @Test
    public void getName() {
        assertEquals("Profile", proxy.getName());
    }
    
    @Test
    public void setName() {
        // Empty string should throw exception
        assertThrows(ArchiScriptException.class, () -> {
            proxy.setName("  ");
        });
        
        // If a Profile exists with the same type and new name should throw exception
        profile2.setName("Profile2");
        profile2.setConceptType("BusinessActor");
        assertThrows(ArchiScriptException.class, () -> {
            proxy.setName("Profile2");
        });
        
        // Check case-insensitive
        assertThrows(ArchiScriptException.class, () -> {
            proxy.setName("ProFiLe2");
        });
        
        // Can change case of name
        proxy.setName("profile");
        assertEquals("profile", proxy.getName());
        
        // Success
        proxy.setName("New Name");
        assertEquals("New Name", proxy.getName());
    }
    
    @Test
    public void getType_IsKebabCase() {
        assertEquals("business-actor", proxy.getType());
    }

    @Test
    public void setType() {
        // Wrong type should throw exception
        assertThrows(ArchiScriptException.class, () -> {
            proxy.setType("bogus");
        });
        
        // In use should throw exception
        assertThrows(ArchiScriptException.class, () -> {
            proxy.setType("business-role");
        });

        // Already has profile of name and type should throw exception
        assertThrows(ArchiScriptException.class, () -> {
            proxy2.setType("business-actor");
        });
        
        // Success
        proxy2.setType("node");
        assertEquals("node", proxy2.getType());
    }
    
    @Test
    public void getImage() {
        assertEquals("imgpath", proxy.getImage().get("path"));
    }
    
    @Test
    public void setImage() {
        // Non existing image should throw exception
        assertThrows(ArchiScriptException.class, () -> {
            Map<String, Object> map = new HashMap<>();
            map.put("path", "imgpath");
            proxy.setImage(map);
        });
        
        // But we can set it to null
        proxy.setImage(null);
        assertNull(proxy.getImage());
    }
    
    @Test
    public void delete() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.getProfiles().add(profile);
        model.setDefaults();
        
        IBusinessActor concept = IArchimateFactory.eINSTANCE.createBusinessActor();
        concept.getProfiles().add(profile);
        model.getDefaultFolderForObject(concept).getElements().add(concept);
        
        proxy.delete();
        
        assertEquals(0, model.getProfiles().size());
        assertEquals(0, concept.getProfiles().size());
    }

    @Test
    public void equals() {
        ProfileProxy proxy2 = new ProfileProxy(profile);
        assertTrue(proxy.equals(proxy2));
    }
    
    @Test
    public void hashCode_() {
        ProfileProxy proxy2 = new ProfileProxy(profile);
        assertTrue(proxy.hashCode() == proxy2.hashCode());
    }
}