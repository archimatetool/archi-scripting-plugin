/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateComponent;

/**
 * DiagramModel wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelProxy extends EObjectProxy {
    
    public DiagramModelProxy() {
    }
    
    public DiagramModelProxy(IDiagramModel dm) {
        super(dm);
    }
    
    @Override
    protected IDiagramModel getEObject() {
        return (IDiagramModel)super.getEObject();
    }
    
    public EObjectProxyCollection<EObjectProxy> getConcepts() {
        return getConcepts(IArchimateConcept.class);
    }
    
    public EObjectProxyCollection<EObjectProxy> getElements() {
        return getConcepts(IArchimateElement.class);
    }
    
    public EObjectProxyCollection<EObjectProxy> getRelationships() {
        return getConcepts(IArchimateRelationship.class);
    }
    
    private EObjectProxyCollection<EObjectProxy> getConcepts(Class<?> type) {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<EObjectProxy>();
        
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
            case "concepts": //$NON-NLS-1$
                return getConcepts();
            case "elements": //$NON-NLS-1$
                return getElements();
            case "relationships": //$NON-NLS-1$
                return getRelationships();
        }
        
        return super.attr(attribute);
    }
}
