/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.editor.ui.services.EditorManager;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IConnectable;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDiagramModelReference;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DeleteFolderObjectCommand;
import com.archimatetool.script.commands.DuplicateDiagramModelCommand;
import com.archimatetool.script.commands.SetCommand;

/**
 * DiagramModel wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class DiagramModelProxy extends EObjectProxy {
    
    DiagramModelProxy(IDiagramModel dm) {
        super(dm);
    }
    
    /**
     * Add (move) an existing diagram object to this diagram at x, y and return the diagram object proxy
     */
    public DiagramModelObjectProxy add(DiagramModelObjectProxy dmProxy, int x, int y) {
        ModelFactory.moveDiagramModelObject(getEObject(), dmProxy.getEObject());
        return dmProxy.setBounds(Map.of(BOUNDS_X, x, BOUNDS_Y, y));
    }

    /**
     * Create and add a diagram object and return the diagram object proxy
     */
    public DiagramModelObjectProxy createObject(String type, int x, int y, int width, int height) {
        return createObject(type, x, y, width, height, false);
    }
    
    /**
     * Create and add a diagram object and return the diagram object proxy with nested option
     */
    public DiagramModelObjectProxy createObject(String type, int x, int y, int width, int height, boolean autoNest) {
        return ModelFactory.createDiagramObject(getEObject(), type, x, y, width, height, autoNest);
    }
    
    /**
     * Create and add a view reference to another view and return the diagram object proxy
     */
    public DiagramModelReferenceProxy createViewReference(DiagramModelProxy dmRef, int x, int y, int width, int height) {
        return createViewReference(dmRef, x, y, width, height, false);
    }
    
    /**
     * Create and add a view reference to another view and return the diagram object proxy with nested option
     */
    public DiagramModelReferenceProxy createViewReference(DiagramModelProxy dmRef, int x, int y, int width, int height, boolean autoNest) {
        return ModelFactory.createViewReference(getEObject(), dmRef.getEObject(), x, y, width, height, autoNest);
    }
    
    /**
     * Create and add a plain connection between two diagram components in this view and return the diagram connection
     */
    public DiagramModelConnectionProxy createConnection(DiagramModelComponentProxy source, DiagramModelComponentProxy target) {
        // Ensure that source and target diagram components belong to this diagram model
        if(source.getEObject().getDiagramModel() != getEObject()) {
            throw new ArchiScriptException(Messages.DiagramModelProxy_1);
        }
        if(target.getEObject().getDiagramModel() != getEObject()) {
            throw new ArchiScriptException(Messages.DiagramModelProxy_2);
        }
        
        return ModelFactory.createDiagramConnection((IConnectable)source.getEObject(), (IConnectable)target.getEObject());
    }
    
    /**
     * Open a View in the UI. If Archi is not running it has no effect.
     */
    public DiagramModelProxy openInUI() {
        ModelUtil.openDiagramModelInUI(getEObject());
        return this;
    }
    
    public int getConnectionRouter() {
        int val = getEObject().getConnectionRouterType();
        return val == IDiagramModel.CONNECTION_ROUTER_BENDPOINT ? 0 : 1;
    }
    
    public DiagramModelProxy setConnectionRouter(int val) {
        val = val == 1 ? IDiagramModel.CONNECTION_ROUTER_MANHATTAN : IDiagramModel.CONNECTION_ROUTER_BENDPOINT;
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL__CONNECTION_ROUTER_TYPE, val));
        return this;
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
            list.add(EObjectProxy.get(dmo));
        }
        
        // All connections
        for(Iterator<EObject> iter = getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            if(eObject instanceof IDiagramModelConnection dmc) {
                list.add(new DiagramModelConnectionProxy(dmc));
            }
        }
        
        return list;
    }

    protected EObjectProxyCollection objectRefs() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject().getArchimateModel() != null) {
            for(IDiagramModel dm : getEObject().getArchimateModel().getDiagramModels()) {
                for(IDiagramModelReference ref : DiagramModelUtils.findDiagramModelReferences(dm, getEObject())) {
                    list.add(EObjectProxy.get(ref));
                }
            }
        }
        
        return list;
    }
    
    protected EObjectProxyCollection viewRefs() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject().getArchimateModel() != null) {
            for(IDiagramModel dm : getEObject().getArchimateModel().getDiagramModels()) {
                for(IDiagramModelReference ref : DiagramModelUtils.findDiagramModelReferences(dm, getEObject())) {
                    list.add(EObjectProxy.get(ref.getDiagramModel()));
                }
            }
        }
        
        return list;
    }
    
    @Override
    public void delete() {
        // Delete diagram references first
        for(EObjectProxy proxy : objectRefs()) {
            proxy.delete();
        }

        for(EObjectProxy child : children()) {
            if(child instanceof DiagramModelObjectProxy) { // As children() also contains connections don't delete them here
                child.delete();
            }
        }
        
        if(getEObject().getArchimateModel() != null) {
            if(PlatformUI.isWorkbenchRunning()) {
                EditorManager.closeDiagramEditor(getEObject()); // important!!
            }
            
            CommandHandler.executeCommand(new DeleteFolderObjectCommand(getEObject()));
        }
    }
    
    /**
     * Create a duplicate of this diagram model and put it in the same folder
     */
    public DiagramModelProxy duplicate() {
        return duplicate((FolderProxy)parent());
    }

    /**
     * Create a duplicate of this diagram model and put it in the given parent folder
     */
    public DiagramModelProxy duplicate(FolderProxy parentFolder) {
        // Check for correct folder
        if(!ModelUtil.isCorrectFolderForObject(parentFolder.getEObject(), getEObject())) {
            throw new ArchiScriptException(Messages.ModelFactory_0);
        }
        
        // Check for same model
        ModelUtil.checkComponentsInSameModel(getEObject(), parentFolder.getEObject());
        
        DuplicateDiagramModelCommand command = new DuplicateDiagramModelCommand(getEObject(), parentFolder.getEObject());
        CommandHandler.executeCommand(command);
        return (DiagramModelProxy)EObjectProxy.get(command.getDuplicate());
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case VIEW_CONNECTION_ROUTER:
                return getConnectionRouter();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case VIEW_CONNECTION_ROUTER:
                if(value instanceof Integer val) {
                    setConnectionRouter(val);
                }
        }
        
        return super.attr(attribute, value);
    }

    @Override
    protected Object getInternal() {
        return new IReferencedProxy() {
            @Override
            public EObjectProxyCollection objectRefs() {
                return DiagramModelProxy.this.objectRefs();
            }

            @Override
            public EObjectProxyCollection viewRefs() {
                return DiagramModelProxy.this.viewRefs();
            }
        };
    }
}
