/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;

import com.archimatetool.canvas.model.ICanvasModel;
import com.archimatetool.editor.ui.textrender.TextRenderer;
import com.archimatetool.model.IArchimateDiagramModel;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateModelObject;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IDiagramModelGroup;
import com.archimatetool.model.IDiagramModelNote;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IDiagramModelReference;
import com.archimatetool.model.IDocumentable;
import com.archimatetool.model.IFeatures;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IIdentifier;
import com.archimatetool.model.INameable;
import com.archimatetool.model.IProperties;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.ISketchModel;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.AddPropertyCommand;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.RemovePropertiesCommand;
import com.archimatetool.script.commands.SetCommand;

/**
 * Abstract EObject wrapper proxy
 * 
 * @author Phillip Beauvoir
 * @author jbsarrodie
 */
public abstract class EObjectProxy implements IModelConstants, Comparable<EObjectProxy> {
    
    private EObject fEObject;
    
    /**
     * Factory method for correct type of EObjectProxy
     * @param eObject
     * @return EObjectProxy type or null if not found
     */
    static EObjectProxy get(EObject eObject) {
        if(eObject instanceof IArchimateModel model) {
            return new ArchimateModelProxy(model);
        }
        
        if(eObject instanceof IArchimateElement element) {
            return new ArchimateElementProxy(element);
        }
        
        if(eObject instanceof IArchimateRelationship relation) {
            return new ArchimateRelationshipProxy(relation);
        }
        
        if(eObject instanceof IArchimateDiagramModel dm) {
            return new ArchimateDiagramModelProxy(dm);
        }
        
        if(eObject instanceof ISketchModel sm) {
            return new SketchDiagramModelProxy(sm);
        }
        
        if(eObject instanceof ICanvasModel cm) {
            return new CanvasDiagramModelProxy(cm);
        }
        
        if(eObject instanceof IDiagramModelNote note) {
            return new DiagramModelNoteProxy(note);
        }
        
        if(eObject instanceof IDiagramModelGroup group) {
            return new DiagramModelGroupProxy(group);
        }

        if(eObject instanceof IDiagramModelReference ref) {
            return new DiagramModelReferenceProxy(ref);
        }

        if(eObject instanceof IDiagramModelObject dmo) {
            return new DiagramModelObjectProxy(dmo);
        }
        
        if(eObject instanceof IDiagramModelConnection dmc) {
            return new DiagramModelConnectionProxy(dmc);
        }

        if(eObject instanceof IFolder folder) {
            return new FolderProxy(folder);
        }

        return null;
    }
    
    EObjectProxy(EObject eObject) {
        setEObject(eObject);
    }
    
    protected void setEObject(EObject eObject) {
        fEObject = eObject;
    }
    
    protected EObject getEObject() {
        return fEObject;
    }
    
    /**
     * @return The (possibly) referenced eObject underlying this eObject
     * sub-classes can over-ride and return the underlying eObject
     */
    protected EObject getReferencedEObject() {
        return getEObject();
    }
    
    public ArchimateModelProxy getModel() {
        return (ArchimateModelProxy)get(getArchimateModel());
    }
    
    // Helper method to get the eObject's containing IArchimateModel
    protected IArchimateModel getArchimateModel() {
        EObject o = getEObject();
        while(!(o instanceof IArchimateModel) && o != null) {
            o = o.eContainer();
        }
        return (IArchimateModel)o;
    }
    
    public String getId() {
        return getEObject() instanceof IIdentifier id ? id.getId() : null;
    }

    public String getName() {
        return getEObject() instanceof INameable nameable ? nameable.getName() : null;
    }
    
