/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.diagram.util.DiagramUtils;
import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.ui.ImageFactory;
import com.archimatetool.export.svg.PDFExportProvider;
import com.archimatetool.export.svg.SVGExportProvider;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimatePackage;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.archimatetool.script.ArchiScriptException;

/**
 * Model utility functions
 * 
 * @author Phillip Beauvoir
 */
public class Model {
    
    /**
     * Create a new model
     * @param modelName the name
     * @return the model
     */
    public ArchimateModelProxy create(String modelName) {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        model.setName(modelName);
        
        IArchiveManager archiveManager = IArchiveManager.FACTORY.createArchiveManager(model);
        model.setAdapter(IArchiveManager.class, archiveManager);
        
        // Don't add a CommandStack or other adapters. These will be created if openInUI() is called
        
        return new ArchimateModelProxy(model);
    }
    
    /**
     * Load and open a model
     * @param path
     * @return the model
     */
    public ArchimateModelProxy load(String path) {
        File file = new File(path);
        
        if(PlatformUI.isWorkbenchRunning()) {
            // Already open in UI
            for(IArchimateModel model : IEditorModelManager.INSTANCE.getModels()) {
                if(file.equals(model.getFile())) {
                    return new ArchimateModelProxy(model);
                }
            }
            
            // Load and Open it in UI
            IArchimateModel model = IEditorModelManager.INSTANCE.openModel(file);
            if(model != null) {
                return new ArchimateModelProxy(model);
            }
        }
        // No UI, else load from file
        else {
            IArchimateModel model = IEditorModelManager.INSTANCE.loadModel(file);
            if(model != null) {
                return new ArchimateModelProxy(model);
            }
        }
        
        throw new ArchiScriptException(NLS.bind(Messages.ArchimateModelProxy_2, path));
    }
    
    /**
     * @param modelProxy
     * @return true if modelProxy is loaded in the models tree
     */
    public boolean isModelLoaded(ArchimateModelProxy modelProxy) {
        if(PlatformUI.isWorkbenchRunning() && modelProxy != null) {
            return IEditorModelManager.INSTANCE.getModels()
                                        .stream()
                                        .anyMatch(model -> model.equals(modelProxy.getArchimateModel()));
        }
        
        return false;
    }
    
    /**
     * @return a list of loaded models
     */
    public List<ArchimateModelProxy> getLoadedModels() {
        List<ArchimateModelProxy> models = new ArrayList<ArchimateModelProxy>();
        
        if(PlatformUI.isWorkbenchRunning()) {
            IEditorModelManager.INSTANCE.getModels()
                                        .forEach(model -> models.add(new ArchimateModelProxy(model)));
        }

        return models;
    }
    
