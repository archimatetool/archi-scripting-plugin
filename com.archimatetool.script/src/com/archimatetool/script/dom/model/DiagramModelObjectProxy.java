/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.osgi.util.NLS;

import com.archimatetool.editor.ArchiPlugin;
import com.archimatetool.editor.diagram.commands.DiagramModelObjectLineStyleCommand;
import com.archimatetool.editor.diagram.commands.DiagramModelObjectOutlineAlphaCommand;
import com.archimatetool.editor.model.commands.AddListMemberCommand;
import com.archimatetool.editor.model.commands.RemoveListMemberCommand;
import com.archimatetool.editor.preferences.IPreferenceConstants;
import com.archimatetool.editor.ui.factory.IArchimateElementUIProvider;
import com.archimatetool.editor.ui.factory.IGraphicalObjectUIProvider;
import com.archimatetool.editor.ui.factory.IObjectUIProvider;
import com.archimatetool.editor.ui.factory.ObjectUIFactory;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IBounds;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelContainer;
import com.archimatetool.model.IDiagramModelImageProvider;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IIconic;
import com.archimatetool.model.ITextAlignment;
import com.archimatetool.model.ITextPosition;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.ChangePositionCommand;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.DeleteDiagramModelObjectCommand;
import com.archimatetool.script.commands.MoveListObjectCommand;
import com.archimatetool.script.commands.ScriptCommandWrapper;
import com.archimatetool.script.commands.SetCommand;

