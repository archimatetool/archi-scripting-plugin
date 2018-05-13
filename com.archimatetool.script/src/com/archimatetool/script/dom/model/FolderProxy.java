/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IFolder;

/**
 * DiagramModel wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class FolderProxy extends EObjectProxy {
    
    FolderProxy(IFolder folder) {
        super(folder);
    }
    
    @Override
    protected IFolder getEObject() {
        return (IFolder)super.getEObject();
    }
    
    @Override
    public boolean isFolder() {
        return true;
    }
    
    @Override
    public EObjectProxyCollection<EObjectProxy> children() {
        EObjectProxyCollection<EObjectProxy> list = new EObjectProxyCollection<EObjectProxy>();
        
        for(IFolder folder : getEObject().getFolders()) {
            list.add(EObjectProxy.get(folder));
        }

        for(EObject eObject : getEObject().getElements()) {
            list.add(EObjectProxy.get(eObject));
        }
        
        return list;
    }
    
    @Override
    public EObjectProxy attr(String attribute, Object value) {
        // Can only rename user folders
        if(NAME.equals(attribute) && getEObject().getType() != FolderType.USER) {
            return this;
        }
        
        return super.attr(attribute, value);
    }
}
