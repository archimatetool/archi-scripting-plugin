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
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProfile;
import com.archimatetool.script.dom.model.SelectorFilterFactory.ISelectorFilter;


/**
 * SelectorFilterFactory Tests
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class SelectorFilterFactoryTests {
    
    @Test
    public void accept_All() {
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("*");
        assertNotNull(filter);
        
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
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(IModelConstants.CONCEPT);
        assertNotNull(filter);
        
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
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(IModelConstants.ELEMENT);
        assertNotNull(filter);
        
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
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(IModelConstants.RELATION);
        assertNotNull(filter);
        
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
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(IModelConstants.VIEW);
        assertNotNull(filter);
        
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
        assertNotNull(filter);
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("#1234");
        assertNotNull(filter);
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_Name() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo");
        
        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateConcept(concept);

        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter(".foo");
        assertNotNull(filter);

        assertTrue(filter.accept(concept));
        assertTrue(filter.accept(dmo));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter(".foot");
        assertNotNull(filter);
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_TypeName() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo");
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("business-role.foo");
        assertNotNull(filter);
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("business-actor.foo");
        assertNotNull(filter);
        assertFalse(filter.accept(concept));
    }
    
    @Test
    public void accept_TypeName_Specialization() {
        IProfile profile = IArchimateFactory.eINSTANCE.createProfile();
        profile.setName("foo");
        
        // "specialization" is an alias for "profile"
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("specialization.foo");
        assertNotNull(filter);
        assertTrue(filter.accept(profile));
        
        // Also "profile"
        filter = SelectorFilterFactory.INSTANCE.getFilter("profile.foo");
        assertNotNull(filter);
        assertTrue(filter.accept(profile));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("business-actor.foo");
        assertNotNull(filter);
        assertFalse(filter.accept(profile));
    }

    @Test
    public void accept_TypeName_With_Dots() {
        IArchimateConcept concept = IArchimateFactory.eINSTANCE.createBusinessRole();
        concept.setName("foo.bar.pok");
        
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("business-role.foo.bar.pok");
        assertNotNull(filter);
        assertTrue(filter.accept(concept));
        
        filter = SelectorFilterFactory.INSTANCE.getFilter("business-actor.foo.bar.pok");
        assertNotNull(filter);
        assertFalse(filter.accept(concept));
    }

    @Test
    public void accept_Type() {
        IArchimateConcept element = IArchimateFactory.eINSTANCE.createBusinessRole();
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("business-role");
        assertNotNull(filter);
        assertTrue(filter.accept(element));
        filter = SelectorFilterFactory.INSTANCE.getFilter("business-actor");
        assertFalse(filter.accept(element));
        
        IArchimateConcept relation = IArchimateFactory.eINSTANCE.createAssociationRelationship();
        filter = SelectorFilterFactory.INSTANCE.getFilter("association-relationship");
        assertNotNull(filter);
        assertTrue(filter.accept(relation));
        filter = SelectorFilterFactory.INSTANCE.getFilter("influence-relationship");
        assertNotNull(filter);
        assertFalse(filter.accept(relation));
    
        IDiagramModel dm = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        filter = SelectorFilterFactory.INSTANCE.getFilter("archimate-diagram-model");
        assertNotNull(filter);
        assertTrue(filter.accept(dm));
        
        IFolder folder = IArchimateFactory.eINSTANCE.createFolder();
        filter = SelectorFilterFactory.INSTANCE.getFilter("folder");
        assertNotNull(filter);
        assertTrue(filter.accept(folder));
        
        IProfile profile = IArchimateFactory.eINSTANCE.createProfile();
        // "specialization" is an alias for "profile"
        filter = SelectorFilterFactory.INSTANCE.getFilter("specialization");
        assertNotNull(filter);
        assertTrue(filter.accept(profile));
        
        // Also profile
        filter = SelectorFilterFactory.INSTANCE.getFilter("profile");
        assertNotNull(filter);
        assertTrue(filter.accept(profile));
    }

    @Test
    public void referencedObjectNotNull() {
        // Create a DiagramModelArchimateObject without a referenced concept
        // This will test SelectorFilterFactory returning null in getReferencedObject
        IDiagramModelArchimateObject dmao = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        ISelectorFilter filter = SelectorFilterFactory.INSTANCE.getFilter("diagram-model-archimate-object");
        assertNotNull(filter);
        assertFalse(filter.accept(dmao));
    }
}
