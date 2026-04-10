/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.IFolder;
import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;


/**
 * SelectorFilterFactory Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class SelectorFilterFactoryTests implements IModelConstants {
    
    @Test
    public void accept_All() {
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter("*").orElseThrow();
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));

        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateConcept(IArchimateFactory.eINSTANCE.createBusinessActor());
        assertFalse(filter.accept(dmo));
        
        IDiagramModelArchimateConnection dmc = IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection();
        dmc.setArchimateConcept(IArchimateFactory.eINSTANCE.createAssignmentRelationship());
        assertFalse(filter.accept(dmc));
        
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createBounds()));
    }
    
    @Test
    public void accept_Concept() {
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter(CONCEPT).orElseThrow();
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));
        
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));

        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateConcept(IArchimateFactory.eINSTANCE.createBusinessActor());
        assertTrue(filter.accept(dmo));
        
        IDiagramModelArchimateConnection dmc = IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection();
        dmc.setArchimateConcept(IArchimateFactory.eINSTANCE.createAssignmentRelationship());
        assertTrue(filter.accept(dmc));
    }
    
    @Test
    public void accept_Element() {
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter(ELEMENT).orElseThrow();
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));

        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));
        
        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateConcept(IArchimateFactory.eINSTANCE.createBusinessActor());
        assertTrue(filter.accept(dmo));
        
        IDiagramModelArchimateConnection dmc = IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection();
        dmc.setArchimateConcept(IArchimateFactory.eINSTANCE.createAssignmentRelationship());
        assertFalse(filter.accept(dmc));
    }
    
    @Test
    public void accept_Relation() {
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter(RELATION).orElseThrow();
        
        assertTrue(filter.accept(IArchimateFactory.eINSTANCE.createAssociationRelationship()));

        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createBusinessRole()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createSketchModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createArchimateDiagramModel()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createFolder()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelGroup()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelNote()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelConnection()));
        assertFalse(filter.accept(IArchimateFactory.eINSTANCE.createDiagramModelReference()));
        
        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateConcept(IArchimateFactory.eINSTANCE.createBusinessActor());
        assertFalse(filter.accept(dmo));
        
        IDiagramModelArchimateConnection dmc = IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection();
        dmc.setArchimateConcept(IArchimateFactory.eINSTANCE.createAssignmentRelationship());
        assertTrue(filter.accept(dmc));
    }

    @Test
    public void accept_View() {
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter(VIEW).orElseThrow();
        
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
        
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter("#123").orElseThrow();
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.getInstance().getFilter("#1234").orElseThrow();
        assertFalse(filter.accept(concept));
        
        filter = SelectorFilterFactory.getInstance().getFilter("#").orElseThrow();
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_Name() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo");
        
        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateConcept(concept);

        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter(".foo").orElseThrow();
        assertTrue(filter.accept(concept));
        assertTrue(filter.accept(dmo));
        
        filter = SelectorFilterFactory.getInstance().getFilter(".foot").orElseThrow();
        assertFalse(filter.accept(concept));
        
        filter = SelectorFilterFactory.getInstance().getFilter(".").orElseThrow();
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_TypeName() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo");
        
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter("business-role.foo").orElseThrow();
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.getInstance().getFilter("business-actor.foo").orElseThrow();
        assertFalse(filter.accept(concept));
        
        filter = SelectorFilterFactory.getInstance().getFilter("something.foo").orElseThrow();
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_TypeName_With_Dots() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo.bar.pok");
        
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter("business-role.foo.bar.pok").orElseThrow();
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.getInstance().getFilter("business-actor.foo.bar.pok").orElseThrow();
        assertFalse(filter.accept(concept));
    }

    @Test
    public void accept_Type() {
        IArchimateConcept element = IArchimateFactory.eINSTANCE.createBusinessRole();
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter("business-role").orElseThrow();
        assertTrue(filter.accept(element));
        filter = SelectorFilterFactory.getInstance().getFilter("business-actor").orElseThrow();
        assertFalse(filter.accept(element));
        
        IArchimateConcept relation = IArchimateFactory.eINSTANCE.createAssociationRelationship();
        filter = SelectorFilterFactory.getInstance().getFilter("association-relationship").orElseThrow();
        assertTrue(filter.accept(relation));
        filter = SelectorFilterFactory.getInstance().getFilter("influence-relationship").orElseThrow();
        assertNotNull(filter);
        assertFalse(filter.accept(relation));
    
        IDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        filter = SelectorFilterFactory.getInstance().getFilter("archimate-diagram-model").orElseThrow();
        assertTrue(filter.accept(dm));
        
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        filter = SelectorFilterFactory.getInstance().getFilter("folder").orElseThrow();
        assertTrue(filter.accept(folder));
    }

    @Test
    public void referencedObjectNotNull() {
        // Create a DiagramModelArchimateObject without a referenced concept
        // This will test SelectorFilterFactory returning null in getReferencedObject
        IDiagramModelArchimateObject dmao = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter("diagram-model-archimate-object").orElseThrow();
        assertFalse(filter.accept(dmao));
    }
    
    @Test
    public void getLegend() {
        // Create a Note
        IDiagramModelNote note = IArchimateFactory.eINSTANCE.createDiagramModelNote();
        
        // This will be accepted
        ISelectorFilter filter = SelectorFilterFactory.getInstance().getFilter(DIAGRAM_MODEL_NOTE).orElseThrow();
        assertTrue(filter.accept(note));
        
        // Set Note to Legend
        note.setIsLegend(true);
        note.setName("Legend");
        
        // Should not return note selector
        filter = SelectorFilterFactory.getInstance().getFilter(DIAGRAM_MODEL_NOTE).orElseThrow();
        assertFalse(filter.accept(note));
        
        // Should return legend selector
        filter = SelectorFilterFactory.getInstance().getFilter(DIAGRAM_MODEL_LEGEND).orElseThrow();
        assertTrue(filter.accept(note));
    }
}
