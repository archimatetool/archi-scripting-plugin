/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateModel;


/**
 * ArchiMate Model Utils object
 * 
 * @author Phillip Beauvoir
 */
public class ArchimateModelScriptUtils {

    /**
     * Open a model from file and return the ArchimateModelProxy
     * @param file
     * @return The ArchimateModelProxy
     */
    public ArchimateModelProxy openModel(String file) {
        return new ArchimateModelProxy(IEditorModelManager.INSTANCE.openModel(new File(file)));
    }
	
    /**
     * Open a given model and return the ArchimateModelProxy
     * @param model
     * @return The ArchimateModelProxy
     */
    public ArchimateModelProxy openModel(IArchimateModel model) {
        IEditorModelManager.INSTANCE.openModel(model);
        return new ArchimateModelProxy(model);
    }
}
