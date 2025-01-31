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
import com.archimatetool.script.TestFiles;


/**
 * ArchimateDiagramModelProxy Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class ArchimateDiagramModelProxyTests extends DiagramModelProxyTests {
    
    private IArchimateDiagramModel testEObject;
    private ArchimateDiagramModelProxy testProxy;
    
    @Override
    protected IArchimateDiagramModel getTestEObject() {
        return testEObject;
    }
    
    @Override
    protected ArchimateDiagramModelProxy getTestProxy() {
        return testProxy;
    }
    
    @BeforeEach
    public void runOnceBeforeEachTest() {
        ArchimateModelProxy modelProxy = TestsHelper.loadTestArchimateModelProxy(TestFiles.TEST_MODEL_FILE_ARCHISURANCE);
        testEObject = (IArchimateDiagramModel)ArchimateModelUtils.getObjectByID(modelProxy.getEObject(), "4056");
        testProxy = (ArchimateDiagramModelProxy)EObjectProxy.get(testEObject);
    }

    @Test
    public void get_ReturnsCorrectProxy() {
        EObjectProxy proxy = EObjectProxy.get(testEObject);
        assertTrue(proxy instanceof ArchimateDiagramModelProxy);
    }

    @Override
    @Test
    public void parent() {
        EObjectProxy parent = testProxy.parent();
        assertEquals("e64e9b49", parent.getId());
    }

    @Override
    @Test
    public void parents() {
        EObjectProxyCollection collection = testProxy.parents();
        assertEquals(1, collection.size());
        assertEquals("e64e9b49", collection.get(0).getId());
    }
    
    @Override
    @Test
    public void find() {
        EObjectProxyCollection collection = testProxy.find();
        assertEquals(65, collection.size());
    }
    
    @Override
    @Test
    public void find_Selector() {
        super.find_Selector();
        
        EObjectProxyCollection collection = testProxy.find("garbage");
        assertEquals(0, collection.size());

        collection = testProxy.find("*");
        assertEquals(0, collection.size());
        
        collection = testProxy.find("diagram-model-group");
        assertEquals(7, collection.size());
    }

    @Override
    @Test
    public void children() {
        EObjectProxyCollection collection = testProxy.children();
        assertEquals(35, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelComponentProxy);
        }
    }
    
    @Test
    public void objectRefs() {
        EObjectProxyCollection collection = testProxy.objectRefs();
        assertEquals(1, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelObjectProxy);
            assertTrue(eObjectProxy.getEObject() instanceof IDiagramModelReference);
        }
    }

    @Test
    public void viewRefs() {
        EObjectProxyCollection collection = testProxy.viewRefs();
        assertEquals(1, collection.size());
        
        for(EObjectProxy eObjectProxy : collection) {
            assertTrue(eObjectProxy instanceof DiagramModelProxy);
            assertTrue(eObjectProxy.getEObject() instanceof IDiagramModel);
        }
    }
    
    @Override
    @Test
    public void delete() {
        assertEquals(35, testProxy.children().size());
        
        assertEquals(1, testProxy.viewRefs().size());

        // Store dm contents
        ArrayList<IDiagramModelComponent> children = new ArrayList<>();
        for(Iterator<EObject> iter = testProxy.getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            if(eObject instanceof IDiagramModelComponent) {
                children.add((IDiagramModelComponent)eObject);
            }
        }
        assertEquals(65, children.size());
        
        testProxy.delete();
        
        assertEquals(0, testProxy.children().size());
        assertNull(testProxy.getModel());
        assertEquals(0, testProxy.viewRefs().size());
        
        for(IDiagramModelComponent eObject : children) {
            assertNull(eObject.eContainer());
            assertNull(eObject.getArchimateModel());
            
            assertEquals(0, ((IConnectable)eObject).getSourceConnections().size());
            assertEquals(0, ((IConnectable)eObject).getTargetConnections().size());
        }
    }
    
    @Test
    public void getViewpoint() {
        Map<String, Object> map = testProxy.getViewpoint();
        assertEquals("layered", map.get("id"));
        assertEquals("Layered", map.get("name"));
        
        testProxy.setViewpoint("implementation_migration");
        map = testProxy.getViewpoint();
        assertEquals("implementation_migration", map.get("id"));
        assertEquals("Implementation and Migration", map.get("name"));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void attr_getViewpoint() {
        Map<String, Object> map = (Map<String, Object>)testProxy.attr("viewpoint");
        assertEquals("layered", map.get("id"));
        assertEquals("Layered", map.get("name"));
        
        testProxy.attr("viewpoint", "implementation_migration");
        map = (Map<String, Object>)testProxy.attr("viewpoint");
        assertEquals("implementation_migration", map.get("id"));
        assertEquals("Implementation and Migration", map.get("name"));
    }

    @Test
    public void isAllowedConceptForViewpoint() {
        testProxy.setViewpoint("strategy");
        assertFalse(testProxy.isAllowedConceptForViewpoint("business-actor"));
        assertFalse(testProxy.isAllowedConceptForViewpoint("node"));
        assertFalse(testProxy.isAllowedConceptForViewpoint("location"));
        assertTrue(testProxy.isAllowedConceptForViewpoint("resource"));
        assertTrue(testProxy.isAllowedConceptForViewpoint("outcome"));
    }
}