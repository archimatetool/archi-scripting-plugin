/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;

import junit.framework.JUnit4TestAdapter;


/**
 * SelectorFilterFactory Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class SelectorFilterFactoryTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SelectorFilterFactoryTests.class);
    }
    
    
    @Test
    public void accept_All() {
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("*");
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection()));
        
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createBounds()));
    }
    
    @Test
    public void accept_Concepts() {
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("concepts");
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));
        
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection()));
    }
    
    @Test
    public void accept_Elements() {
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("elements");
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));

        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection()));
    }
    
    @Test
    public void accept_Relations() {
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("relations");
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));

        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection()));
    }

    @Test
    public void accept_Views() {
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("views");
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));

        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection()));
    }
    
    @Test
    public void accept_ID() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setId("123");
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("#123");
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("#1234");
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_Name() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo");
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(".foo");
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter(".foot");
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_TypeName() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo");
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("business-role.foo");
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("business-actor.foo");
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_Type() {
        IArchimateConcept element = IArchimateFactory.eINSTANCE.createBusinessRole();
        IArchimateConcept relation = IArchimateFactory.eINSTANCE.createAssociationRelationship();
        IDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("business-role");
        assertTrue(filter.accept(element));
        filter = SelectorFilterFactory.INSTANCE.getFilter("business-actor");
        assertFalse(filter.accept(element));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("association-relationship");
        assertTrue(filter.accept(relation));
        filter = SelectorFilterFactory.INSTANCE.getFilter("influence-relationship");
        assertFalse(filter.accept(relation));
    
        filter = SelectorFilterFactory.INSTANCE.getFilter("archimate-diagram-model");
        assertTrue(filter.accept(dm));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("folder");
        assertTrue(filter.accept(folder));
    }

}