    /**
     * @param relationshipType
     * @param sourceType
     * @param targetType
     * @return True if relationship type is allowed between source and target
     */
    public boolean isAllowedRelationship(String relationshipType, String sourceType, String targetType) {
        EClass relClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(relationshipType));
        EClass sourceClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(sourceType));
        EClass targetClass = (EClass)IArchimatePackage.eINSTANCE.getEClassifier(ModelUtil.getCamelCase(targetType));
        
        if(relClass == null || sourceClass == null || targetClass == null) {
            throw new ArchiScriptException("Invalid type name."); //$NON-NLS-1$
        }
        
        return ArchimateModelUtils.isValidRelationship(sourceClass, targetClass, relClass);
    }
    
    
    // ======================================== View Image Handling =====================================================
    
    /**
     * Render a View as a String of BASE64 bytes
     * @param dmProxy The DiagramModelProxy
     * @param formatOne of "PNG", "BMP", "JPG", "JPEG",
     * @return a string encoded in BASE64
     * @throws IOException
     */
    public String renderViewAsBase64(DiagramModelProxy dmProxy, String format) throws IOException {
        return renderViewAsBase64(dmProxy, format, null);
    }
    
    /**
     * Render a View as a String of BASE64 bytes
     * @param dmProxy The DiagramModelProxy
     * @param format One of "PNG", "BMP", "JPG", "JPEG",
     * @param options can be scale and margin insets
     * @return a string encoded in BASE64
     * @throws IOException
     */
    public String renderViewAsBase64(DiagramModelProxy dmProxy, String format, Map<?, ?> options) throws IOException {
        int imgFormat = getImageFormat(format);
        ImageLoader loader = getImageLoader(dmProxy, format, options);

        try(ByteArrayOutputStream stream = new ByteArrayOutputStream(1024)) {
            loader.save(stream, imgFormat);
            Encoder encoder = Base64.getEncoder();
            return encoder.encodeToString(stream.toByteArray());
        }
    }

    /**
     * Render a View as to file
     * @param dmProxy The DiagramModelProxy
     * @param path File path
     * @param format One of "PNG", "BMP", "JPG", "JPEG",
     * @throws IOException
     */
    public void renderViewToFile(DiagramModelProxy dmProxy, String path, String format) {
        renderViewToFile(dmProxy, path, format, null);
    }

    /**
     * Render a View as to file
     * @param dmProxy The DiagramModelProxy
     * @param path File path
     * @param format One of "PNG", "BMP", "JPG", "JPEG",
     * @param options can be scale and margin insets
     * @throws IOException
     */
    public void renderViewToFile(DiagramModelProxy dmProxy, String path, String format, Map<?, ?> options) {
        File file = new File(path);
        createParentFolder(file);
        
        int imgFormat = getImageFormat(format);
        
        ImageLoader loader = getImageLoader(dmProxy, format, options);
        loader.save(file.getPath(), imgFormat);
    }

    /**
     * Render a View as a String of SVG
     * @param dmProxy The DiagramModelProxy
     * @param setViewBox if true sets the viewbox bounds to the bounds of the diagram
     * @return The SVG as a String
     * @throws Exception
     */
    public String renderViewAsSVGString(DiagramModelProxy dmProxy, boolean setViewBox) throws Exception {
        if(dmProxy == null) {
            throw new ArchiScriptException("renderViewAsSVGString - View is null"); //$NON-NLS-1$
        }

        return new SVGExportProvider().getSVGString(dmProxy.getEObject(), setViewBox);
    }
    
    /**
     * Render a View to file in SVG format
     * @param dmProxy The DiagramModelProxy
     * @param path the file to save to
     * @param setViewBox if true sets the viewbox bounds to the bounds of the diagram
     * @throws Exception
     */
    public void renderViewToSVG(DiagramModelProxy dmProxy, String path, boolean setViewBox) throws Exception {
        if(dmProxy == null) {
            throw new ArchiScriptException("renderViewToSVG - View is null"); //$NON-NLS-1$
        }

        new SVGExportProvider().export(dmProxy.getEObject(), new File(path), setViewBox);
    }
    
    /**
     * Render a View to file in PDF format
     * @param dmProxy The DiagramModelProxy
     * @param path the file to save to
     * @throws Exception
     */
    public void renderViewToPDF(DiagramModelProxy dmProxy, String path) throws Exception {
        if(dmProxy == null) {
            throw new ArchiScriptException("renderViewToPDF - View is null"); //$NON-NLS-1$
        }

        new PDFExportProvider().export(dmProxy.getEObject(), new File(path));
    }

    private ImageLoader getImageLoader(DiagramModelProxy dmProxy, String format, Map<?, ?> options) {
        if(dmProxy == null) {
            throw new ArchiScriptException("getImageLoader - View is null"); //$NON-NLS-1$
        }
        
        if(format == null) {
            throw new ArchiScriptException("getImageLoader - Format is null"); //$NON-NLS-1$
        }
        
        // Default options
        int scale = ModelUtil.getIntValueFromMap(options, "scale", 1); //$NON-NLS-1$
        int margin = ModelUtil.getIntValueFromMap(options, "margin", 10); //$NON-NLS-1$
        
        Image image = DiagramUtils.createImage(dmProxy.getEObject(), scale, margin);
        
        try {
            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { image.getImageData(ImageFactory.getImageDeviceZoom()) };
            return loader;
        }
        finally {
            image.dispose();
        }
    }
    
    private int getImageFormat(String format) {
        switch(format.toUpperCase()) {
            case "PNG": //$NON-NLS-1$
            default:
                return SWT.IMAGE_PNG;

            case "BMP": //$NON-NLS-1$
                return SWT.IMAGE_BMP;

            case "GIF": //$NON-NLS-1$
                return SWT.IMAGE_GIF;

            case "JPG": //$NON-NLS-1$
            case "JPEG": //$NON-NLS-1$
                return SWT.IMAGE_JPEG;
        }
    }
    
    /**
     * Ensure parent folder exists by creating it
     */
    private boolean createParentFolder(File file) {
        File parent = file.getParentFile();
        return parent != null ? parent.mkdirs() : false;
    }
}
