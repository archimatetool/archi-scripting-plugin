/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.script;

import org.eclipse.ui.IStartup;


/**
 * Early Startup class - shakes the plugin to come alive!
 * Implement IStartup so that things are initialised
 * 
 * @author Phillip Beauvoir
 */
public class Startup implements IStartup {

    @Override
    public void earlyStartup() {
        // Register the part listener
        ArchiScriptPlugin.INSTANCE.registerPartListener();
    }

}
