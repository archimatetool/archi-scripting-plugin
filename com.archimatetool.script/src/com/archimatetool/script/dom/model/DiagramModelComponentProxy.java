/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import com.archimatetool.editor.ui.ColorFactory;
import com.archimatetool.editor.ui.FontFactory;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.IConnectable;
import com.archimatetool.model.IDiagramModelArchimateComponent;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelConnection;
import com.archimatetool.model.IFontAttribute;
import com.archimatetool.model.ILineObject;
import com.archimatetool.script.ArchiScriptException;
import com.archimatetool.script.commands.CommandHandler;
import com.archimatetool.script.commands.SetCommand;

/**
 * Diagram Model Component wrapper proxy
 * 
 * @author Phillip Beauvoir
 */
public abstract class DiagramModelComponentProxy extends EObjectProxy implements IConnectableProxy {
    
    DiagramModelComponentProxy(IDiagramModelComponent component) {
        super(component);
    }
    
    @Override
    protected IDiagramModelComponent getEObject() {
        return (IDiagramModelComponent)super.getEObject();
    }
    
    public DiagramModelProxy getDiagramModel() {
        return new DiagramModelProxy(getEObject().getDiagramModel());
    }
    
    /**
     * @return The ArchiMate component that this diagram component references or null if it does not reference one
     */
    public ArchimateConceptProxy getArchimateConcept() {
        return isArchimateConcept() ? (ArchimateConceptProxy)EObjectProxy.get(((IDiagramModelArchimateComponent)getEObject()).getArchimateConcept()) : null;
    }
    
    /**
     * @return true if this diagram component references an ArchiMate component
     */
    private boolean isArchimateConcept() {
        return getEObject() instanceof IDiagramModelArchimateComponent;
    }
    
    @Override
    protected EObject getReferencedConcept() {
        if(isArchimateConcept()) {
            return ((IDiagramModelArchimateComponent)getEObject()).getArchimateConcept();
        }

        return super.getReferencedConcept();
    }

    /**
     * @return a list of source connections (if any)
     */
    @Override
    public EObjectProxyCollection outRels() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IConnectable) {
            for(IDiagramModelConnection dmc : ((IConnectable)getEObject()).getSourceConnections()) {
                list.add(new DiagramModelConnectionProxy(dmc));
            }
        }
        
        return list;
    }
    
    /**
     * @return a list of target connections (if any)
     */
    @Override
    public EObjectProxyCollection inRels() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IConnectable) {
            for(IDiagramModelConnection dmc : ((IConnectable)getEObject()).getTargetConnections()) {
                list.add(new DiagramModelConnectionProxy(dmc));
            }
        }
        
        return list;
    }

    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case DIAGRAM_MODEL:
                return getDiagramModel();
            case ARCHIMATE_CONCEPT:
                return getArchimateConcept();
            case FONT_COLOR:
                return getFontColor();
            case FONT_NAME:
                return getFontData().getName();
            case FONT_SIZE:
                return getFontData().getHeight();
            case FONT_STYLE:
                return getFontStyleAsString();
            case LINE_COLOR:
                return getLineColor();
            case LINE_WIDTH:
                return ((ILineObject)getEObject()).getLineWidth();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case FONT_COLOR:
                if(value instanceof String) {
                    checkColorValue((String)value); // check correct color value
                }
                // Set color. A null value is allowed
                CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT_COLOR, value));
                break;
            case FONT_NAME:
                if(value instanceof String) {
                    FontData fd = getFontData();
                    fd.setName((String)value);
                    CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT, fd.toString()));
                }
                break;
            case FONT_SIZE:
                if(value instanceof Integer) {
                    FontData fd = getFontData();
                    fd.setHeight((Integer)value);
                    CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT, fd.toString()));
                }
                break;
            case FONT_STYLE:
                if(value instanceof String) {
                    FontData fd = getFontData();
                    fd.setStyle(getFontStyleAsInteger((String)value));
                    CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT, fd.toString()));
                }
                break;
            case LINE_COLOR:
                if(value instanceof String) {
                    checkColorValue((String)value); // check correct color value
                }
                // Set color. A null value is allowed
                CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.LINE_OBJECT__LINE_COLOR, value));
                break;
        }
        
        return super.attr(attribute, value);
    }
    
    protected String getFontColor() {
        String color = ((IFontAttribute)getEObject()).getFontColor();
        return color == null ? "#000000" : color; //$NON-NLS-1$
    }
    
    protected int getFontStyleAsInteger(String style) {
        int s = 0;
        
        if(style != null) {
            if(style.contains("bold")) { //$NON-NLS-1$
                s |= SWT.BOLD; 
            }
            if(style.contains("italic")) { //$NON-NLS-1$
                s |= SWT.ITALIC; 
            }
        }
        
        return s;
    }
    
    protected String getFontStyleAsString() {
        FontData fd = getFontData();
        
        if(fd.getStyle() == 0) {
            return "normal"; //$NON-NLS-1$
        }
        
        String style = ""; //$NON-NLS-1$
        
        if((fd.getStyle() & SWT.BOLD) == SWT.BOLD) {
            style = "bold"; //$NON-NLS-1$
        }
        if((fd.getStyle() & SWT.ITALIC) == SWT.ITALIC) {
            style += "italic"; //$NON-NLS-1$
        }
        
        return style;
    }
    
    protected String getLineColor() {
        String color = ((ILineObject)getEObject()).getLineColor();
        RGB rgb = ColorFactory.convertStringToRGB(color);
        if(rgb == null) {
            rgb = ColorFactory.getDefaultLineColor(getEObject()).getRGB();
        }
        return ColorFactory.convertRGBToString(rgb);
    }

    protected FontData getFontData() {
        FontData fd;
        
        String fontName = ((IFontAttribute)getEObject()).getFont();
        if(fontName != null) {
            fd = new FontData(fontName);
        }
        else {
            fd = new FontData(FontFactory.getDefaultUserViewFontData().toString());
        }
        
        return fd;
    }
    
    protected void checkColorValue(String value) {
        if(ColorFactory.convertStringToRGB(value) == null) {
            throw new ArchiScriptException(NLS.bind(Messages.DiagramModelComponentProxy_0, value));
        }
    }
}