/**
 * Diagram Model Object wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public class DiagramModelObjectProxy extends DiagramModelComponentProxy {
    
    DiagramModelObjectProxy(IDiagramModelObject object) {
        super(object);
    }
    
    /**
     * Add an Archimate element to this diagram object and return the new diagram object
     */
    public DiagramModelObjectProxy add(ArchimateElementProxy elementProxy, int x, int y, int width, int height) {
        if(getEObject() instanceof IDiagramModelContainer parent) {
            return ModelFactory.addArchimateDiagramObject(parent, elementProxy.getEObject(), x, y, width, height, false);
        }
        
        throw new ArchiScriptException(Messages.DiagramModelObjectProxy_0);
    }
    
    /**
     * Create and add a diagram object and return the diagram object proxy
     */
    public DiagramModelObjectProxy createObject(String type, int x, int y, int width, int height) {
        if(getEObject() instanceof IDiagramModelContainer parent) {
            return ModelFactory.createDiagramObject(parent, type, x, y, width, height, false);
        }
        
        throw new ArchiScriptException(Messages.DiagramModelObjectProxy_1);
    }
    
    /**
     * Create and add a view reference to another view and return the diagram object proxy
     */
    public DiagramModelReferenceProxy createViewReference(DiagramModelProxy dmRef, int x, int y, int width, int height) {
        if(getEObject() instanceof IDiagramModelContainer parent) {
            return ModelFactory.createViewReference(parent, dmRef.getEObject(), x, y, width, height, false);
        }
        
        throw new ArchiScriptException(Messages.DiagramModelObjectProxy_1);
    }

    @Override
    protected IDiagramModelObject getEObject() {
        return (IDiagramModelObject)super.getEObject();
    }
    
    @Override
    protected boolean isArchimateConcept() {
        return getEObject() instanceof IDiagramModelArchimateObject;
    }
    
    public Map<String, Object> getBounds() {
        IBounds b = getEObject().getBounds();
        
        Map<String, Object> map = new HashMap<>();
        map.put("x", b.getX()); //$NON-NLS-1$
        map.put("y", b.getY()); //$NON-NLS-1$
        map.put("width", b.getWidth()); //$NON-NLS-1$
        map.put("height", b.getHeight()); //$NON-NLS-1$
        
        return map;
    }
    
    public DiagramModelObjectProxy setBounds(Map<?, ?> map) {
        int x = ModelUtil.getIntValueFromMap(map, "x", getEObject().getBounds().getX()); //$NON-NLS-1$
        int y = ModelUtil.getIntValueFromMap(map, "y", getEObject().getBounds().getY()); //$NON-NLS-1$
        int width = ModelUtil.getIntValueFromMap(map, "width", getEObject().getBounds().getWidth()); //$NON-NLS-1$
        int height = ModelUtil.getIntValueFromMap(map, "height", getEObject().getBounds().getHeight()); //$NON-NLS-1$
        
        if(width == -1) {
            width = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_WIDTH);
        }
        
        if(height == -1) {
            height = ArchiPlugin.INSTANCE.getPreferenceStore().getInt(IPreferenceConstants.DEFAULT_ARCHIMATE_FIGURE_HEIGHT);
        }

        if(width <= 0 || height <= 0) {
            throw new ArchiScriptException(Messages.DiagramModelObjectProxy_3);
        }
        
        IBounds bounds = IArchimateFactory.eINSTANCE.createBounds(x, y, width, height);
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__BOUNDS, bounds));
        
        return this;
    }
    
    // ============================================================================
    
    // Z order
    
    public DiagramModelObjectProxy sendToBack() {
        if(getEObject().eContainer() instanceof IDiagramModelContainer parent) {
            CommandHandler.executeCommand(new MoveListObjectCommand(parent.getChildren(), getEObject(), 0));
        }
        
        return this;
    }
    
    public DiagramModelObjectProxy sendBackward() {
        if(getEObject().eContainer() instanceof IDiagramModelContainer parent) {
            int position = parent.getChildren().indexOf(getEObject());
            if(position > 0) {
                CommandHandler.executeCommand(new MoveListObjectCommand(parent.getChildren(), getEObject(), position - 1));
            }
        }
        
        return this;
    }
    
    public DiagramModelObjectProxy bringToFront() {
        if(getEObject().eContainer() instanceof IDiagramModelContainer parent) {
            CommandHandler.executeCommand(new MoveListObjectCommand(parent.getChildren(), getEObject(), -1));
        }
        
        return this;
    }
    
    public DiagramModelObjectProxy bringForward() {
        if(getEObject().eContainer() instanceof IDiagramModelContainer parent) {
            int position = parent.getChildren().indexOf(getEObject());
            if(position < parent.getChildren().size() - 1) {
                CommandHandler.executeCommand(new MoveListObjectCommand(parent.getChildren(), getEObject(), position + 1));
            }
        }
        
        return this;
    }
    
    public int getIndex() {
        if(getEObject().eContainer() instanceof IDiagramModelContainer parent) {
            return parent.getChildren().indexOf(getEObject());
        }
        return -1;
    }
    
    public DiagramModelObjectProxy setIndex(int position) {
        if(getEObject().eContainer() instanceof IDiagramModelContainer parent) {
            if(position < -1 || position >= parent.getChildren().size()) {
                throw new ArchiScriptException("Index out of bounds"); //$NON-NLS-1$
            }
            
            CommandHandler.executeCommand(new MoveListObjectCommand(parent.getChildren(), getEObject(), position));
        }
        
        return this;
    }
    
    // ============================================================================
    
    /**
     * @return child node diagram objects of this diagram object (if any)
     */
    @Override
    protected EObjectProxyCollection children() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IDiagramModelContainer parent) {
            for(IDiagramModelObject dmo : parent.getChildren()) {
                list.add(EObjectProxy.get(dmo));
            }
        }
        
        return list;
    }
    
    @Override
    protected EObjectProxyCollection find() {
        // We don't include relationships
        EObjectProxyCollection all = super.find();
        return all.filter(IModelConstants.ELEMENT);
    }
    
    public String getFillColor() {
        return getEObject().getFillColor();
    }
    
    public DiagramModelObjectProxy setFillColor(String value) {
        checkColorValue(value); // check correct color value
        // Set color. A null value is allowed
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__FILL_COLOR, value));
        return this;
    }
    
    public String getIconColor() {
        return getEObject().getIconColor();
    }
    
    public DiagramModelObjectProxy setIconColor(String value) {
        // Only for objects that have an icon
        if(ObjectUIFactory.INSTANCE.getProvider(getEObject()) instanceof IGraphicalObjectUIProvider provider && provider.hasIcon()) {
            checkColorValue(value);
            CommandHandler.executeCommand(new SetCommand(getEObject(), IDiagramModelObject.FEATURE_ICON_COLOR, value, IDiagramModelObject.FEATURE_ICON_COLOR_DEFAULT));
        }
        
        return this;
    }
    
    public int getOpacity() {
        return getEObject().getAlpha();
    }
    
    public DiagramModelObjectProxy setOpacity(int value) {
        if(value < 0) {
            value = 0;
        }
        if(value > 255) {
            value = 255;
        }
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_OBJECT__ALPHA, value));
        
        return this;
    }

    public int getOutlineOpacity() {
        return getEObject().getLineAlpha();
    }
    
    public DiagramModelObjectProxy setOutlineOpacity(int value) {
        if(value < 0) {
            value = 0;
        }
        if(value > 255) {
            value = 255;
        }
        
        CommandHandler.executeCommand(new ScriptCommandWrapper(new DiagramModelObjectOutlineAlphaCommand(getEObject(), value), getEObject()));
        
        return this;
    }

    public int getGradient() {
        return getEObject().getGradient();
    }
    
    public DiagramModelObjectProxy setGradient(int value) {
        if(value < -1 || value > 3) {
            value = -1;
        }
        
        if(ModelUtil.shouldExposeFeature(getEObject(), IDiagramModelObject.FEATURE_GRADIENT)) {
            CommandHandler.executeCommand(new SetCommand(getEObject(), IDiagramModelObject.FEATURE_GRADIENT, value, IDiagramModelObject.FEATURE_GRADIENT_DEFAULT));
        }
        
        return this;
    }
    
    public DiagramModelObjectProxy setFigureType(int value) {
        // If this is an ArchiMate type and we support alternate figures for this diagram model object
        if(isArchimateConcept() &&
                ObjectUIFactory.INSTANCE.getProviderForClass(getConcept().getEObject().eClass()) instanceof IArchimateElementUIProvider provider && provider.hasAlternateFigure()) {
            if(value < 0) {
                value = 0;
            }
            if(value > 1) {
                value = 1;
            }          
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_ARCHIMATE_OBJECT__TYPE, value));
        }
        
        return this;
    }
    
    public int getFigureType() {
        if(isArchimateConcept()) {
            return ((IDiagramModelArchimateObject)getEObject()).getType();
        }
        
        return 0;
    }
    
    public DiagramModelObjectProxy setTextAlignment(int alignment) {
        if(alignment != ITextAlignment.TEXT_ALIGNMENT_CENTER
                && alignment != ITextAlignment.TEXT_ALIGNMENT_LEFT
                && alignment != ITextAlignment.TEXT_ALIGNMENT_RIGHT) {
            throw new ArchiScriptException(Messages.DiagramModelObjectProxy_2);
        }
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.TEXT_ALIGNMENT__TEXT_ALIGNMENT, alignment));
        
        return this;
    }
    
    public Object getTextAlignment() {
        return getEObject().getTextAlignment();
    }
    
    public DiagramModelObjectProxy setTextPosition(int position) {
        if(getEObject() instanceof ITextPosition) {
            if(position < ITextPosition.TEXT_POSITION_TOP || position > ITextPosition.TEXT_POSITION_BOTTOM) {
                position = ITextPosition.TEXT_POSITION_TOP;
            }
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.TEXT_POSITION__TEXT_POSITION, position));
        }
        
        return this;
    }
    
    public Object getTextPosition() {
        if(getEObject() instanceof ITextPosition textPositionObject) {
            return textPositionObject.getTextPosition();
        }
        
        return null;
    }
    
    public Object getShowIcon() {
        return getEObject().getIconVisibleState();
    }
    
    public DiagramModelObjectProxy setShowIcon(int value) {
        // Only for objects that have an icon
        if(ObjectUIFactory.INSTANCE.getProvider(getEObject()) instanceof IGraphicalObjectUIProvider provider && provider.hasIcon()) {
            // Bounds checking
            if(value < IDiagramModelObject.ICON_VISIBLE_IF_NO_IMAGE_DEFINED || value > IDiagramModelObject.ICON_VISIBLE_NEVER) {
                value = IDiagramModelObject.FEATURE_ICON_VISIBLE_DEFAULT;
            }
            
            CommandHandler.executeCommand(new SetCommand(getEObject(), IDiagramModelObject.FEATURE_ICON_VISIBLE, value, IDiagramModelObject.FEATURE_ICON_VISIBLE_DEFAULT));
        }
        
        return this;
    }
    
    public Object getImageSource() {
        if(isArchimateConcept() &&
                ModelUtil.shouldExposeFeature(getEObject(), IDiagramModelArchimateObject.FEATURE_IMAGE_SOURCE)) {
            return ((IDiagramModelArchimateObject)getEObject()).getImageSource();
        }
        
        return -1;
    }
    
    public DiagramModelObjectProxy setImageSource(int value) {
        if(isArchimateConcept() &&
                ModelUtil.shouldExposeFeature(getEObject(), IDiagramModelArchimateObject.FEATURE_IMAGE_SOURCE)) {
            
            // Bounds checking
            if(value < IDiagramModelArchimateObject.IMAGE_SOURCE_PROFILE || value > IDiagramModelArchimateObject.IMAGE_SOURCE_CUSTOM) {
                value = IDiagramModelArchimateObject.FEATURE_IMAGE_SOURCE_DEFAULT;
            }
            
            CommandHandler.executeCommand(new SetCommand(getEObject(), IDiagramModelArchimateObject.FEATURE_IMAGE_SOURCE, value, IDiagramModelArchimateObject.FEATURE_IMAGE_SOURCE_DEFAULT));
        }
        
        return this;
    }
    
    public Object getImagePosition() {
        if(getEObject() instanceof IIconic iconic &&
                ModelUtil.shouldExposeFeature(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH.getName())) {
            return iconic.getImagePosition();
        }
        
        return -1;
    }
    
    public DiagramModelObjectProxy setImagePosition(int value) {
        if(getEObject() instanceof IIconic &&
                ModelUtil.shouldExposeFeature(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH.getName())) {
            // Bounds checking
            if(value < IIconic.ICON_POSITION_TOP_LEFT || value > IIconic.ICON_POSITION_FILL) {
                value = IIconic.ICON_POSITION_TOP_LEFT;
            }
            
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.ICONIC__IMAGE_POSITION, value));
        }
        
        return this;
    }
    
    public Map<String, Object> getImage() {
        if(getEObject() instanceof IDiagramModelImageProvider imageProvider &&
                ModelUtil.shouldExposeFeature(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH.getName())) {
            return ModelFactory.createImageObject(getArchimateModel(), imageProvider.getImagePath());
        }
        
        return null;
    }
    
    public DiagramModelObjectProxy setImage(Map<?, ?> map) {
        if(getEObject() instanceof IDiagramModelImageProvider &&
                ModelUtil.shouldExposeFeature(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH.getName())) {
            
            String imagePath = map != null ? ModelUtil.getStringValueFromMap(map, "path", null) : null; //$NON-NLS-1$
            
            // If imagePath is not null check that the ArchiveManager has this image
            if(imagePath != null && !ModelUtil.hasImage(getArchimateModel(), imagePath)) {
                throw new ArchiScriptException(NLS.bind(Messages.ModelFactory_12, imagePath));
            }
            
            CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.DIAGRAM_MODEL_IMAGE_PROVIDER__IMAGE_PATH, imagePath));
        }
        
        return this;
    }
    
    public boolean getDeriveLineColor() {
        IObjectUIProvider provider = ObjectUIFactory.INSTANCE.getProvider(getEObject());
        if(provider != null && provider.shouldExposeFeature(IDiagramModelObject.FEATURE_DERIVE_ELEMENT_LINE_COLOR)) {
            return getEObject().getDeriveElementLineColor();
        }
        
        return false;
    }
    
    public DiagramModelObjectProxy setDeriveLineColor(boolean set) {
        IObjectUIProvider provider = ObjectUIFactory.INSTANCE.getProvider(getEObject());
        if(provider != null && provider.shouldExposeFeature(IDiagramModelObject.FEATURE_DERIVE_ELEMENT_LINE_COLOR)) {
            CommandHandler.executeCommand(new SetCommand(getEObject(), IDiagramModelObject.FEATURE_DERIVE_ELEMENT_LINE_COLOR, set, IDiagramModelObject.FEATURE_DERIVE_ELEMENT_LINE_COLOR_DEFAULT));
        }
        
        return this;
    }
    
    public int getLineStyle() {
        // Get this value from the UIProvider in case it is set to default value
        return (int)ObjectUIFactory.INSTANCE.getProvider(getEObject()).getFeatureValue(IDiagramModelObject.FEATURE_LINE_STYLE);
    }
    
    public DiagramModelObjectProxy setLineStyle(int lineStyle) {
        IObjectUIProvider provider = ObjectUIFactory.INSTANCE.getProvider(getEObject());
        if(provider != null && provider.shouldExposeFeature(IDiagramModelObject.FEATURE_LINE_STYLE)) {
            CommandHandler.executeCommand(new ScriptCommandWrapper(new DiagramModelObjectLineStyleCommand(getEObject(), lineStyle), getEObject()));
        }
        
        return this;
    }
    
    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case BOUNDS:
                return getBounds();
            case INDEX:
                return getIndex();
            case FILL_COLOR:
                return getFillColor();
            case ICON_COLOR:
                return getIconColor();
            case OPACITY:
                return getOpacity();
            case OUTLINE_OPACITY:
                return getOutlineOpacity();
            case GRADIENT:
                return getGradient();
            case FIGURE_TYPE:
                return getFigureType();
            case TEXT_ALIGNMENT:
                return getTextAlignment();
            case TEXT_POSITION:
                return getTextPosition();
            case SHOW_ICON:
                return getShowIcon();
            case IMAGE_SOURCE:
                return getImageSource();
            case IMAGE_POSITION:
                return getImagePosition();
            case IMAGE:
                return getImage();
            case DERIVE_LINE_COLOR:
                return getDeriveLineColor();
            case LINE_STYLE:
                return getLineStyle();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case BOUNDS:
                if(value instanceof Map val) {
                    return setBounds(val);
                }
            case INDEX:
                if(value instanceof Integer val) {
                    return setIndex(val);
                }
            case FILL_COLOR:
                if(value == null || value instanceof String) {
                    return setFillColor((String)value);
                }
            case ICON_COLOR:
                if(value == null || value instanceof String) {
                    return setIconColor((String)value);
                }
            case OPACITY:
                if(value instanceof Integer val) {
                    return setOpacity(val);
                }
            case OUTLINE_OPACITY:
                if(value instanceof Integer val) {
                    return setOutlineOpacity(val);
                }
            case GRADIENT:
                if(value instanceof Integer val) {
                    return setGradient(val);
                }
            case FIGURE_TYPE:
                if(value instanceof Integer val) {
                    return setFigureType(val);
                }
            case TEXT_ALIGNMENT:
                if(value instanceof Integer val) {
                    return setTextAlignment(val);
                }
            case TEXT_POSITION:
                if(value instanceof Integer val) {
                    return setTextPosition(val);
                }
            case SHOW_ICON:
                if(value instanceof Integer val) {
                    return setShowIcon(val);
                }
            case IMAGE_SOURCE:
                if(value instanceof Integer val) {
                    return setImageSource(val);
                }
            case IMAGE_POSITION:
                if(value instanceof Integer val) {
                    return setImagePosition(val);
                }
            case IMAGE:
                if(value instanceof Map val) {
                    return setImage(val);
                }
            case DERIVE_LINE_COLOR:
                if(value instanceof Boolean val) {
                    return setDeriveLineColor(val);
                }
            case LINE_STYLE:
                if(value instanceof Integer val) {
                    return setLineStyle(val);
                }
        }
        
        return super.attr(attribute, value);
    }
    
    @Override
    public void delete() {
        deleteInternal(true);
    }
    
    /**
     * @param deleteChildren If true child objects are deleted as well.
     *                       If false child objects are not deleted but reparented
     */
    public void delete(boolean deleteChildren) {
        if(deleteChildren) {
            deleteInternal(true);
        }
        else {
            deleteAndReparentChildren();
        }
    }

    private void deleteInternal(boolean deleteChildren) {
        for(EObjectProxy rel : inRels()) {
            rel.delete();
        }

        for(EObjectProxy rel : outRels()) {
            rel.delete();
        }
        
        if(deleteChildren) {
            for(EObjectProxy child : children()) {
                child.delete();
            }
        }
        
        if(getEObject().eContainer() != null) {
            CommandHandler.executeCommand(new DeleteDiagramModelObjectCommand(getEObject()));
        }
    }

    /**
     * Delete this object but re-parent child objects to this object's parent
     */
    private void deleteAndReparentChildren() {
        IDiagramModelObject dmo = getEObject();
        
        // Not a container, or no children, or parent not a container
        if(!(dmo instanceof IDiagramModelContainer dmc) || dmc.getChildren().isEmpty()
                || !(dmo.eContainer() instanceof IDiagramModelContainer parent)) {
            return;
        }
        
        IArchimateModel model = getArchimateModel(); // Store this now as it will be null when this is deleted
        
        // Delete this first, it's faster updating the UI
        deleteInternal(false);

        // Iterate thru child objects and move them to the container parent
        for(IDiagramModelObject child : new ArrayList<>(dmc.getChildren())) {
            // Remove child from this
            Command cmd = new RemoveListMemberCommand<>(dmc.getChildren(), child);
            CommandHandler.executeCommand(new ScriptCommandWrapper(cmd, model));
            
            // Adjust x,y position to new parent
            CommandHandler.executeCommand(new ChangePositionCommand(child, dmo.getBounds().getX(), dmo.getBounds().getY()).setModel(model));

            // Add child to new parent
            cmd = new AddListMemberCommand<>(parent.getChildren(), child);
            CommandHandler.executeCommand(new ScriptCommandWrapper(cmd, model));
        }
    }
}
