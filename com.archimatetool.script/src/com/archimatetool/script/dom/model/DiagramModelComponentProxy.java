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
public abstract class DiagramModelComponentProxy extends EObjectProxy {
    
    DiagramModelComponentProxy(IDiagramModelComponent component) {
        super(component);
    }
    
    @Override
    protected IDiagramModelComponent getEObject() {
        return (IDiagramModelComponent)super.getEObject();
    }
    
    public DiagramModelProxy getView() {
        return (DiagramModelProxy)EObjectProxy.get(getEObject().getDiagramModel());
    }
    
    /**
     * @return The ArchiMate component that this diagram component references or null if it does not reference one
     */
    public ArchimateConceptProxy getConcept() {
        return isArchimateConcept() ? (ArchimateConceptProxy)EObjectProxy.get(((IDiagramModelArchimateComponent)getEObject()).getArchimateConcept()) : null;
    }
    
    /**
     * @return true if this diagram component references an ArchiMate component
     */
    protected boolean isArchimateConcept() {
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
    protected EObjectProxyCollection outRels() {
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
    protected EObjectProxyCollection inRels() {
        EObjectProxyCollection list = new EObjectProxyCollection();
        
        if(getEObject() instanceof IConnectable) {
            for(IDiagramModelConnection dmc : ((IConnectable)getEObject()).getTargetConnections()) {
                list.add(new DiagramModelConnectionProxy(dmc));
            }
        }
        
        return list;
    }

    public String getFontColor() {
        String color = ((IFontAttribute)getEObject()).getFontColor();
        return color == null ? "#000000" : color; //$NON-NLS-1$
    }
    
    public DiagramModelComponentProxy setFontColor(String value) {
        // check correct color value
        checkColorValue(value); 
        // Set color. A null value is allowed
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT_COLOR, value));
        return this;
    }

    public String getFontName() {
        return getFontData().getName();
    }
    
    public DiagramModelComponentProxy setFontName(String value) {
        FontData fd = getFontData();
        fd.setName(value);
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT, fd.toString()));
        return this;
    }
    
    public int getFontSize() {
        return getFontData().getHeight();
    }
    
    public DiagramModelComponentProxy setFontSize(int value) {
        FontData fd = getFontData();
        fd.setHeight(value);
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT, fd.toString()));
        return this;
    }
    
    public String getFontStyle() {
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
    
    public DiagramModelComponentProxy setFontStyle(String value) {
        FontData fd = getFontData();
        
        int style = 0;
        if(value != null) {
            if(value.contains("bold")) { //$NON-NLS-1$
                style |= SWT.BOLD; 
            }
            if(value.contains("italic")) { //$NON-NLS-1$
                style |= SWT.ITALIC; 
            }
        }
        fd.setStyle(style);
        
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.FONT_ATTRIBUTE__FONT, fd.toString()));
        return this;
    }
    
    public String getLineColor() {
        String color = ((ILineObject)getEObject()).getLineColor();
        RGB rgb = ColorFactory.convertStringToRGB(color);
        if(rgb == null) {
            rgb = ColorFactory.getDefaultLineColor(getEObject()).getRGB();
        }
        return ColorFactory.convertRGBToString(rgb);
    }
    
    public DiagramModelComponentProxy setLineColor(String value) {
        // check correct color value
        checkColorValue(value); 
        // Set color. A null value is allowed
        CommandHandler.executeCommand(new SetCommand(getEObject(), IArchimatePackage.Literals.LINE_OBJECT__LINE_COLOR, value));
        return this;
    }

    public int getLineWidth() {
        return ((ILineObject)getEObject()).getLineWidth();
    }

    @Override
    protected Object attr(String attribute) {
        switch(attribute) {
            case VIEW:
                return getView();
            case CONCEPT:
                return getConcept();
            case FONT_COLOR:
                return getFontColor();
            case FONT_NAME:
                return getFontName();
            case FONT_SIZE:
                return getFontSize();
            case FONT_STYLE:
                return getFontStyle();
            case LINE_COLOR:
                return getLineColor();
            case LINE_WIDTH:
                return getLineWidth();
        }
        
        return super.attr(attribute);
    }
    
    @Override
    protected EObjectProxy attr(String attribute, Object value) {
        switch(attribute) {
            case FONT_COLOR:
                if(value instanceof String) {
                    return setFontColor((String)value);
                }
                break;
            case FONT_NAME:
                if(value instanceof String) {
                    return setFontName((String)value);
                }
                break;
            case FONT_SIZE:
                if(value instanceof Integer) {
                    return setFontSize((int)value);
                }
                break;
            case FONT_STYLE:
                if(value instanceof String) {
                    return setFontStyle((String)value);
                }
                break;
            case LINE_COLOR:
                if(value == null || value instanceof String) {
                    return setLineColor((String)value);
                }
                break;
        }
        
        return super.attr(attribute, value);
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
        if(value != null && ColorFactory.convertStringToRGB(value) == null) {
            throw new ArchiScriptException(NLS.bind(Messages.DiagramModelComponentProxy_0, value));
        }
    }
    
    @Override
    protected Object getInternal() {
        return new IConnectableProxy() {
            @Override
            public EObjectProxyCollection outRels() {
                return DiagramModelComponentProxy.this.outRels();
            }
            
            @Override
            public EObjectProxyCollection inRels() {
                return DiagramModelComponentProxy.this.inRels();
            }
        };
    }

}
