/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;

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
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
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
     * Render a View as a String of BASE64 bytes
     * @param dmProxy The DiagramModelProxy
     * @param format One of "PNG", "BMP", "JPG", "JPEG",
     * @param options can be scale and margin insets
     * @return a string encoded in BASE64
     * @throws IOException
     */
    public String renderViewAsBase64(DiagramModelProxy dmProxy, String format, int... options) throws IOException {
        if(dmProxy == null || format == null) {
            throw new ArchiScriptException("Null argument"); //$NON-NLS-1$
        }
        
        // Default options
        int scale = options.length > 0 ? options[0] : 1;
        int margin = options.length > 1 ? options[1] : 10;
        
        // Format
        int imgFormat = SWT.IMAGE_PNG;
        
        switch(format.toUpperCase()) {
            case "PNG": //$NON-NLS-1$
                imgFormat = SWT.IMAGE_PNG;
                break;

            case "BMP": //$NON-NLS-1$
                imgFormat = SWT.IMAGE_BMP;
                break;

            case "GIF": //$NON-NLS-1$
                imgFormat = SWT.IMAGE_GIF;
                break;

            case "JPG": //$NON-NLS-1$
            case "JPEG": //$NON-NLS-1$
                imgFormat = SWT.IMAGE_JPEG;
                break;

            default:
                break;
        }
        
        Image image = DiagramUtils.createImage(dmProxy.getEObject(), scale, margin);
        
        try {
            ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { image.getImageData(ImageFactory.getDeviceZoom()) };
            
            try(ByteArrayOutputStream stream = new ByteArrayOutputStream(1024)) {
                loader.save(stream, imgFormat);
                
                Encoder encoder = Base64.getEncoder();
                return encoder.encodeToString(stream.toByteArray());
            }
        }
        finally {
            image.dispose();
        }
    }

}
