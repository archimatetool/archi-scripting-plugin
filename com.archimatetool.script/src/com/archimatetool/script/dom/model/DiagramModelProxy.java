/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDiagramModelReference;

/**
 * DiagramModel wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelProxy extends EObjectProxy implements IReferencedProxy{
    
    DiagramModelProxy(IDiagramModel dm) {
        super(dm);
    }
    
    @Override
    protected IDiagramModel getEObject() {
        return (IDiagramModel)super.getEObject();
    }
    
    /**
     * @return child node diagram objects of this diagram model
     */
    @Override
    protected EObjectProxyCollection children() {
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

    @Override
    public EObjectProxyCollection objectRefs() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(IDiagramModel dm : getEObject().getArchimateModel().getDiagramModels()) {
            for(IDiagramModelReference dmc : DiagramModelUtils.findDiagramModelReferences(dm, getEObject())) {
                list.add(EObjectProxy.get(dmc));
            }
        }
        
        return list;
    }
    
    @Override
    public EObjectProxyCollection viewRefs() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(IDiagramModel dm : getEObject().getArchimateModel().getDiagramModels()) {
            for(IDiagramModelReference dmc : DiagramModelUtils.findDiagramModelReferences(dm, getEObject())) {
                list.add(EObjectProxy.get(dmc.getDiagramModel()));
            }
        }
        
        return list;
    }
}
