/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import com.archimatetool.canvas.model.ICanvasModel;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.ISketchModel;

/**
 * DiagramModel wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelProxy extends EObjectProxy {
    
    DiagramModelProxy(IDiagramModel dm) {
        super(dm);
    }
    
    @Override
    protected IDiagramModel getEObject() {
        return (IDiagramModel)super.getEObject();
    }
    
    @Override
    public boolean isView() {
        return getEObject() instanceof IArchimateDiagramModel;
    }
    
    @Override
    public boolean isCanvas() {
        return getEObject() instanceof ICanvasModel;
    }
    
    @Override
    public boolean isSketch() {
        return getEObject() instanceof ISketchModel;
    }

    /**
     * @return child node diagram objects of this diagram model
     */
    @Override
    public EObjectProxyCollection children() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() == null) {
            return list;
        }
        
        // Immediate children IDiagramModelObject
        for(IDiagramModelObject dmo : getEObject().getChildren()) {
            list.add(new DiagramModelObjectProxy(dmo));
        }
        
        // All connections
        for(Iterator<EObject> iter = getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            if(eObject instanceof IDiagramModelConnection) {
                list.add(new DiagramModelConnectionProxy((IDiagramModelConnection)eObject));
            }
        }
        
        return list;
    }
    
    public EObjectProxyCollection getConcepts() {
        return getConcepts(IArchimateConcept.class);
    }
    
    public EObjectProxyCollection getElements() {
        return getConcepts(IArchimateElement.class);
    }
    
    public EObjectProxyCollection getRelationships() {
        return getConcepts(IArchimateRelationship.class);
    }
    
    private EObjectProxyCollection getConcepts(Class<?> type) {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() == null) {
            return list;
        }
        
        for(Iterator<EObject> iter = getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            
            if(eObject instanceof IDiagramModelArchimateComponent) {
                IArchimateConcept concept = ((IDiagramModelArchimateComponent)eObject).getArchimateConcept();
                if(type.isInstance(concept)) {
                    EObjectProxy proxy = EObjectProxy.get(concept);
                    if(!list.contains(proxy)) {
                        list.add(proxy);
                    }
                }
            }
        }
        
        return list;
    }
    
    @Override
    public Object attr(String attribute) {
        switch(attribute) {
            case CHILDREN:
                return children();
            case CONCEPTS:
                return getConcepts();
            case ELEMENTS:
                return getElements();
            case RELATIONSHIPS:
                return getRelationships();
        }
        
        return super.attr(attribute);
    }
}
