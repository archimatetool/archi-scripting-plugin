/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script.dom;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * DomExtensionHelper
 * 
 * @author Phillip Beauvoir
 */
public class DomExtensionHelper {

    /**
     * @param factoryID ID of the factory
     * @return An instance of the class in the extension registry with the given id
     * @throws CoreException
     */
    public static Object getDomObject(String factoryID) throws CoreException {
        
        for(IConfigurationElement configurationElement : Platform.getExtensionRegistry().getConfigurationElementsFor(IArchiScriptDOMFactory.EXTENSION_ID)) {
            String id = configurationElement.getAttribute("id"); //$NON-NLS-1$
            if(id.equals(factoryID)) {
                return configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
            }
        }

        return null;
    }
}
