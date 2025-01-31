/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;

import com.archimatetool.editor.model.IArchiveManager;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.util.ArchimateResourceFactory;

/**
 * Helper
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class TestFiles {
    
    public static File TEST_MODEL_FILE_ARCHISURANCE = getTestFile("Archisurance.archimate");
    public static File TEST_MODEL_MERGE = getTestFile("mergetest.archimate");
    public static File TEST_MODEL_MERGE2 = getTestFile("mergetest2.archimate");
    
    public static File getTestDataFolder() {
        return getLocalBundleFolder("com.archimatetool.script.tests", "testdata");
    }
    
    public static File getTestFile(String fileName) {
        return new File(getTestDataFolder(), fileName);
    }

    public static File getLocalBundleFolder(String bundleName, String path) {
        URL url = Platform.getBundle(bundleName).getEntry("/");
        try {
            url = FileLocator.resolve(url);
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        return new File(url.getPath(), path);
    }
    
    public static IArchimateModel loadTestArchimateModel(File file) {
        try {
            Resource resource = ArchimateResourceFactory.createNewResource(file);
            resource.load(null);
            IArchimateModel model = (IArchimateModel)resource.getContents().get(0);
            model.setFile(file);
            model.setAdapter(IArchiveManager.class, IArchiveManager.FACTORY.createArchiveManager(model));
            return model;
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

}
