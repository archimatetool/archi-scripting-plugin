/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IConnectable;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelReference;
import com.archimatetool.model.util.ArchimateModelUtils;


/**
 * ArchimateDiagramModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateDiagramModelProxyTests extends DiagramModelProxyTests {
    
    protected ArchimateModelProxy testModelProxy;
    
    private ArchimateDiagramModelProxy actualTestProxy;
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        testModelProxy = TestsHelper.loadTestModel(TestsHelper.TEST_MODEL_FILE_ARCHISURANCE);
        
        testEObject = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(testModelProxy.getEObject(), "4056");
        testProxy = EObjectProxy.get(testEObject);
        actualTestProxy = (ArchimateDiagramModelProxy)testProxy;
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof ArchimateDiagramModelProxy);
    }

    @Override
    @Test
    public void parent() {
        EObjectProxy parent = actualTestProxy.parent();
        assertEquals("e64e9b49", parent.getId());
    }

    @Override
    @Test
    public void parents() {
        EObjectProxyCollection collection = actualTestProxy.parents();
        assertEquals(1, collection.size());
        assertEquals("e64e9b49", collection.get(0).getId());
    }
    
    @Override
    @Test
    public void find() {
        EObjectProxyCollection collection = actualTestProxy.find();
        assertEquals(65, collection.size());
    }
    
    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        EObjectProxyCollection collection = actualTestProxy.find("garbage");
        assertEquals(0, collection.size());

        collection = actualTestProxy.find("*");
        assertEquals(0, collection.size());
        
        collection = actualTestProxy.find("diagram-model-group");
        assertEquals(7, collection.size());
    }

    @Override
    @Test
    public void children() {
        EObjectProxyCollection collection = actualTestProxy.children();
        assertEquals(35, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelComponentProxy);
        }
    }
    
    @Test
    public void objectRefs() {
        EObjectProxyCollection collection = actualTestProxy.objectRefs();
        assertEquals(1, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelObjectProxy);
            assertTrue(eObjectProxy.getEObject() instanceof IDiagramModelReference);
        }
    }

    @Test
    public void viewRefs() {
        EObjectProxyCollection collection = actualTestProxy.viewRefs();
        assertEquals(1, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelProxy);
            assertTrue(eObjectProxy.getEObject() instanceof IDiagramModel);
        }
    }
    
    @Override
    @Test
    public void delete() {
        assertEquals(35, actualTestProxy.children().size());
        
        assertEquals(1, actualTestProxy.viewRefs().size());

        // Store dm contents
        ArrayList<IDiagramModelComponent> children = new ArrayList<>();
        for(Iterator<EObject> iter = actualTestProxy.getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            if(eObject instanceof IDiagramModelComponent) {
                children.add((IDiagramModelComponent)eObject);
            }
        }
        assertEquals(65, children.size());
        
        actualTestProxy.delete();
        
        assertEquals(0, testProxy.children().size());
        assertNull(testProxy.getModel());
        assertEquals(0, actualTestProxy.viewRefs().size());
        
        for(IDiagramModelComponent eObject : children) {
            assertNull(eObject.eContainer());
            assertNull(eObject.getArchimateModel());
            
            assertEquals(0, ((IConnectable)eObject).getSourceConnections().size());
            assertEquals(0, ((IConnectable)eObject).getTargetConnections().size());
        }
    }
    
    @Test
    public void getViewpoint() {
        Map<String, Object> map = actualTestProxy.getViewpoint();
        assertEquals("layered", map.get("id"));
        assertEquals("Layered", map.get("name"));
        
        actualTestProxy.setViewpoint("implementation_migration");
        map = actualTestProxy.getViewpoint();
        assertEquals("implementation_migration", map.get("id"));
        assertEquals("Implementation and Migration", map.get("name"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void attr_getViewpoint() {
        Map<String, Object> map = (Map<String, Object>)actualTestProxy.attr("viewpoint");
        assertEquals("layered", map.get("id"));
        assertEquals("Layered", map.get("name"));
        
        actualTestProxy.attr("viewpoint", "implementation_migration");
        map = (Map<String, Object>)actualTestProxy.attr("viewpoint");
        assertEquals("implementation_migration", map.get("id"));
        assertEquals("Implementation and Migration", map.get("name"));
    }

    @Test
    public void isAllowedConceptForViewpoint() {
        actualTestProxy.setViewpoint("strategy");
        assertFalse(actualTestProxy.isAllowedConceptForViewpoint("business-actor"));
        assertFalse(actualTestProxy.isAllowedConceptForViewpoint("node"));
        assertFalse(actualTestProxy.isAllowedConceptForViewpoint("location"));
        assertTrue(actualTestProxy.isAllowedConceptForViewpoint("resource"));
        assertTrue(actualTestProxy.isAllowedConceptForViewpoint("outcome"));
    }
}