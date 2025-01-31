/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.File;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.script.TestFiles;

/**
 * Dom Tests Helper
 * 
 * @author Phillip Beauvoir
 */
public class TestsHelper {
    
    /**
     * Load the model from file and return an ArchimateModelProxy for that model
     */
    static ArchimateModelProxy loadTestArchimateModelProxy(File file) {
        return new ArchimateModelProxy(TestFiles.loadTestArchimateModel(file));
    }
    
    /**
     * Create a simple model and return an ArchimateModelProxy for that model
     */
    static ArchimateModelProxy createTestArchimateModelProxy() {
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        return new ArchimateModelProxy(model);
    }
}
