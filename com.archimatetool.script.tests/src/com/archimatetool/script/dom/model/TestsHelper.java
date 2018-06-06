/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom.model;

import java.io.IOException;

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.testingtools.ArchimateTestModel;
import com.archimatetool.tests.TestData;

/**
 * Helper
 * 
 * @author Phillip Beauvoir
 */
public class TestsHelper {
    
    static ArchimateModelProxy loadTestModel() {
        ArchimateTestModel tm = new ArchimateTestModel(TestData.TEST_MODEL_FILE_ARCHISURANCE);
        try {
            IArchimateModel model = tm.loadModel();
            return (ArchimateModelProxy)EObjectProxy.get(model);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

}
