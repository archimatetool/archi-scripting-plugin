/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
import com.archimatetool.script.ArchiScriptException;


/**
 * EObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public abstract class EObjectProxyTests {
    
    protected abstract IArchimateModelObject getTestEObject();
    protected abstract EObjectProxy getTestProxy();
    
    @BeforeAll
    public static void ensureDefaultDisplay() {
        if(Display.getCurrent() == null) {
            Display.getDefault();
        }
    }
    
    @Test
    public void getEObject() {
        assertSame(getTestEObject(), getTestProxy().getEObject());
    }

    @Test
    public void getReferencedEObject() {
        assertSame(getTestProxy().getEObject(), getTestProxy().getReferencedEObject());
    }
    
    @Test
    public void getModel() {
        assertNotNull(getTestProxy().getModel());
    }
    
    @Test
    public void getArchimateModel() {
        assertNotNull(getTestProxy().getArchimateModel());
    }
    
    @Test
    public void getID() {
        getTestEObject().setId("123");
        assertEquals("123", getTestProxy().getId());
    }
    
    @Test
    public void setName() {
        getTestProxy().setName("name");
        assertEquals("name", getTestProxy().getName());
    }

    @Test
    public void setDocumentation() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IDocumentable);
        
        getTestProxy().setDocumentation("doc");
        assertEquals("doc", getTestProxy().getDocumentation());
    }
    
    @Test
    public void getType() {
        assertEquals(ModelUtil.getKebabCase(getTestProxy().getReferencedEObject().eClass().getName()), getTestProxy().getType());
    }
    
    @Test
    public void delete() {
        assertThrows(ArchiScriptException.class, () -> {
            getTestProxy().delete();
        });
    }
    
    @Test
    public void find() {
        EObjectProxyCollection collection = getTestProxy().find();
        assertTrue(collection.isEmpty());
    }
    
    @Test
    public void find_Selector() {
        EObjectProxyCollection collection = getTestProxy().find("");
        assertTrue(collection.isEmpty());
    }
    
    @Test
    public void find_EObject() {
        EObjectProxyCollection collection = getTestProxy().find(getTestEObject());
        assertEquals(getTestProxy(), collection.get(0));
        assertEquals(getTestEObject(), collection.get(0).getEObject());
    }
    
    @Test
    public void find_EObjectProxy() {
        EObjectProxyCollection collection = getTestProxy().find(getTestProxy());
        assertSame(getTestProxy(), collection.get(0));
    }
    
    @Test
    public void children() {
        EObjectProxyCollection collection = getTestProxy().children();
        assertTrue(collection.isEmpty());
    }
    
    @Test
    public void parent() {
        EObjectProxy object = getTestProxy().parent();
        assertNull(object);
    }

    @Test
    public void parents() {
        EObjectProxyCollection collection = getTestProxy().parents();
        assertNull(collection);
    }
    
    @Test
    public void prop() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IProperties);

        List<String> collection = getTestProxy().prop();
        assertTrue(collection.isEmpty());
        
        IProperties obj = (IProperties)getTestProxy().getReferencedEObject();

        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value2"));
        
        collection = getTestProxy().prop();
        
        assertEquals(2, collection.size());
        assertEquals("key1", collection.get(0));
        assertEquals("key2", collection.get(1));
    }
    
    @Test
    public void prop_Key() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IProperties);
        
        String s = getTestProxy().prop("key");
        assertNull(s);
        
        IProperties obj = (IProperties)getTestProxy().getReferencedEObject();
        
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value2"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value3"));
        
        assertEquals("value1", getTestProxy().prop("key1"));
        assertEquals("value3", getTestProxy().prop("key2"));
    }

    @Test
    public void prop_Key_Duplicate() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IProperties);

        Object prop = getTestProxy().prop("key", true);
        assertNull(prop);
        
        IProperties obj = (IProperties)getTestProxy().getReferencedEObject();
        
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value2"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value3"));

        prop = getTestProxy().prop("key1", false);
        assertEquals("value1", prop);
        
        prop = getTestProxy().prop("key1", true);
        assertTrue(prop instanceof List<?>);
        assertEquals("value1", ((List<?>)prop).get(0));
        assertEquals("value2", ((List<?>)prop).get(1));
    }
    
    @Test
    public void prop_Key_Value() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IProperties);

        EObjectProxy proxy = getTestProxy().prop("key", "value");
        assertSame(getTestProxy(), proxy);
        
        IProperties obj = (IProperties)getTestProxy().getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(1, props.size());
        
        IProperty p = props.get(0);
        assertEquals("key", p.getKey());
        assertEquals("value", p.getValue());
        
        getTestProxy().prop("key", "value2");
        assertEquals("value2", p.getValue());
        assertEquals(1, props.size());
    }
    
    @Test
    public void prop_Key_Value_Duplicate() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IProperties);

        EObjectProxy proxy = getTestProxy().prop("key", "value", true);
        assertSame(getTestProxy(), proxy);
        
        IProperties obj = (IProperties)getTestProxy().getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(1, props.size());
        
        IProperty p = props.get(0);
        assertEquals("key", p.getKey());
        assertEquals("value", p.getValue());
        
        getTestProxy().prop("key", "value2", true);
        assertEquals(2, props.size());
        p = props.get(1);
        assertEquals("value2", p.getValue());
    }
    
    @Test
    public void removeProp() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IProperties);

        getTestProxy().prop("key", "value", true);
        getTestProxy().prop("key", "value2", true);
        
        IProperties obj = (IProperties)getTestProxy().getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(2, props.size());
        
        getTestProxy().removeProp("key");
        assertEquals(0, props.size());
    }    
    
    @Test
    public void removeProp_Key_Value() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IProperties);

        getTestProxy().prop("key", "value", true);
        getTestProxy().prop("key", "value2", true);
        
        IProperties obj = (IProperties)getTestProxy().getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(2, props.size());
        
        getTestProxy().removeProp("key", "value");
        assertEquals(1, props.size());
        assertEquals("value2", props.get(0).getValue());
    }
    
    @Test
    public void getLabelExpression() {
        assertEquals(null, getTestProxy().getLabelExpression());
    }
    
    @Test
    public void setLabelExpression() {
        assertEquals(null, getTestProxy().getLabelExpression());

        try {
            getTestProxy().setLabelExpression("${name}");
            assertEquals("${name}", getTestProxy().getLabelExpression());
            
            getTestProxy().attr(IModelConstants.LABEL_EXPRESSION, "${documentation}");
            assertEquals("${documentation}", getTestProxy().attr(IModelConstants.LABEL_EXPRESSION));
        }
        // Will throw exception if this object doesn't support setLabelExpression()
        catch(ArchiScriptException ex) {
            assertEquals(null, getTestProxy().getLabelExpression());
        }
    }
    
    @Test
    public void getLabelValue() {
        assertEquals("", getTestProxy().getLabelValue());
        
        try {
            getTestProxy().setLabelExpression("${name}"); 
            assertEquals(getTestProxy().getName(), getTestProxy().getLabelValue());
            assertEquals(getTestProxy().getName(), getTestProxy().attr(IModelConstants.LABEL_VALUE));
        }
        // Will throw exception if this object doesn't support setLabelExpression()
        catch(ArchiScriptException ex) {
            assertEquals("", getTestProxy().getLabelValue());
        }
    }

    @Test
    public void attr_ID() {
        assertEquals(((IIdentifier)getTestProxy().getEObject()).getId(), getTestProxy().attr(IModelConstants.ID));
    }
 
    @Test
    public void attr_Type() {
        assertEquals(ModelUtil.getKebabCase(getTestProxy().getReferencedEObject().eClass().getName()), getTestProxy().attr(IModelConstants.TYPE));
    }

    @Test
    public void attr_Name() {
        getTestProxy().attr(IModelConstants.NAME, "foo");
        assertEquals("foo", getTestProxy().attr(IModelConstants.NAME));
    }

    @Test
    public void attr_Documentation() {
        assumeTrue(getTestProxy().getReferencedEObject() instanceof IDocumentable);
        
        getTestProxy().attr(IModelConstants.DOCUMENTATION, "doc");
        assertEquals("doc", getTestProxy().attr(IModelConstants.DOCUMENTATION));
    }

    @Test
    public void equals() {
        EObjectProxy proxy = EObjectProxy.get(getTestEObject());
        assertTrue(proxy.equals(getTestProxy()));
    }
    
    @Test
    public void hashCode_() {
        EObjectProxy proxy = EObjectProxy.get(getTestEObject());
        assertTrue(proxy.hashCode() == getTestEObject().hashCode());
    }

}
