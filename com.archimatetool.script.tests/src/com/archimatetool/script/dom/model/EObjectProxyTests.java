/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.junit.Test;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.testingtools.ArchimateTestModel;
import com.archimatetool.tests.TestData;

import junit.framework.JUnit4TestAdapter;


/**
 * EObjectProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public abstract class EObjectProxyTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(EObjectProxyTests.class);
    }
    
    protected IArchimateModelObject testEObject;
    protected EObjectProxy testProxy;
    
    protected ArchimateModelProxy loadTestModel() {
        ArchimateTestModel tm = new ArchimateTestModel(TestData.TEST_MODEL_FILE_ARCHISURANCE);
        try {
            IArchimateModel model = tm.loadModel();
            return (ArchimateModelProxy)EObjectProxy.get(model);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    @Test
    public void getEObject_IsExpectedObject() {
        assertSame(testEObject, testProxy.getEObject());
    }

    @Test
    public void getReferencedConcept_IsExpectedObject() {
        assertSame(testProxy.getEObject(), testProxy.getReferencedConcept());
    }
    
    @Test
    public void isSet() {
        assertTrue(testProxy.isSet());
    }
    
    @Test
    public void isConcept() {
        assertFalse(testProxy.isConcept());
    }
    
    @Test
    public void isElement() {
        assertFalse(testProxy.isElement());
    }
    
    @Test
    public void isRelationship() {
        assertFalse(testProxy.isRelationship());
    }
    
    @Test
    public void isFolder() {
        assertFalse(testProxy.isFolder());
    }
    
    @Test
    public void isView() {
        assertFalse(testProxy.isView());
    }
    
    @Test
    public void isCanvas() {
        assertFalse(testProxy.isCanvas());
    }
    
    @Test
    public void isSketch() {
        assertFalse(testProxy.isSketch());
    }
    
    @Test
    public void getModel_IsExpectedObject() {
        assertNotNull(testProxy.getModel());
    }
    
    @Test
    public void getID() {
        ((IIdentifier)testEObject).setId("123");
        assertEquals("123", testProxy.getId());
    }
    
    @Test
    public void setName() {
        testProxy.setName("name");
        assertEquals("name", testProxy.getName());
    }

    @Test
    public void setDocumentation() {
        testProxy.setDocumentation("doc");
        assertEquals("doc", testProxy.getDocumentation());
    }
    
    @Test
    public void getType_IsExpected() {
        assertTrue(testProxy.getType() == testProxy.getReferencedConcept().eClass().getName());
    }
    
    @Test(expected = ArchiScriptException.class)
    public void delete_ExceptionThrown() {
        testProxy.delete();
    }
    
    @Test
    public void invoke_ReturnsThis() {
        assertSame(testProxy, testProxy.invoke("foo"));
    }
    
    @Test
    public void find_All() {
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
    public void children_Expected() {
        EObjectProxyCollection collection = testProxy.children();
        assertTrue(collection.isEmpty());
    }
    
    @Test
    public void parent_Expected() {
        EObjectProxy object = testProxy.parent();
        assertNull(object);
    }

    @Test
    public void parents_Expected() {
        EObjectProxyCollection collection = testProxy.parents();
        assertNull(collection);
    }
    
    @Test
    public void prop_Expected() {
        List<String> collection = testProxy.prop();
        assertTrue(collection.isEmpty());
        
        IProperties obj = (IProperties)testProxy.getReferencedConcept();

        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value2"));
        
        collection = testProxy.prop();
        
        assertEquals(2, collection.size());
        assertEquals("key1", collection.get(0));
        assertEquals("key2", collection.get(1));
    }
    
    @Test
    public void prop_String_Expected() {
        String s = testProxy.prop("key");
        assertNull(s);
        
        IProperties obj = (IProperties)testProxy.getReferencedConcept();
        
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value1"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key1", "value2"));
        obj.getProperties().add(IArchimateFactory.eINSTANCE.createProperty("key2", "value3"));
        
        assertEquals("value1", testProxy.prop("key1"));
        assertEquals("value3", testProxy.prop("key2"));
    }

    @Test
    public void prop_String_Duplicate_Expected() {
        Object prop = testProxy.prop("key", true);
        assertNull(prop);
        
        IProperties obj = (IProperties)testProxy.getReferencedConcept();
        
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
    public void prop_Key_Value_Expected() {
        EObjectProxy proxy = testProxy.prop("key", "value");
        assertSame(testProxy, proxy);
        
        IProperties obj = (IProperties)testProxy.getReferencedConcept();
        
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
    public void prop_Key_Value_Duplicate_Expected() {
        EObjectProxy proxy = testProxy.prop("key", "value", true);
        assertSame(testProxy, proxy);
        
        IProperties obj = (IProperties)testProxy.getReferencedConcept();
        
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
    public void removeProp_Expected() {
        testProxy.prop("key", "value", true);
        testProxy.prop("key", "value2", true);
        
        IProperties obj = (IProperties)testProxy.getReferencedConcept();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(2, props.size());
        
        testProxy.removeProp("key");
        assertEquals(0, props.size());
    }    
    
    @Test
    public void removeProp_Value_Expected() {
        testProxy.prop("key", "value", true);
        testProxy.prop("key", "value2", true);
        
        IProperties obj = (IProperties)testProxy.getReferencedConcept();
        
        EList<IProperty> props = obj.getProperties();
        assertEquals(2, props.size());
        
        testProxy.removeProp("key", "value");
        assertEquals(1, props.size());
        assertEquals("value2", props.get(0).getValue());
    }
    
    @Test
    public void attr_Get() {
        assertEquals(((IIdentifier)testProxy.getEObject()).getId(), testProxy.attr(IModelConstants.ID));
        
        assertEquals(((IIdentifier)testProxy.getReferencedConcept()).eClass().getName(), testProxy.attr(IModelConstants.TYPE));
        
        testProxy.setName("foo");
        assertEquals("foo", testProxy.attr(IModelConstants.NAME));

        testProxy.setDocumentation("doc");
        assertEquals("doc", testProxy.attr(IModelConstants.DOCUMENTATION));
        
        assertTrue(testProxy.attr(IModelConstants.CHILDREN) instanceof EObjectProxyCollection);
    }
 
    @Test
    public void attr_Set() {
        EObjectProxy proxy = testProxy.attr(IModelConstants.NAME, "name");
        assertSame(testProxy, proxy);
        
        assertEquals("name", testProxy.getName());
        
        testProxy.attr(IModelConstants.DOCUMENTATION, "doc");
        assertEquals("doc", testProxy.getDocumentation());
    }
    
    @Test
    public void equals_Same() {
        EObjectProxy proxy1 = EObjectProxy.get(testEObject);
        assertTrue(proxy1.equals(testProxy));
    }

}
