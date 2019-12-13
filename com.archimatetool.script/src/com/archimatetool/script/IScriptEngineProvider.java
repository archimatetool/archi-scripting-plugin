/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.archimatetool.editor.utils.FileUtils;

/**
 * Interface for different types of script engine support
 * 
 * @author Phillip Beauvoir
 */
public interface IScriptEngineProvider {
    
    String EXTENSION_ID = "com.archimatetool.script.scriptEngineProvider"; //$NON-NLS-1$
    
    /**
     * Run the script in the given file
     * @param file The script file
     * @param engine The engine to use. This is the one returned by createScriptEngine()
     * @throws IOException
     * @throws ScriptException
     */
    void run(File file, ScriptEngine engine) throws IOException, ScriptException;
    
    /**
     * @return the unique ID of this provider
     */
    String getID();
    
    /**
     * @return the name of this provider
     */
    String getName();
    
    /**
     * @return the script engine to use
     */
    ScriptEngine createScriptEngine();
    
    /**
     * @return The supported file extensions (with leading ".").
     *         The first in the list will be used as the default when creating new files.
     *         For example ".ajs", ".groovy")
     */
    String[] getSupportedFileExtensions();
    
    /**
     * @return The image to use for a script
     */
    Image getImage();
    
    /**
     * @return The image descriptor to use for a script
     */
    ImageDescriptor getImageDescriptor();
    
    /**
     * @return The URL to a file to use as a "new" template 
     */
    URL getNewFile();
    
    /**
     * Instance methods to access installed Providers
     */
    static class INSTANCE {
        private static Map<String, IScriptEngineProvider> idMap = new HashMap<>();
        private static Map<String, IScriptEngineProvider> extMap = new HashMap<>();
        
        static {
            IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_ID);
            for(IExtension extension : point.getExtensions()) {
                for(IConfigurationElement element : extension.getConfigurationElements()) {
                    try { 
                        IScriptEngineProvider provider = (IScriptEngineProvider)element.createExecutableExtension("class"); //$NON-NLS-1$
                        idMap.put(provider.getID(), provider);
                        Arrays.stream(provider.getSupportedFileExtensions()).forEach(ext -> extMap.put(ext, provider));
                    }
                    catch(CoreException ex) {
                        ex.printStackTrace();
                    } 
                }
            }
        }
        
        public static IScriptEngineProvider getProviderByID(String providerID) {
            IScriptEngineProvider provider = idMap.get(providerID);
            return provider == null ? idMap.get(JSProvider.ID) : provider;
        }
        
        public static IScriptEngineProvider getProviderForFile(File file) {
            if(ScriptFiles.isLinkedFile(file)) {
                try {
                    file = ScriptFiles.resolveLinkFile(file);
                }
                catch(IOException ex) {
                    ex.printStackTrace();
                }
            }
            
            String ext = FileUtils.getFileExtension(file).toLowerCase();
            return extMap.get(ext);
        }
        
        public static List<IScriptEngineProvider> getInstalledProviders() {
            return new ArrayList<IScriptEngineProvider>(idMap.values());
        }
    }
    
}
