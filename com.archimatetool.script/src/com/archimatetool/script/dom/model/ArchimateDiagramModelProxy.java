/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.viewpoints.IViewpoint;
import com.archimatetool.model.viewpoints.ViewpointManager;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.SetCommand;

/**
 * ArchimateDiagramModelProxy wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateDiagramModelProxy extends DiagramModelProxy {
    
    ArchimateDiagramModelProxy(IArchimateDiagramModel dm) {
        super(dm);
    }
    
    /**
     * Add an Archimate element to an ArchiMate View and return the diagram object
     */
    public DiagramModelObjectProxy add(ArchimateElementProxy elementProxy, int x, int y, int width, int height) {
        return add(elementProxy, x, y, width, height, false);
    }
    
    /**
     * Add an Archimate element to an ArchiMate View and return the diagram object with nested option
     */
    public DiagramModelObjectProxy add(ArchimateElementProxy elementProxy, int x, int y, int width, int height, boolean autoNest) {
        return ModelFactory.addArchimateDiagramObject(getEObject(), elementProxy.getEObject(), x, y, width, height, autoNest);
    }
    
    /**
     * Add an Archimate connection to ArchiMate objects and return thr diagram connection
     */
    public DiagramModelConnectionProxy add(ArchimateRelationshipProxy relation, DiagramModelComponentProxy source, DiagramModelComponentProxy target) {
        if(!source.isArchimateConcept() || !target.isArchimateConcept()) {
            throw new ArchiScriptException(Messages.DiagramModelProxy_0);
        }
        
        return ModelFactory.addArchimateDiagramConnection(relation.getEObject(), (IDiagramModelArchimateComponent)source.getEObject(),
                (IDiagramModelArchimateComponent)target.getEObject());
    }

    @Override
    protected IArchimateDiagramModel getEObject() {
        return (IArchimateDiagramModel)super.getEObject();
    }
    
    public Map<String, Object> getViewpoint() {
        IViewpoint vp = ViewpointManager.INSTANCE.getViewpoint(getEObject().getViewpoint());
        
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", vp.getID()); //$NON-NLS-1$
        map.put("name", vp.getName()); //$NON-NLS-1$
        
        return map;
    }
    
    public ArchimateDiagramModelProxy setViewpoint(String id) {
        IViewpoint vp = ViewpointManager.INSTANCE.getViewpoint(id);
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.ARCHIMATE_DIAGRAM_MODEL__VIEWPOINT, vp.getID()));
        return this;
    }
    
    public boolean isAllowedConceptForViewpoint(String conceptName) {
        EClass eClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(conceptName));
        if(eClass != null) {
            return ViewpointManager.INSTANCE.isAllowedConceptForDiagramModel(getEObject(), eClass);
        }
        return false;
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case VIEWPOINT:
                return getViewpoint();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case VIEWPOINT:
                if(value instanceof String) {
                    return setViewpoint((String)value);
                }
                break;
        }
        
        return super.attr(attribute, value);
    }

}
