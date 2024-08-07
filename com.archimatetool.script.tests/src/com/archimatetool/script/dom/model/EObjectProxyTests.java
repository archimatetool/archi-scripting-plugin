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
    
    protected IArchimateModelObject testEObject;
    protected EObjectProxy testProxy;
    
    @BeforeAll
    public static void ensureDefaultDisplay() {
        if(Display.getCurrent() == null) {
            Display.getDefault();
        }
    }
    
    @Test
    public void getEObject() {
        assertSame(testEObject, testProxy.getEObject());
    }

    @Test
    public void getReferencedEObject() {
        assertSame(testProxy.getEObject(), testProxy.getReferencedEObject());
    }
    
    @Test
    public void getModel() {
        assertNotNull(testProxy.getModel());
    }
    
    @Test
    public void getArchimateModel() {
        assertNotNull(testProxy.getArchimateModel());
    }
    
    @Test
    public void getID() {
        testEObject.setId("123");
        assertEquals("123", testProxy.getId());
    }
    
    @Test
    public void setName() {
        testProxy.setName("name");
        assertEquals("name", testProxy.getName());
    }

    @Test
    public void setDocumentation() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IDocumentable);
        
        testProxy.setDocumentation("doc");
        assertEquals("doc", testProxy.getDocumentation());
    }
    
    @Test
    public void getType() {
        assertEquals(ModelUtil.getKebabCase(testProxy.getReferencedEObject().eClass().getName()), testProxy.getType());
    }
    
    @Test
    public void delete() {
        assertThrows(ArchiScriptException.class, () -> {
            testProxy.delete();
        });
    }
    
    @Test
    public void find() {
        EObjectProxyCollection collection = testProxy.find();
        assertTrue(collection.isEmpty());
    }
    
    @Test
    public void find_Selector() {
        EObjectProxyCollection collection = testProxy.find("");
        assertTrue(collection.isEmpty());
    }
    
    @Test
    public void find_EObject() {
        EObjectProxyCollection collection = testProxy.find(testEObject);
        assertEquals(testProxy, collection.get(0));
        assertEquals(testEObject, collection.get(0).getEObject());
    }
    
    @Test
    public void find_EObjectProxy() {
        EObjectProxyCollection collection = testProxy.find(testProxy);
        assertSame(testProxy, collection.get(0));
    }
    
    @Test
    public void children() {
        EObjectProxyCollection collection = testProxy.children();
        assertTrue(collection.isEmpty());
    }
    
    @Test
    public void parent() {
        EObjectProxy object = testProxy.parent();
        assertNull(object);
    }

    @Test
    public void parents() {
        EObjectProxyCollection collection = testProxy.parents();
        assertNull(collection);
    }
    
    @Test
    public void prop() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IProperties);

        List<String> collection = testProxy.prop();
        assertTrue(collection.isEmpty());
        
        IProperties obj = (IProperties)testProxy.getReferencedEObject();

        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value2"));
        
        collection = testProxy.prop();
        
        assertEquals(2, collection.size());
        assertEquals("key1", collection.get(0));
        assertEquals("key2", collection.get(1));
    }
    
    @Test
    public void prop_Key() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IProperties);
        
        String s = testProxy.prop("key");
        assertNull(s);
        
        IProperties obj = (IProperties)testProxy.getReferencedEObject();
        
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value2"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value3"));
        
        assertEquals("value1", testProxy.prop("key1"));
        assertEquals("value3", testProxy.prop("key2"));
    }

    @Test
    public void prop_Key_Duplicate() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IProperties);

        Object prop = testProxy.prop("key", true);
        assertNull(prop);
        
        IProperties obj = (IProperties)testProxy.getReferencedEObject();
        
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value2"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value3"));

        prop = testProxy.prop("key1", false);
        assertEquals("value1", prop);
        
        prop = testProxy.prop("key1", true);
        assertTrue(prop instanceof List<?>);
        assertEquals("value1", ((List<?>)prop).get(0));
        assertEquals("value2", ((List<?>)prop).get(1));
    }
    
    @Test
    public void prop_Key_Value() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IProperties);

        EObjectProxy proxy = testProxy.prop("key", "value");
        assertSame(testProxy, proxy);
        
        IProperties obj = (IProperties)testProxy.getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(1, props.size());
        
        IProperty p = props.get(0);
        assertEquals("key", p.getKey());
        assertEquals("value", p.getValue());
        
        testProxy.prop("key", "value2");
        assertEquals("value2", p.getValue());
        assertEquals(1, props.size());
    }
    
    @Test
    public void prop_Key_Value_Duplicate() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IProperties);

        EObjectProxy proxy = testProxy.prop("key", "value", true);
        assertSame(testProxy, proxy);
        
        IProperties obj = (IProperties)testProxy.getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(1, props.size());
        
        IProperty p = props.get(0);
        assertEquals("key", p.getKey());
        assertEquals("value", p.getValue());
        
        testProxy.prop("key", "value2", true);
        assertEquals(2, props.size());
        p = props.get(1);
        assertEquals("value2", p.getValue());
    }
    
    @Test
    public void removeProp() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IProperties);

        testProxy.prop("key", "value", true);
        testProxy.prop("key", "value2", true);
        
        IProperties obj = (IProperties)testProxy.getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(2, props.size());
        
        testProxy.removeProp("key");
        assertEquals(0, props.size());
    }    
    
    @Test
    public void removeProp_Key_Value() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IProperties);

        testProxy.prop("key", "value", true);
        testProxy.prop("key", "value2", true);
        
        IProperties obj = (IProperties)testProxy.getReferencedEObject();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(2, props.size());
        
        testProxy.removeProp("key", "value");
        assertEquals(1, props.size());
        assertEquals("value2", props.get(0).getValue());
    }
    
    @Test
    public void getLabelExpression() {
        assertEquals(null, testProxy.getLabelExpression());
    }
    
    @Test
    public void setLabelExpression() {
        assertEquals(null, testProxy.getLabelExpression());

        try {
            testProxy.setLabelExpression("${name}");
            assertEquals("${name}", testProxy.getLabelExpression());
            
            testProxy.attr(IModelConstants.LABEL_EXPRESSION, "${documentation}");
            assertEquals("${documentation}", testProxy.attr(IModelConstants.LABEL_EXPRESSION));
        }
        // Will throw exception if this object doesn't support setLabelExpression()
        catch(ArchiScriptException ex) {
            assertEquals(null, testProxy.getLabelExpression());
        }
    }
    
    @Test
    public void getLabelValue() {
        assertEquals("", testProxy.getLabelValue());
        
        try {
            testProxy.setLabelExpression("${name}"); 
            assertEquals(testProxy.getName(), testProxy.getLabelValue());
            assertEquals(testProxy.getName(), testProxy.attr(IModelConstants.LABEL_VALUE));
        }
        // Will throw exception if this object doesn't support setLabelExpression()
        catch(ArchiScriptException ex) {
            assertEquals("", testProxy.getLabelValue());
        }
    }

    @Test
    public void attr_ID() {
        assertEquals(((IIdentifier)testProxy.getEObject()).getId(), testProxy.attr(IModelConstants.ID));
    }
 
    @Test
    public void attr_Type() {
        assertEquals(ModelUtil.getKebabCase(testProxy.getReferencedEObject().eClass().getName()), testProxy.attr(IModelConstants.TYPE));
    }

    @Test
    public void attr_Name() {
        testProxy.attr(IModelConstants.NAME, "foo");
        assertEquals("foo", testProxy.attr(IModelConstants.NAME));
    }

    @Test
    public void attr_Documentation() {
        assumeTrue(testProxy.getReferencedEObject() instanceof IDocumentable);
        
        testProxy.attr(IModelConstants.DOCUMENTATION, "doc");
        assertEquals("doc", testProxy.attr(IModelConstants.DOCUMENTATION));
    }

    @Test
    public void equals() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy.equals(testProxy));
    }
    
    @Test
    public void hashCode_() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy.hashCode() == testEObject.hashCode());
    }

}
