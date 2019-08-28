/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IFolder;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DeleteFolderObjectCommand;

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
    protected EObjectProxyCollection children() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        for(IFolder folder : getEObject().getFolders()) {
            list.add(EObjectProxy.get(folder));
        }

        for(EObject eObject : getEObject().getElements()) {
            list.add(EObjectProxy.get(eObject));
        }
        
        return list;
    }
    
    @Override
    public EObjectProxy setName(String name) {
        // Can only rename user folders
        if(isUserFolder()) {
            super.setName(name);
        }
        
        return this;
    }
    
    /**
     * Create a sub-folder
     * @param name
     * @return
     */
    public FolderProxy createFolder(String name) {
        return ModelFactory.createFolder(getEObject(), name);
    }
    
    /**
     * Add conceptProxy to this folder.
     * If conceptProxy already has a parent then conceptProxy is moved to this folder
     * throws ArchiScriptException if incorrect folder type
     * @param conceptProxy
     * @return
     */
    public FolderProxy add(ArchimateConceptProxy conceptProxy) {
        ModelFactory.addObject(getEObject(), conceptProxy.getEObject());
        return this;
    }
    
    /**
     * Add diagramProxy to this folder.
     * If diagramProxy already has a parent then diagramProxy is moved to this folder
     * throws ArchiScriptException if incorrect folder type
     * @param diagramProxy
     * @return
     */
    public FolderProxy add(DiagramModelProxy diagramProxy) {
        ModelFactory.addObject(getEObject(), diagramProxy.getEObject());
        return this;
    }
    
    /**
     * Add folderProxy to this folder.
     * If folderProxy already has a parent then folderProxy is moved to this folder
     * throws ArchiScriptException if incorrect folder type
     * @param folderProxy
     * @return
     */
    public FolderProxy add(FolderProxy folderProxy) {
        ModelFactory.addFolder(getEObject(), folderProxy.getEObject());
        return this;
    }

    @Override
    public void delete() {
        if(!isUserFolder()) {
            return;
        }

        for(EObjectProxy child : children()) {
            child.delete();
        }
        
        if(getEObject().getArchimateModel() != null) {
            CommandHandler.executeCommand(new DeleteFolderObjectCommand(getEObject()));
        }
    }
    
    private boolean isUserFolder() {
        return getEObject().getType() == FolderType.USER;
    }
}
