/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.archimatetool.script.ArchiScriptException;


/**
 * ArchimateConceptProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public abstract class ArchimateConceptProxyTests extends EObjectProxyTests {
    
    @Override
    protected abstract ArchimateConceptProxy getTestProxy();
    
    @Test
    public void getConcept() {
        assertSame(getTestProxy(), getTestProxy().getConcept());
    }
    
    @Test
    public void setSpecialization() {
        assertNull(getTestProxy().getSpecialization());
        
        // Create Specialization
        getTestProxy().getModel().createSpecialization("Special", ModelUtil.getKebabCase(getTestProxy().getEObject().eClass().getName()), null);
        
        // Now set it
        getTestProxy().setSpecialization("Special");
        assertEquals("Special", getTestProxy().getSpecialization());
        
        getTestProxy().setSpecialization(null);
        assertNull(getTestProxy().getSpecialization());
    }

    @Test
    public void setSpecialization_Exists_But_Wrong_Type() {
        assertNull(getTestProxy().getSpecialization());
        
        // Create Specialization for Node type
        getTestProxy().getModel().createSpecialization("Special", "node", null);
        
        // Try to set it, should throw exception
        assertThrows(ArchiScriptException.class, () -> {
            getTestProxy().setSpecialization("Special");
        });
    }

    @Test
    public void setSpecialization_Not_Exists_Throws_Exception() {
        assertNull(getTestProxy().getSpecialization());

        assertThrows(ArchiScriptException.class, () -> {
            getTestProxy().setSpecialization("Special");
        });
    }

    @Test
    public void setSpecialization_EmptyString_Throws_Exception() {
        assertThrows(ArchiScriptException.class, () -> {
            getTestProxy().setSpecialization("");
        });
    }
}