    public EObjectProxy setName(String name) {
        if(getEObject() instanceof INameable) {
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.NAMEABLE__NAME, name));
        }
        return this;
    }
    
    public String getDocumentation() {
        // Get the referenced object because not all top objects are IDocumentable (diagram objects)
        return getReferencedEObject() instanceof IDocumentable documentable ? documentable.getDocumentation() : null;
    }
    
    public EObjectProxy setDocumentation(String documentation) {
        if(getReferencedEObject() instanceof IDocumentable) { // Get the referenced object because not all top objects are IDocumentable (diagram objects)
            CommandHandler.executeCommand(new SetCommand(getReferencedEObject(), IArchimatePackage.Literals.DOCUMENTABLE__DOCUMENTATION, documentation));
        }
        return this;
    }
    
    /**
     * @return class type of this object
     */
    public String getType() {
        if(getReferencedEObject() != null) {
            return ModelUtil.getKebabCase(getReferencedEObject().eClass().getName());
        }
        
        return null;
    }
    
    /**
     * Delete this object.
     * Sub-classes to implement this
     */
    public void delete() {
        throw new ArchiScriptException(NLS.bind(Messages.EObjectProxy_0, this));
    }
    
    /**
     * Iterate over the descendent contents of this object and add them to a collection.
     * Descendent objects are those supported in {@link EObjectProxy#get(EObject)}
     * @return the collection of descendent objects or an empty list if no child objects.
     */
    protected EObjectProxyCollection find() {
    	EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() == null) {
            return list;
        }

        for(Iterator<EObject> iter = getEObject().eAllContents(); iter.hasNext();) {
            EObject eObject = iter.next();
            EObjectProxy proxy = EObjectProxy.get(eObject);
            if(proxy != null) {
                list.add(proxy);
            }
        }

        return list;
    }
    
    /**
     * Iterate over the contents of this object and filter them by the selector
     * @param selector The selector to filter on. See SelectorFilterFactory
     * @return the collection of matched objects
     */
    protected EObjectProxyCollection find(String selector) {
        return find().filter(selector);
    }
    
    /**
     * TODO: I can't see where/why this is used. Is it needed?
     * @param eObject The eObject to get as an EObjectProxy and added to a collection.
     * @return a collection containing the object or an empty collection if the EObjectProxy is not found.
     */
    protected EObjectProxyCollection find(EObject eObject) {
    	return find(EObjectProxy.get(eObject));
    }
    
    /**
     * Add the given object to a collection so that the EObjectProxyCollection methods can be used on it.
     * In JS this manifests as in this example:
     * <pre>
     *  var object = ...;
     *  var parent = $(object).parent();
     * </pre>
     * 
     * @param object the EObjectProxy to wrap in a collection
     * @return a collection containing the object.
     */
    protected EObjectProxyCollection find(EObjectProxy object) {
    	EObjectProxyCollection list = new EObjectProxyCollection();
    	
    	if(object != null) {
    	    list.add(object);
    	}
    	
    	return list;
    }
    
    /**
     * @return children as collection. Default is an empty list.
     */
    protected EObjectProxyCollection children() {
        return new EObjectProxyCollection();
    }
    
    /**
     * @return parent of this object. Default is the eContainer.
     */
    protected EObjectProxy parent() {
        return getEObject() == null ? null : EObjectProxy.get(getEObject().eContainer());
	}
    
    /**
     * @return all parents of this object as a hierarchy.
     */
    protected EObjectProxyCollection parents() {
        EObjectProxy parent = parent();
        
        if(parent == null || parent.getEObject() instanceof IArchimateModel) {
            return null;
        }

        EObjectProxyCollection list = new EObjectProxyCollection();
        list.add(parent);
        return list.add(list.parents());
    }

    // ========================================= Properties =========================================
    
	/**
     * Return the list of properties' key
     * @return
     */
    public List<String> prop() {
    	return getPropertyKey();
    }
    
    /**
     * Return a property value.
     * If multiple properties exist with the same key, then return only the first one.
     * @param propKey
     * @return
     */
    public String prop(String propKey) {
    	return (String)prop(propKey, false);
    }
    
    /**
     * Return a property value.
     * If multiple properties exist with the same key, then return only
     * the first one (if duplicate=false) or a list with all values
     * (if duplicate=true).
     * @param propKey
     * @param allowDuplicate
     * @return
     */
    public Object prop(String propKey, boolean allowDuplicate) {
    	List<String> propValues = getPropertyValue(propKey);
    	
    	if(propValues.isEmpty()) {
            return null;
    	}
    	else if(allowDuplicate) {
    		return propValues;
    	}
    	else {
    		return propValues.get(0);
    	}
    }
    
    /**
     * Sets a property.
     * Property is updated if it already exists.
     * @param propKey
     * @param propValue
     * @return
     */
    public EObjectProxy prop(String propKey, String propValue) {
    	return prop(propKey, propValue, false);
    }
    
    /**
     * Sets a property.
     * Property is updated if it already exists (if duplicate=false)
     * or added anyway (if duplicate=true).
     * @param propKey
     * @param propValue
     * @param allowDuplicate
     * @return
     */
    public EObjectProxy prop(String propKey, String propValue, boolean allowDuplicate) {
    	return allowDuplicate ? addProperty(propKey, propValue) : addOrUpdateProperty(propKey, propValue);
    }
    
    /**
     * Add a property to this object
     * @param key
     * @param value
     */
    private EObjectProxy addProperty(String key, String value) {
        if(getReferencedEObject() instanceof IProperties properties && key != null && value != null) {
            CommandHandler.executeCommand(new AddPropertyCommand(properties, key, value));
        }
        
        return this;
    }
    
    /**
     * Add the property only if it doesn't already exists, or update it if it does.
     * If this object already has multiple properties matching the key, all of them are updated.
     * @param key
     * @param value
     */
    private EObjectProxy addOrUpdateProperty(String key, String value) {
        if(getReferencedEObject() instanceof IProperties properties && key != null && value != null) {
            boolean updated = false;
            
            for(IProperty prop : properties.getProperties()) {
                if(prop.getKey().equals(key)) {
                    CommandHandler.executeCommand(new SetCommand(prop, IArchimatePackage.Literals.PROPERTY__VALUE, value));
                    updated = true;
                }
            }
            
            if(!updated) {
                addProperty(key, value);
            }
        }
        
        return this;
    }

    /**
     * @return a list of strings containing the list of properties keys. A key appears only once (duplicates are removed)
     */
    private List<String> getPropertyKey() {
        List<String> list = new ArrayList<String>();
        
        if(getReferencedEObject() instanceof IProperties properties) {
            for(IProperty p : properties.getProperties()) {
                if(!list.contains(p.getKey())) {
                    list.add(p.getKey());
                }
            }
        }
        
        return list;
    }
    
    /**
     * @param key
     * @return a list containing the value of property named "key"
     */
    private List<String> getPropertyValue(String key) {
        List<String> list = new ArrayList<String>();
        
        if(getReferencedEObject() instanceof IProperties properties) {
            for(IProperty p : properties.getProperties()) {
                if(p.getKey().equals(key)) {
                    list.add(p.getValue());
                }
            }
        }
        
        return list;
    }
    
    /**
     * Remove all instances of property "key" 
     * @param key
     */
    public EObjectProxy removeProp(String key) {
        return removeProp(key, null);
    }
    
    /**
     * Remove (all instances of) property "key" that matches "value"
     * @param key
     */
    public EObjectProxy removeProp(String key, String value) {
        if(getReferencedEObject() instanceof IProperties properties) {
            List<IProperty> toRemove = new ArrayList<IProperty>();
            
            for(IProperty p : properties.getProperties()) {
                if(p.getKey().equals(key)) {
                    if(value == null || p.getValue().equals(value)) {
                        toRemove.add(p);
                    }
                }
            }
            
            CommandHandler.executeCommand(new RemovePropertiesCommand(properties, toRemove));
        }
        
        return this;
    }
    
    // ========================================= Label Expressions =========================================
    
    public String getLabelExpression() {
        if(TextRenderer.getDefault().isSupportedObject(getEObject())) {
            return TextRenderer.getDefault().getFormatExpression((IArchimateModelObject)getEObject());
        }
        return null;
    }
    
    public EObjectProxy setLabelExpression(String expression) {
        if(TextRenderer.getDefault().isSupportedObject(getEObject())) {
            CommandHandler.executeCommand(new SetCommand((IFeatures)getEObject(), TextRenderer.FEATURE_NAME, expression, "")); //$NON-NLS-1$
        }
        else {
            throw new ArchiScriptException(NLS.bind(Messages.EObjectProxy_1, this));
        }
        
        return this;
    }
    
    public String getLabelValue() {
        if(TextRenderer.getDefault().isSupportedObject(getEObject())) {
            return TextRenderer.getDefault().render((IArchimateModelObject)getEObject());
        }
        
        return ""; //$NON-NLS-1$
    }

    // =====================================================================================================
    
    protected Object attr(String attribute) {
        switch(attribute) {
            case TYPE:
                return getType();

            case ID:
                return getId();

            case NAME:
                return getName();
            
            case DOCUMENTATION:
                return getDocumentation();
                
            case LABEL_EXPRESSION:
                return getLabelExpression();
                
            case LABEL_VALUE:
                return getLabelValue();

            default:
                return null;
        }
    }
    
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case NAME:
                if(value instanceof String s) {
                    return setName(s);
                }
            
            case DOCUMENTATION:
                if(value instanceof String s) {
                    return setDocumentation(s);
                }

            case LABEL_EXPRESSION:
                if(value instanceof String s) {
                    return setLabelExpression(s);
                }
        }
        
        return this;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        
        if(!(obj instanceof EObjectProxy)) {
            return false;
        }
        
        if(getEObject() == null) {
            return false;
        }
        
        return getEObject() == ((EObjectProxy)obj).getEObject();
    }
    
    // Need to use the hashCode of the underlying object because a Java Set will use it for contains()
    @Override
    public int hashCode() {
        return getEObject() == null ? super.hashCode() : getEObject().hashCode();
    }
    
    @Override
    public String toString() {
        return getType() + ": " + getName(); //$NON-NLS-1$
    }

    @Override
    public int compareTo(EObjectProxy o) {
        if(o == null || o.getName() == null || getName() == null) {
            return 0;
        }
        return getName().compareTo(o.getName());
    }
    
    /**
     * @return Internal class that implements interface methods that should not be exposed as public methods
     */
    protected Object getInternal() {
        return null;
    }
}
