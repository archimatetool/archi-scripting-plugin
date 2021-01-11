/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.archimatetool.script.ArchiScriptPlugin;

/**
 * DOM Extension Factory loads DOM objects from extensions
 */
@SuppressWarnings("nls")
public class DomExtensionFactory {
    
    // Extension ID for dom objects
    public static String EXTENSION_ID = "com.archimatetool.script.dom";
    
    /**
     * @return A map of all registered DOM extensions
     */
    public static Map<String, Object> getDOMExtensions() {
        Map<String, Object> map = new HashMap<>();
        
        IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_ID);
        
        for(IExtension extension : point.getExtensions()) {
            for(IConfigurationElement element : extension.getConfigurationElements()) {
                try {
                    //String id = element.getAttribute("id");
                    String variableName = element.getAttribute("variableName");
                    Object domObject = element.createExecutableExtension("class");

                    // If the class object implements IArchiScriptDOMFactory then call its getDOMroot() method to get the object.
                    // Useful if the factory needs to instantiate the dom class object via delefgate or proxy.
                    if(domObject instanceof IArchiScriptDOMFactory) {
                        domObject = ((IArchiScriptDOMFactory)domObject).getDOMroot();
                    }

                    if(variableName != null && domObject != null) {
                        map.put(variableName, domObject);
                    }
                }
                catch(CoreException ex) {
                    ArchiScriptPlugin.INSTANCE.getLog().error("Could not load extension", ex);
                }
            }
        }
        
        return map;
    }
}
